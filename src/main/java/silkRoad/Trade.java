package silkRoad;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import silkRoad.tradingPost.TradingPostObjectEntity;

public class Trade {
    private int id;
    private Point sourceLocation;
    private List<Point> destinations;
    private InventoryItem exportItem;
    private InventoryItem importItem;

    public Trade(Point sourceLocation, String exportItemId, int exportItemAmount,
            String importItemId, int importItemAmount) {
        this.sourceLocation = sourceLocation;
        destinations = new LinkedList<>();
        exportItem = new InventoryItem(exportItemId, exportItemAmount);
        importItem = new InventoryItem(importItemId, importItemAmount);
    }

    public Trade(Point sourceLocation) {
        this(sourceLocation, "oaklog", 200, "coin", 25);
    }

    public void addDestination(Point destinationLocation) {
        destinations.add(destinationLocation);
    }

    public void removeDestination(Point destinationLocation) {
        destinations.remove(destinationLocation);
    }

    @Override
    public String toString() {
        return "Trade{id=" + id + ", src=(" + sourceLocation.x + ", " + sourceLocation.y + "), ["
                + exportItem.item.getStringID() + ", " + exportItem.getAmount() + ") -> ("
                + importItem.item.getStringID() + ", " + importItem.getAmount() + "]" + "}";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public InventoryItem getExportItem() {
        return exportItem;
    }

    public void setExportItem(InventoryItem exportItem) {
        this.exportItem = exportItem;
    }

    public InventoryItem getImportItem() {
        return importItem;
    }

    public void setImportItem(InventoryItem importItem) {
        this.importItem = importItem;
    }

    public boolean isSourcedBy(Point location) {
        return sourceLocation.equals(location);
    }

    public boolean performTrade(Inventory sourceInv, Server server) {
        int sourceAmount = sourceInv.getAmount(null, null, exportItem.item, "trade");
        boolean srcCanTrade = sourceAmount >= exportItem.getAmount();
        if (!srcCanTrade)
            return false;

        for (Point dest : destinations) {
            Level destLevel = server.world.getLevel(new LevelIdentifier(dest, 0));
            List<Inventory> destInvs = destLevel.entityManager.objectEntities.stream()
                    .filter(oe -> oe instanceof TradingPostObjectEntity)
                    .map(oe -> ((TradingPostObjectEntity) oe).inventory).toList();
            Inventory destInv = destInvs.get(0);
            if (destInv.getAmount(null, null, importItem.item, "trade") >= importItem.getAmount()) {
                sourceInv.removeItems(null, null, exportItem.item, exportItem.getAmount(), "trade");
                destInv.removeItems(null, null, importItem.item, importItem.getAmount(), "trade");

                boolean srcHasSpace = sourceInv.canAddItem(null, null, importItem,
                        "trade") == importItem.getAmount();
                boolean destHasSpace = destInv.canAddItem(null, null, exportItem,
                        "trade") == exportItem.getAmount();
                if (srcHasSpace && destHasSpace) {
                    sourceInv.addItem(null, null, importItem.copy(), "trade", null);
                    destInv.addItem(null, null, exportItem.copy(), "trade", null);
                    return true;
                } else {
                    sourceInv.addItem(null, null, exportItem.copy(), "trade", null);
                    destInv.addItem(null, null, importItem.copy(), "trade", null);
                }
            }
        }
        return false;
    }

    public void addSaveData(SaveData data) {
        data.addInt("id", id);
        data.addPoint("srclocation", sourceLocation);
        int[] destinationXCoords = new int[destinations.size()];
        int[] destinationYCoords = new int[destinations.size()];
        int i = 0;
        for (Point destination : destinations) {
            destinationXCoords[i] = destination.x;
            destinationYCoords[i++] = destination.y;
        }
        data.addIntArray("dstx", destinationXCoords);
        data.addIntArray("dsty", destinationYCoords);
        data.addSafeString("exportitem", exportItem.item.getStringID());
        data.addInt("exportamount", exportItem.getAmount());
        data.addSafeString("importitem", importItem.item.getStringID());
        data.addInt("importamount", importItem.getAmount());
    }

    public static Trade fromLoadData(LoadData save) {
        int id = save.getInt("id");
        Point sourceLocation = save.getPoint("srclocation");
        int[] destinationXCoords = save.getIntArray("dstx", new int[0]);
        int[] destinationYCoords = save.getIntArray("dsty", new int[0]);
        String exportitem = save.getSafeString("exportitem");
        int exportAmount = save.getInt("exportamount");
        String importItem = save.getSafeString("importitem");
        int importamount = save.getInt("importamount");

        Trade trade = new Trade(sourceLocation, exportitem, exportAmount, importItem, importamount);
        trade.setId(id);
        for (int i = 0; i < destinationXCoords.length; i++) {
            trade.destinations.add(new Point(destinationXCoords[i], destinationYCoords[i]));
        }
        return trade;
    }

    public void setupContentPacket(PacketWriter writer) {
        writer.putNextInt(id);
        writer.putNextInt(sourceLocation.x);
        writer.putNextInt(sourceLocation.y);
        writer.putNextString(exportItem.item.getStringID());
        writer.putNextInt(exportItem.getAmount());
        writer.putNextString(importItem.item.getStringID());
        writer.putNextInt(importItem.getAmount());
    }

    public static Trade fromPacket(PacketReader reader) {
        int id = reader.getNextInt();
        int sourceLocationX = reader.getNextInt();
        int sourceLocationY = reader.getNextInt();
        String exportItem = reader.getNextString();
        int exportAmount = reader.getNextInt();
        String importItem = reader.getNextString();
        int importAmount = reader.getNextInt();
        Trade trade = new Trade(new Point(sourceLocationX, sourceLocationY), exportItem,
                exportAmount, importItem, importAmount);
        trade.setId(id);
        return trade;
    }
}

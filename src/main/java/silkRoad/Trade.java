package silkRoad;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class Trade {
    public int id;
    public InventoryItem exportItem;
    public InventoryItem importItem;

    public Trade(Item exportItem, int exportItemAmount, Item importItem, int importItemAmount) {
        if (exportItem != null) {
            this.exportItem = new InventoryItem(exportItem, exportItemAmount);
        }
        if (importItem != null) {
            this.importItem = new InventoryItem(importItem, importItemAmount);
        }
    }

    private Trade(String exportItemId, int exportItemAmount, String importItemId,
            int importItemAmount) {
        if (exportItemAmount >= 0) {
            exportItem = new InventoryItem(exportItemId, exportItemAmount);
        }
        if (importItemAmount >= 0) {
            importItem = new InventoryItem(importItemId, importItemAmount);
        }
    }

    public boolean matchesFilter(String filter) {
        if (exportItem != null && exportItem.item.matchesSearch(exportItem, null, filter)) {
            return true;
        }
        if (importItem != null && importItem.item.matchesSearch(importItem, null, filter)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Trade && ((Trade) obj).id == id;
    }

    public void addSaveData(SaveData data) {
        data.addInt("id", id);
        if (exportItem == null) {
            data.addInt("exportamount", -1);
        } else {
            data.addInt("exportamount", exportItem.getAmount());
            data.addSafeString("exportitem", exportItem.item.getStringID());
        }
        if (importItem == null) {
            data.addInt("importamount", -1);
        } else {
            data.addInt("importamount", importItem.getAmount());
            data.addSafeString("importitem", importItem.item.getStringID());
        }
    }

    public static Trade fromLoadData(LoadData save) {
        int id = save.getInt("id");
        int exportAmount = save.getInt("exportamount");
        String exportItem = exportAmount < 0 ? null : save.getSafeString("exportitem");
        int importAmount = save.getInt("importamount");
        String importItem = importAmount < 0 ? null : save.getSafeString("importitem");

        Trade trade = new Trade(exportItem, exportAmount, importItem, importAmount);
        trade.id = id;
        return trade;
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextInt(id);
        if (exportItem == null) {
            writer.putNextInt(-1);
        } else {
            writer.putNextInt(exportItem.getAmount());
            writer.putNextString(exportItem.item.getStringID());
        }
        if (importItem == null) {
            writer.putNextInt(-1);
        } else {
            writer.putNextInt(importItem.getAmount());
            writer.putNextString(importItem.item.getStringID());
        }
    }

    public static Trade readPacket(PacketReader reader) {
        int id = reader.getNextInt();
        int exportAmount = reader.getNextInt();
        String exportItem = exportAmount < 0 ? null : reader.getNextString();
        int importAmount = reader.getNextInt();
        String importItem = importAmount < 0 ? null : reader.getNextString();

        Trade trade = new Trade(exportItem, exportAmount, importItem, importAmount);
        trade.id = id;
        return trade;
    }
}

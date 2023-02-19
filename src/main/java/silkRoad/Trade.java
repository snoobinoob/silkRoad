package silkRoad;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryItem;

public class Trade {
    public int id;
    public InventoryItem exportItem;
    public InventoryItem importItem;

    public Trade(String exportItemId, int exportItemAmount, String importItemId,
            int importItemAmount) {
        exportItem = new InventoryItem(exportItemId, exportItemAmount);
        importItem = new InventoryItem(importItemId, importItemAmount);
    }

    public Trade() {
        this("coin", 25, "ironore", 8);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Trade && ((Trade) obj).id == id;
    }

    public void addSaveData(SaveData data) {
        data.addInt("id", id);
        data.addSafeString("exportitem", exportItem.item.getStringID());
        data.addInt("exportamount", exportItem.getAmount());
        data.addSafeString("importitem", importItem.item.getStringID());
        data.addInt("importamount", importItem.getAmount());
    }

    public static Trade fromLoadData(LoadData save) {
        int id = save.getInt("id");
        String exportItem = save.getSafeString("exportitem");
        int exportAmount = save.getInt("exportamount");
        String importItem = save.getSafeString("importitem");
        int importAmount = save.getInt("importamount");

        Trade trade = new Trade(exportItem, exportAmount, importItem, importAmount);
        trade.id = id;
        return trade;
    }

    public Packet getContentPacket() {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(id);
        writer.putNextString(exportItem.item.getStringID());
        writer.putNextInt(exportItem.getAmount());
        writer.putNextString(importItem.item.getStringID());
        writer.putNextInt(importItem.getAmount());
        return content;
    }

    public static Trade fromPacket(PacketReader reader) {
        int id = reader.getNextInt();
        String exportItem = reader.getNextString();
        int exportAmount = reader.getNextInt();
        String importItem = reader.getNextString();
        int importAmount = reader.getNextInt();
        Trade trade = new Trade(exportItem, exportAmount, importItem, importAmount);
        trade.id = id;
        return trade;
    }
}

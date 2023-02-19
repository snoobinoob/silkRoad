package silkRoad.tradingPost;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.level.maps.Level;
import silkRoad.TradeInfo;
import silkRoad.packet.PacketTradeInfo;

public class TradingPostObjectEntity extends InventoryObjectEntity {
    public TradeInfo trades;

    public TradingPostObjectEntity(Level level, int x, int y) {
        super(level, x, y, 40);

        trades = new TradeInfo();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        serverTickTradesSync(getLevel().getServer());
    }

    private void serverTickTradesSync(Server server) {
        if (server == null) {
            return;
        }

        if (trades.isDirty()) {
            server.network.sendToClientsAt(new PacketTradeInfo(this), getLevel());
            trades.markClean();
        }
    }

    @Override
    public void remove() {
        super.remove();
        trades.remove(this);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSaveData(trades.getSaveData());
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        trades.applyLoadData(save);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        trades.writeContent(writer);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        trades.readContent(reader);
    }
}

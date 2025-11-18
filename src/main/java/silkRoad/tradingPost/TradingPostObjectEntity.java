package silkRoad.tradingPost;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import silkRoad.TradeInfo;
import silkRoad.packet.PacketTradeInfo;

import java.util.ArrayList;

public class TradingPostObjectEntity extends InventoryObjectEntity {
    public TradeInfo trades;

    public TradingPostObjectEntity(Level level, int tileX, int tileY) {
        super(level, tileX, tileY, 40);

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
            server.network.sendToClientsWithTile(new PacketTradeInfo(this), getLevel(), tileX, tileY);
            trades.markClean();
        }
    }

    @Override
    public void onObjectDestroyed(GameObject previousObject, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
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

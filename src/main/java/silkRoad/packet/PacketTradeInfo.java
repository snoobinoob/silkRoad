package silkRoad.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import silkRoad.tradingPost.TradingPostObjectEntity;

public class PacketTradeInfo extends Packet {
    private final int tileX;
    private final int tileY;
    private final Packet content;

    public PacketTradeInfo(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        tileX = reader.getNextInt();
        tileY = reader.getNextInt();
        content = reader.getNextContentPacket();
    }

    public PacketTradeInfo(TradingPostObjectEntity objectEntity) {
        tileX = objectEntity.tileX;
        tileY = objectEntity.tileY;
        content = objectEntity.trades.getContentPacket();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextContentPacket(content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        TradingPostObjectEntity objectEntity = (TradingPostObjectEntity) client.getLevel().entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity != null) {
            objectEntity.trades.applyContentPacket(content);
        }
    }
}

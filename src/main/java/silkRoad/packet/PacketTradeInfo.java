package silkRoad.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import silkRoad.tradingPost.TradingPostObjectEntity;

public class PacketTradeInfo extends Packet {
    private int x;
    private int y;
    private Packet content;

    public PacketTradeInfo(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        x = reader.getNextShortUnsigned();
        y = reader.getNextShortUnsigned();
        content = reader.getNextContentPacket();
    }

    public PacketTradeInfo(TradingPostObjectEntity objectEntity) {
        x = objectEntity.getX();
        y = objectEntity.getY();
        content = objectEntity.trades.getContentPacket();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextShortUnsigned(x);
        writer.putNextShortUnsigned(y);
        writer.putNextContentPacket(content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        TradingPostObjectEntity objectEntity =
                (TradingPostObjectEntity) client.getLevel().entityManager.getObjectEntity(x, y);
        if (objectEntity != null) {
            objectEntity.trades.applyContentPacket(content);
        }
    }
}

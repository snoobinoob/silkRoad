package silkRoad.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.client.Client;
import silkRoad.TradeMetadata;
import silkRoad.TradeRegistry;

import java.util.List;

public class PacketConnectionTradeList extends PacketTradeList {

    public PacketConnectionTradeList(byte[] data) {
        super(data);
    }

    public PacketConnectionTradeList(List<TradeMetadata> trades) {
        super(trades);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        TradeRegistry.setClientTrades(trades);
    }
}

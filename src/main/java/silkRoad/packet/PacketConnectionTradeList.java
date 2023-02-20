package silkRoad.packet;

import java.util.List;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.client.Client;
import silkRoad.SilkRoad;
import silkRoad.TradeMetadata;

public class PacketConnectionTradeList extends PacketTradeList {

    public PacketConnectionTradeList(byte[] data) {
        super(data);
    }

    public PacketConnectionTradeList(List<TradeMetadata> trades) {
        super(trades);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        SilkRoad.clientTrades = trades;
    }
}

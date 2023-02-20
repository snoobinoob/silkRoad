package silkRoad.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.client.Client;
import silkRoad.Location;
import silkRoad.SilkRoad;
import silkRoad.Trade;
import silkRoad.TradeMetadata;

public class PacketAddTrade extends PacketTrade {
    public PacketAddTrade(byte[] data) {
        super(data);
    }

    public PacketAddTrade(Trade trade, Location source) {
        super(trade, source);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        SilkRoad.clientTrades.add(new TradeMetadata(trade, source));
    }
}

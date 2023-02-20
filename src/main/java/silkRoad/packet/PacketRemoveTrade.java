package silkRoad.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.client.Client;
import silkRoad.Location;
import silkRoad.SilkRoad;
import silkRoad.Trade;
import silkRoad.TradeMetadata;

public class PacketRemoveTrade extends PacketTrade {
    public PacketRemoveTrade(byte[] data) {
        super(data);
    }

    public PacketRemoveTrade(Trade trade, Location source) {
        super(trade, source);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        SilkRoad.clientTrades.remove(new TradeMetadata(trade, source));
    }
}

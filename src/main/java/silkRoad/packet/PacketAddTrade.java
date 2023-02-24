package silkRoad.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.client.Client;
import silkRoad.Location;
import silkRoad.Trade;
import silkRoad.TradeMetadata;
import silkRoad.TradeRegistry;

public class PacketAddTrade extends PacketTrade {
    public PacketAddTrade(byte[] data) {
        super(data);
    }

    public PacketAddTrade(Trade trade, Location source) {
        super(trade, source);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        TradeRegistry.addClientTrade(new TradeMetadata(trade, source));
    }
}

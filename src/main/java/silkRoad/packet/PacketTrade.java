package silkRoad.packet;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import silkRoad.Location;
import silkRoad.Trade;

public abstract class PacketTrade extends Packet {
    public Trade trade;
    public Location source;

    public PacketTrade(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        trade = Trade.readPacket(reader);
        source = Location.readPacket(reader);
    }

    public PacketTrade(Trade trade, Location source) {
        this.trade = trade;
        PacketWriter writer = new PacketWriter(this);
        trade.writePacket(writer);
        source.writePacket(writer);
    }
}

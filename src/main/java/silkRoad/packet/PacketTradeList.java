package silkRoad.packet;

import java.util.LinkedList;
import java.util.List;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import silkRoad.Location;
import silkRoad.Trade;
import silkRoad.TradeMetadata;

public abstract class PacketTradeList extends Packet {
    public List<TradeMetadata> trades;

    public PacketTradeList(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        int numTrades = reader.getNextInt();
        trades = new LinkedList<>();
        for (int i = 0; i < numTrades; i++) {
            Trade trade = Trade.readPacket(reader);
            Location source = Location.readPacket(reader);
            trades.add(new TradeMetadata(trade, source));
        }
    }

    public PacketTradeList(List<TradeMetadata> tradeList) {
        trades = tradeList;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(trades.size());
        for (TradeMetadata tradeData : trades) {
            tradeData.trade.writePacket(writer);
            tradeData.source.writePacket(writer);
        }
    }
}

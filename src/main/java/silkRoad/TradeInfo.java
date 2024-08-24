package silkRoad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import silkRoad.tradingPost.TradingPostObjectEntity;

public class TradeInfo {
    public List<Trade> incomingTrades;
    public List<Trade> outgoingTrades;

    private boolean dirty;
    private final Map<Integer, Runnable> listeners;

    public TradeInfo() {
        incomingTrades = new ArrayList<>();
        outgoingTrades = new ArrayList<>();

        listeners = new HashMap<>();
    }

    public void markDirty() {
        dirty = true;
    }

    public void markClean() {
        dirty = false;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void onChanged(int uniqueSeed, Runnable listener) {
        listeners.put(uniqueSeed, listener);
    }

    public void removeListener(int uniqueSeed) {
        listeners.remove(uniqueSeed);
    }

    private void notifyListeners() {
        listeners.values().forEach(Runnable::run);
    }

    public void addOutgoingTrade(Trade trade) {
        outgoingTrades.add(trade);
        markDirty();
    }

    public void removeOutgoingTrade(Trade trade) {
        outgoingTrades.remove(trade);
        markDirty();
    }

    public void addIncomingTrade(Trade trade) {
        incomingTrades.add(trade);
        markDirty();
    }

    public void removeIncomingTrade(Trade trade) {
        incomingTrades.remove(trade);
        markDirty();
    }

    public void remove(TradingPostObjectEntity oe) {
        List<Integer> tradeIds = incomingTrades.stream().map(t -> t.id).collect(Collectors.toList());
        for (int tradeId : tradeIds) {
            TradeRegistry.unsubscribe(tradeId, oe);
        }
        tradeIds = outgoingTrades.stream().map(t -> t.id).collect(Collectors.toList());
        for (int tradeId : tradeIds) {
            TradeRegistry.removeTrade(tradeId, oe);
        }
    }

    public Packet getContentPacket() {
        Packet p = new Packet();
        PacketWriter writer = new PacketWriter(p);
        writeContent(writer);
        return p;
    }

    public void applyContentPacket(Packet packet) {
        PacketReader reader = new PacketReader(packet);
        readContent(reader);
        notifyListeners();
    }

    public void writeContent(PacketWriter writer) {
        writeTradeList(writer, incomingTrades);
        writeTradeList(writer, outgoingTrades);
    }

    public void readContent(PacketReader reader) {
        readTradeList(reader, incomingTrades);
        readTradeList(reader, outgoingTrades);
    }

    private void writeTradeList(PacketWriter writer, List<Trade> list) {
        writer.putNextInt(list.size());
        for (Trade trade : list) {
            trade.writePacket(writer);
        }
    }

    private void readTradeList(PacketReader reader, List<Trade> list) {
        list.clear();
        int numItems = reader.getNextInt();
        for (int i = 0; i < numItems; i++) {
            list.add(Trade.readPacket(reader));
        }
    }

    public SaveData getSaveData() {
        SaveData save = new SaveData("TRADEINFO");
        SaveData incoming = new SaveData("IN");
        for (Trade trade : incomingTrades) {
            SaveData tradeData = new SaveData("TRADE");
            trade.addSaveData(tradeData);
            incoming.addSaveData(tradeData);
        }
        SaveData outgoing = new SaveData("OUT");
        for (Trade trade : outgoingTrades) {
            SaveData tradeData = new SaveData("TRADE");
            trade.addSaveData(tradeData);
            outgoing.addSaveData(tradeData);
        }
        save.addSaveData(incoming);
        save.addSaveData(outgoing);
        return save;
    }

    public void applyLoadData(LoadData save) {
        LoadData data = save.getFirstLoadDataByName("TRADEINFO");
        LoadData incoming = data.getFirstLoadDataByName("IN");
        incomingTrades.clear();
        for (LoadData tradeData : incoming.getLoadDataByName("TRADE")) {
            incomingTrades.add(Trade.fromLoadData(tradeData));
        }
        LoadData outgoing = data.getFirstLoadDataByName("OUT");
        outgoingTrades.clear();
        for (LoadData tradeData : outgoing.getLoadDataByName("TRADE")) {
            outgoingTrades.add(Trade.fromLoadData(tradeData));
        }
    }
}

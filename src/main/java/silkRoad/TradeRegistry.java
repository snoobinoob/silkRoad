package silkRoad;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import silkRoad.tradingPost.TradingPostObjectEntity;

public class TradeRegistry {
    private static int nextId = 0;
    private static Map<Integer, TradeMetadata> tradeMap = new HashMap<>();

    public static void init() {
        nextId = 0;
        tradeMap = new HashMap<>();
    }

    public static Trade getTrade(int id) {
        TradeMetadata tradeData = tradeMap.get(id);
        return tradeData == null ? null : tradeData.trade;
    }

    public static Collection<TradeMetadata> allTrades() {
        return tradeMap.values();
    }

    public static int register(Trade trade, TradingPostObjectEntity source) {
        tradeMap.put(nextId, new TradeMetadata(trade, source));
        trade.id = nextId;
        source.trades.addOutgoingTrade(trade);
        return nextId++;
    }

    public static void removeTrade(int id, TradingPostObjectEntity source) {
        TradeMetadata tradeData = tradeMap.get(id);
        if (tradeData != null) {
            source.trades.removeOutgoingTrade(tradeData.trade);

            List<Location> destinations = tradeData.destinations.stream().toList();
            for (Location destination : destinations) {
                SilkRoad.broker.unsubscribeLocation(id, destination);
            }

            tradeMap.remove(id);
        }
    }

    public static void subscribe(int id, TradingPostObjectEntity subscriber) {
        TradeMetadata tradeData = tradeMap.get(id);
        if (tradeData != null) {
            tradeData.destinations.add(new Location(subscriber));
            subscriber.trades.addIncomingTrade(tradeData.trade);
        }
    }

    public static void unsubscribe(int id, TradingPostObjectEntity subscriber) {
        TradeMetadata tradeData = tradeMap.get(id);
        if (tradeData != null) {
            tradeData.destinations.remove(new Location(subscriber));
            subscriber.trades.removeIncomingTrade(tradeData.trade);
        }
    }

    public static void updateAvailableTrades(TradingPostObjectEntity oe) {
        Location oeLocation = new Location(oe);
        oe.trades.availableTrades.clear();
        oe.trades.availableTrades
                .addAll(tradeMap.values().stream().filter(t -> !t.source.equals(oeLocation))
                        .map(t -> t.trade).collect(Collectors.toList()));
        oe.trades.markDirty();
    }

    public static SaveData getSave() {
        SaveData save = new SaveData("TRADES");
        save.addInt("nextid", nextId);
        for (TradeMetadata tradeData : tradeMap.values()) {
            SaveData tradeSaveData = new SaveData("TRADE");
            tradeData.addSaveData(tradeSaveData);
            save.addSaveData(tradeSaveData);
        }
        return save;
    }

    public static void loadSave(LoadData save) {
        nextId = save.getInt("nextid");
        for (LoadData tradeLoadData : save.getLoadDataByName("TRADE")) {
            TradeMetadata tradeData = TradeMetadata.fromLoadData(tradeLoadData);
            tradeMap.put(tradeData.trade.id, tradeData);
        }
    }
}

package silkRoad;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import silkRoad.packet.PacketAddTrade;
import silkRoad.packet.PacketRemoveTrade;
import silkRoad.tradingPost.TradingPostObjectEntity;

public class TradeRegistry {
    private static int nextId = 0;
    private static Map<Integer, TradeMetadata> tradeMap = new HashMap<>();
    private static List<TradeMetadata> clientTrades = new LinkedList<>();

    public static void init() {
        nextId = 0;
        tradeMap = new HashMap<>();
        clientTrades = new LinkedList<>(); // Note: Destination data never filled
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
        source.getLevel().getServer().network
                .sendToAllClients(new PacketAddTrade(trade, new Location(source)));
        return nextId++;
    }

    public static Trade removeTrade(int id, TradingPostObjectEntity source) {
        TradeMetadata tradeData = tradeMap.get(id);
        if (tradeData != null) {
            source.trades.removeOutgoingTrade(tradeData.trade);

            List<Location> destinations = tradeData.destinations.stream().toList();
            for (Location destination : destinations) {
                SilkRoad.broker.unsubscribeLocation(id, destination);
            }

            tradeMap.remove(id);

            source.getLevel().getServer().network
                    .sendToAllClients(new PacketRemoveTrade(tradeData.trade, new Location(source)));
        }
        return tradeData == null ? null : tradeData.trade;
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

    public static void setClientTrades(List<TradeMetadata> trades) {
        clientTrades = trades;
    }

    public static void addClientTrade(TradeMetadata trade) {
        clientTrades.add(trade);
    }

    public static void removeClientTrade(TradeMetadata trade) {
        clientTrades.remove(trade);
    }

    public static List<Trade> getAvailableTrades(TradingPostObjectEntity oe) {
        Location oeLocation = new Location(oe);
        return clientTrades.stream().filter(t -> {
            if (SilkRoad.settings.maxTradeDistance >= 0
                    && oeLocation.distanceTo(t.source) > SilkRoad.settings.maxTradeDistance) {
                return false;
            }
            return !oeLocation.equals(t.source) && !oe.trades.incomingTrades.contains(t.trade);
        }).map(t -> t.trade).toList();
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

package silkRoad;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import silkRoad.packet.PacketAddTrade;
import silkRoad.packet.PacketRemoveTrade;
import silkRoad.tradingPost.TradingPostObjectEntity;

import java.util.*;
import java.util.stream.Collectors;

public class TradeRegistry {
    private static final Map<String, TradeMetadata> tradeMap = new HashMap<>();
    private static List<TradeMetadata> clientTrades = new LinkedList<>();

    public static Collection<TradeMetadata> allTrades() {
        return tradeMap.values();
    }

    public static void register(Trade trade, TradingPostObjectEntity source) {
        String id = UUID.randomUUID().toString();
        tradeMap.put(id, new TradeMetadata(trade, source));
        trade.id = id;
        source.trades.addOutgoingTrade(trade);
        source.getLevel().getServer().network
            .sendToAllClients(new PacketAddTrade(trade, new Location(source)));
    }

    public static Trade removeTrade(String id, TradingPostObjectEntity source) {
        TradeMetadata tradeData = tradeMap.get(id);
        if (tradeData != null) {
            source.trades.removeOutgoingTrade(tradeData.trade);

            List<Location> destinations = new ArrayList<>(tradeData.destinations);
            for (Location destination : destinations) {
                SilkRoad.broker.unsubscribeLocation(id, destination);
            }

            tradeMap.remove(id);

            source.getLevel().getServer().network
                .sendToAllClients(new PacketRemoveTrade(tradeData.trade, new Location(source)));
        }
        return tradeData == null ? null : tradeData.trade;
    }

    public static void subscribe(String id, TradingPostObjectEntity subscriber) {
        TradeMetadata tradeData = tradeMap.get(id);
        if (tradeData != null) {
            tradeData.destinations.add(new Location(subscriber));
            subscriber.trades.addIncomingTrade(tradeData.trade);
        }
    }

    public static void unsubscribe(String id, TradingPostObjectEntity subscriber) {
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
        }).map(t -> t.trade).collect(Collectors.toList());
    }

    public static boolean hasTrade(Trade trade) {
        if (trade == null) {
            return false;
        }

        return tradeMap.containsKey(trade.id);
    }

    public static SaveData getSave() {
        SaveData save = new SaveData("TRADES");
        for (TradeMetadata tradeData : tradeMap.values()) {
            SaveData tradeSaveData = new SaveData("TRADE");
            tradeData.addSaveData(tradeSaveData);
            save.addSaveData(tradeSaveData);
        }
        return save;
    }

    public static void loadSave(LoadData save) {
        for (LoadData tradeLoadData : save.getLoadDataByName("TRADE")) {
            TradeMetadata tradeData = TradeMetadata.fromLoadData(tradeLoadData);
            if (tradeData != null) {
                tradeMap.put(tradeData.trade.id, tradeData);
            }
        }
    }
}

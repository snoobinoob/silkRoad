package silkRoad;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class TradeRegistry {
    private static int nextId = 0;
    private static Map<Integer, Trade> tradeMap = new HashMap<>();

    private static List<Runnable> listeners = new ArrayList<>();

    public static void init() {
        nextId = 0;
        tradeMap = new HashMap<>();
        listeners = new ArrayList<>();
    }

    public static void onTradesModified(Runnable listener) {
        listeners.add(listener);
    }

    private static void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    public static int register(Trade trade) {
        tradeMap.put(nextId, trade);
        trade.setId(nextId);
        notifyListeners();
        return nextId++;
    }

    public static Trade getTrade(int id) {
        return tradeMap.get(id);
    }

    public static void removeTrade(int id) {
        tradeMap.remove(id);
        notifyListeners();
    }

    public static Stream<Trade> getAvailableTrades(Point location) {
        return tradeMap.values().stream().filter(trade -> {
            return !trade.isSourcedBy(location);
        });
    }

    public static void addSaveData(SaveData save) {
        SaveData data = new SaveData("TRADES");
        data.addInt("nextid", nextId);
        for (Trade trade : tradeMap.values()) {
            SaveData tradeData = new SaveData("TRADE");
            trade.addSaveData(tradeData);
            data.addSaveData(tradeData);
        }
        save.addSaveData(data);
    }

    public static void applyLoadData(LoadData save) {
        LoadData data = save.getFirstLoadDataByName("TRADES");
        if (data == null) {
            GameLog.warn.println("Could not load trades");
        } else {
            nextId = data.getInt("nextid");
            for (LoadData tradeData : data.getLoadDataByName("TRADE")) {
                Trade trade = Trade.fromLoadData(tradeData);
                tradeMap.put(trade.getId(), trade);
            }
        }
        notifyListeners();
    }
}

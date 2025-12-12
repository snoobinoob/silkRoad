package silkRoad;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import silkRoad.tradingPost.TradingPostObjectEntity;

import java.util.ArrayList;
import java.util.List;

public class TradeMetadata {
    public Trade trade;
    public Location source;
    public List<Location> destinations;

    public TradeMetadata(Trade trade, TradingPostObjectEntity oe) {
        this(trade, new Location(oe));
    }

    public TradeMetadata(Trade trade, Location source) {
        this.trade = trade;
        this.source = source;
        this.destinations = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TradeMetadata)) {
            return false;
        }
        TradeMetadata other = (TradeMetadata) obj;
        return trade.equals(other.trade);
    }

    public void addSaveData(SaveData save) {
        trade.addSaveData(save);
        SaveData sourceData = new SaveData("SOURCE");
        source.addSaveData(sourceData);
        save.addSaveData(sourceData);
        for (Location dest : destinations) {
            SaveData destData = new SaveData("DESTINATION");
            dest.addSaveData(destData);
            save.addSaveData(destData);
        }
    }

    public static TradeMetadata fromLoadData(LoadData save) {
        Trade trade = Trade.fromLoadData(save);
        LoadData sourceData = save.getFirstLoadDataByName("SOURCE");
        if (trade == null || sourceData == null) {
            return null;
        }
        Location source = Location.fromLoadData(sourceData);
        TradeMetadata tradeData = new TradeMetadata(trade, source);
        for (LoadData destData : save.getLoadDataByName("DESTINATION")) {
            tradeData.destinations.add(Location.fromLoadData(destData));
        }
        return tradeData;
    }
}

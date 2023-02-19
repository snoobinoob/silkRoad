package silkRoad;

import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.World;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import silkRoad.tradingPost.TradingPostObjectEntity;

public class TradeBroker {
    private World world;
    private long lastTradeTime;

    public TradeBroker() {

    }

    public void init(World world) {
        this.world = world;
        lastTradeTime = world.getWorldTime();
    }

    public void tick() {
        if (lastTradeTime + SilkRoad.settings.worldTimePerTrade < world.getWorldTime()) {
            lastTradeTime += SilkRoad.settings.worldTimePerTrade;

            for (TradeMetadata tradeData : TradeRegistry.allTrades()) {
                processTradeMetadata(tradeData);
            }
        }
    }

    public void unsubscribeLocation(int tradeId, Location location) {
        TradingPostObjectEntity oe = getTradingPostAt(location);
        if (oe == null) {
            return;
        }
        TradeRegistry.unsubscribe(tradeId, oe);
    }

    private void processTradeMetadata(TradeMetadata tradeData) {
        if (tradeData.destinations.size() == 0) {
            return;
        }
        Inventory srcInv = getInventoryAt(tradeData.source);
        if (srcInv == null) {
            return;
        }
        if (!inventoryHasItems(srcInv, tradeData.trade.exportItem)) {
            return;
        }
        for (Location dstLocation : tradeData.destinations) {
            Inventory dstInv = getInventoryAt(dstLocation);
            if (dstInv == null) {
                continue;
            }
            if (!inventoryHasItems(dstInv, tradeData.trade.importItem)) {
                continue;
            }
            removeItems(srcInv, tradeData.trade.exportItem);
            removeItems(dstInv, tradeData.trade.importItem);
            boolean srcHasSpace = inventoryHasSpace(srcInv, tradeData.trade.importItem);
            boolean dstHasSpace = inventoryHasSpace(srcInv, tradeData.trade.exportItem);
            if (srcHasSpace && dstHasSpace) {
                addItems(srcInv, tradeData.trade.importItem.copy());
                addItems(dstInv, tradeData.trade.exportItem.copy());
                break;
            } else {
                addItems(srcInv, tradeData.trade.exportItem.copy());
                addItems(dstInv, tradeData.trade.importItem.copy());
            }
        }
    }

    private Inventory getInventoryAt(Location location) {
        TradingPostObjectEntity oe = getTradingPostAt(location);
        return oe == null ? null : oe.inventory;
    }

    private TradingPostObjectEntity getTradingPostAt(Location location) {
        Level level = world.getLevel(new LevelIdentifier(location.getIslandPoint(), 0));
        if (!level.settlementLayer.isActive()) {
            return null;
        }
        ObjectEntity oe =
                level.entityManager.getObjectEntity(location.getTileX(), location.getTileY());
        if (oe == null || !(oe instanceof TradingPostObjectEntity)) {
            return null;
        }
        return ((TradingPostObjectEntity) oe);
    }

    private boolean inventoryHasItems(Inventory inv, InventoryItem item) {
        return inv.getAmount(null, null, item.item, "count") >= item.getAmount();
    }

    private void removeItems(Inventory inv, InventoryItem item) {
        inv.removeItems(null, null, item.item, item.getAmount(), "trade");
    }

    private boolean inventoryHasSpace(Inventory inv, InventoryItem item) {
        return inv.canAddItem(null, null, item, "trade") == item.getAmount();
    }

    private void addItems(Inventory inv, InventoryItem item) {
        inv.addItem(null, null, item, "trade", null);
    }
}

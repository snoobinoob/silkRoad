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
        TradingPostObjectEntity oe = getTradingPostAt(location, false);
        if (oe == null) {
            return;
        }
        TradeRegistry.unsubscribe(tradeId, oe);
    }

    private void processTradeMetadata(TradeMetadata tradeData) {
        if (tradeData.destinations.size() == 0) {
            return;
        }
        Inventory srcInv = getInventoryAt(tradeData.source, true);
        if (srcInv == null) {
            return;
        }
        if (!inventoryHasItems(srcInv, tradeData.trade.exportItem)) {
            return;
        }
        for (Location dstLocation : tradeData.destinations) {
            Inventory dstInv = getInventoryAt(dstLocation, true);
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
                addItems(srcInv, tradeData.trade.importItem);
                addItems(dstInv, tradeData.trade.exportItem);
                break;
            } else {
                addItems(srcInv, tradeData.trade.exportItem);
                addItems(dstInv, tradeData.trade.importItem);
            }
        }
    }

    private Inventory getInventoryAt(Location location, boolean requireSettlement) {
        TradingPostObjectEntity oe = getTradingPostAt(location, requireSettlement);
        return oe == null ? null : oe.inventory;
    }

    private TradingPostObjectEntity getTradingPostAt(Location location, boolean requireSettlement) {
        Level level = world.getLevel(new LevelIdentifier(location.getIslandPoint(), 0));
        if (requireSettlement && !level.settlementLayer.isActive()) {
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
        return item == null || inv.getAmount(null, null, item.item, "count") >= item.getAmount();
    }

    private void removeItems(Inventory inv, InventoryItem item) {
        if (item != null) {
            inv.removeItems(null, null, item.item, item.getAmount(), "trade");
        }
    }

    private boolean inventoryHasSpace(Inventory inv, InventoryItem item) {
        return item == null || inv.canAddItem(null, null, item, "trade") == item.getAmount();
    }

    private void addItems(Inventory inv, InventoryItem item) {
        if (item != null) {
            inv.addItem(null, null, item.copy(), "trade", null);
        }
    }
}

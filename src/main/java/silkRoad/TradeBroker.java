package silkRoad;

import necesse.engine.world.World;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import silkRoad.tradingPost.TradingPostObjectEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    public void unsubscribeLocation(String tradeId, Location location) {
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
        Collection<InventoryItem> srcItems = getItems(srcInv, tradeData.trade.exportItem);
        if (srcItems == null) {
            return;
        }
        for (Location dstLocation : tradeData.destinations) {
            Inventory dstInv = getInventoryAt(dstLocation, true);
            if (dstInv == null) {
                continue;
            }
            Collection<InventoryItem> dstItems = getItems(dstInv, tradeData.trade.importItem);
            if (dstItems == null) {
                continue;
            }
            removeItems(srcInv, srcItems);
            removeItems(dstInv, dstItems);
            boolean srcHasSpace = inventoryHasSpace(srcInv, dstItems);
            boolean dstHasSpace = inventoryHasSpace(dstInv, srcItems);
            if (srcHasSpace && dstHasSpace) {
                addItems(srcInv, dstItems);
                addItems(dstInv, srcItems);
                break;
            } else {
                addItems(srcInv, srcItems);
                addItems(dstInv, dstItems);
            }
        }
    }

    private Inventory getInventoryAt(Location location, boolean requireSettlement) {
        TradingPostObjectEntity oe = getTradingPostAt(location, requireSettlement);
        return oe == null ? null : oe.inventory;
    }

    private TradingPostObjectEntity getTradingPostAt(Location location, boolean requireSettlement) {
        Level level = world.getLevel(location.levelIdentifier);
        SettlementsWorldData settlementsData = SettlementsWorldData.getSettlementsData(world.worldEntity);
        ServerSettlementData settlementData = settlementsData.getServerDataAtTile(location.levelIdentifier, location.getTileX(), location.getTileY());
        if (requireSettlement && (settlementData == null || !settlementData.hasFlag())) {
            return null;
        }
        ObjectEntity oe = level.entityManager.getObjectEntity(location.getTileX(), location.getTileY());
        if (!(oe instanceof TradingPostObjectEntity)) {
            return null;
        }
        return ((TradingPostObjectEntity) oe);
    }

    private Collection<InventoryItem> getItems(Inventory inv, InventoryItem search) {
        List<InventoryItem> items = new ArrayList<>();
        if (search == null) {
            return items;
        }
        int amountRemaining = search.getAmount();
        for (int slot = 0; slot < inv.getSize(); slot++) {
            if (inv.isSlotClear(slot)) {
                continue;
            }
            InventoryItem item = inv.getItem(slot);
            if (!item.equals(null, search, true, true, "trade")) {
                continue;
            }
            int amountToAdd = Math.min(amountRemaining, item.getAmount());
            items.add(item.copy(amountToAdd));
            amountRemaining -= amountToAdd;
            if (amountRemaining == 0) {
                return items;
            }
        }
        return null;
    }

    private void removeItems(Inventory inv, Collection<InventoryItem> items) {
        for (InventoryItem item : items) {
            inv.removeItems(null, null, item.item, item.getAmount(), "trade");
        }
    }

    private boolean inventoryHasSpace(Inventory inv, Collection<InventoryItem> items) {
        Collection<InventoryItem> copiedItems = items.stream().map(InventoryItem::copy).collect(Collectors.toList());
        int totalItemCount = items.stream().mapToInt(InventoryItem::getAmount).sum();
        for (int slot = 0; slot < inv.getSize(); slot++) {
            InventoryItem currItem = inv.getItem(slot);
            for (InventoryItem item : copiedItems) {
                if (item.getAmount() == 0) {
                    continue;
                }
                int stackSize = inv.getItemStackLimit(slot, item);
                if (currItem == null) {
                    int amountToAdd = Math.min(stackSize, item.getAmount());
                    currItem = item.copy(amountToAdd);
                    item.setAmount(item.getAmount() - amountToAdd);
                    totalItemCount -= amountToAdd;
                } else if (currItem.equals(null, item, true, false, "trade")) {
                    int amountToAdd = Math.min(stackSize - currItem.getAmount(), item.getAmount());
                    currItem = currItem.copy(currItem.getAmount() + amountToAdd);
                    item.setAmount(item.getAmount() - amountToAdd);
                    totalItemCount -= amountToAdd;
                }
            }
            if (totalItemCount == 0) {
                return true;
            }
        }
        return false;
    }

    private void addItems(Inventory inv, Collection<InventoryItem> items) {
        for (InventoryItem item : items) {
            inv.addItem(null, null, item.copy(), "trade", null);
        }
    }
}

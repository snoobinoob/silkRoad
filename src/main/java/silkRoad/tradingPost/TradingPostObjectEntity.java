package silkRoad.tradingPost;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketObjectEntity;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.level.maps.Level;
import silkRoad.Trade;
import silkRoad.TradeRegistry;
import silkRoad.utils.ListenerList;

public class TradingPostObjectEntity extends InventoryObjectEntity {
    public static final int MAX_INCOMING_TRADES = 10;
    public static final int MAX_OUTGOING_TRADES = 10;
    public static final long TIME_BETWEEN_TRADES = 24000; // Approx 30 seconds?

    private ListenerList<Integer> incomingTradeIds;
    private ListenerList<Integer> outgoingTradeIds;
    private long lastTradeTime;

    public TradingPostObjectEntity(Level level, int x, int y) {
        super(level, x, y, 40);
        incomingTradeIds = new ListenerList<>();
        outgoingTradeIds = new ListenerList<>();
        lastTradeTime = getWorldEntity().getWorldTime();
    }

    public void addIncomingTrade(int id) {
        incomingTradeIds.add(id);
        markDirty();
    }

    public void removeIncomingTrade(int id) {
        incomingTradeIds.remove((Integer) id);
        markDirty();
    }

    public ListenerList<Integer> getIncomingTradeIds() {
        return incomingTradeIds;
    }

    public void addOutgoingTrade(int id) {
        outgoingTradeIds.add(id);
        markDirty();
    }

    public void removeOutgoingTrade(int tradeId) {
        outgoingTradeIds.remove((Integer) tradeId);
        markDirty();
    }

    public ListenerList<Integer> getOutgoingTradeIds() {
        return outgoingTradeIds;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        serverTickSync(getLevel().getServer());
        long currentTime = getWorldEntity().getWorldTime();
        while (lastTradeTime + TIME_BETWEEN_TRADES < currentTime) {
            lastTradeTime += TIME_BETWEEN_TRADES;
            System.out.println("Processing trades");
            for (int tradeId : outgoingTradeIds) {
                Trade trade = TradeRegistry.getTrade(tradeId);
                trade.performTrade(inventory, getLevel().getServer());
            }
        }
    }

    private void serverTickSync(Server server) {
        if (server == null)
            return;
        if (isDirty()) {
            server.network.sendToClientsAt(new PacketObjectEntity(this), this.getLevel());
            markClean();
        }
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addIntArray("incomingtradeids", intListToArray(incomingTradeIds));
        save.addIntArray("outgoingtradeids", intListToArray(outgoingTradeIds));
        save.addLong("lasttradetime", lastTradeTime);
    }

    private static int[] intListToArray(ListenerList<Integer> intList) {
        int[] ret = new int[intList.size()];
        int i = 0;
        for (int intVal : intList) {
            ret[i++] = intVal;
        }
        return ret;
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        incomingTradeIds.clear();
        ArrayList<Integer> loadedIds = new ArrayList<>();
        for (int tradeId : save.getIntArray("incomingtradeids", new int[0])) {
            loadedIds.add(tradeId);
        }
        incomingTradeIds.addAll(loadedIds);

        outgoingTradeIds.clear();
        loadedIds.clear();
        for (int tradeId : save.getIntArray("outgoingtradeids", new int[0])) {
            loadedIds.add(tradeId);
        }
        outgoingTradeIds.addAll(loadedIds);
        lastTradeTime = save.getLong("lasttradetime");
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        setupTradesContentPacket(writer, incomingTradeIds);
        setupTradesContentPacket(writer, outgoingTradeIds);
        writer.putNextLong(lastTradeTime);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        applyTradesContentPacket(reader, incomingTradeIds);
        applyTradesContentPacket(reader, outgoingTradeIds);
        reader.getNextLong();
    }

    private void setupTradesContentPacket(PacketWriter writer, ListenerList<Integer> tradeIds) {
        writer.putNextInt(tradeIds.size());
        for (int tradeId : tradeIds) {
            writer.putNextInt(tradeId);
        }
    }

    private void applyTradesContentPacket(PacketReader reader, ListenerList<Integer> tradeIds) {
        tradeIds.clear();
        int numTrades = reader.getNextInt();
        for (int i = 0; i < numTrades; i++) {
            tradeIds.add(reader.getNextInt());
        }
    }
}

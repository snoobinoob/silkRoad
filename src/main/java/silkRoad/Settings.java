package silkRoad;

import necesse.engine.modLoader.ModSettings;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class Settings extends ModSettings {
    public long worldTimePerTrade = 10000;
    public int maxOutgoingTrades = 5;
    public int maxIncomingTrades = 5;
    public int maxTradeDistance = -1;

    @Override
    public void addSaveData(SaveData save) {
        save.addLong("worldtimepertrade", worldTimePerTrade, "Attempt trades every X milliseconds");
        save.addInt("maxoutgoingtrades", maxOutgoingTrades, "Max owned trades per trading post (right side of UI)");
        save.addInt("maxincomingtrades", maxIncomingTrades, "Max subscribed trades per trading post (left side of UI)");
        save.addInt("maxtradedistance", maxTradeDistance, "Allowed distance between settlements (-1 for infinite)");
    }

    @Override
    public void applyLoadData(LoadData save) {
        worldTimePerTrade = save.getLong("worldtimepertrade");
        maxOutgoingTrades = save.getInt("maxoutgoingtrades");
        maxIncomingTrades = save.getInt("maxincomingtrades");
        maxTradeDistance = save.getInt("maxtradedistance");
    }

    public static Settings fromPacket(PacketReader reader) {
        Settings settings = new Settings();
        settings.worldTimePerTrade = reader.getNextLong();
        settings.maxOutgoingTrades = reader.getNextInt();
        settings.maxIncomingTrades = reader.getNextInt();
        settings.maxTradeDistance = reader.getNextInt();
        return settings;
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextLong(worldTimePerTrade);
        writer.putNextInt(maxOutgoingTrades);
        writer.putNextInt(maxIncomingTrades);
        writer.putNextInt(maxTradeDistance);
    }
}

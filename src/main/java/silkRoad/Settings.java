package silkRoad;

import necesse.engine.modLoader.ModSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class Settings extends ModSettings {
    public long worldTimePerTrade = 10000; // Milliseconds

    @Override
    public void addSaveData(SaveData save) {
        save.addLong("worldtimepertrade", worldTimePerTrade);
    }

    @Override
    public void applyLoadData(LoadData save) {
        worldTimePerTrade = save.getLong("worldtimepertrade");
    }

}

package silkRoad.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.WorldEntity;
import net.bytebuddy.asm.Advice;
import silkRoad.TradeRegistry;

public class WorldEntityPatch {
    @ModMethodPatch(target = WorldEntity.class, name = "getSave", arguments = {})
    public static class getSavePatch {
        @Advice.OnMethodExit
        public static void onExit(@Advice.Return SaveData save) {
            TradeRegistry.addSaveData(save);
        }
    }

    @ModMethodPatch(target = WorldEntity.class, name = "applyLoadData",
            arguments = {LoadData.class, boolean.class})
    public static class loadDataPatch {
        @Advice.OnMethodExit
        public static void onExit(@Advice.Argument(0) LoadData save,
                @Advice.Argument(1) boolean isSimple) {
            if (!isSimple) {
                TradeRegistry.init();
                TradeRegistry.applyLoadData(save);
            }
        }
    }
}

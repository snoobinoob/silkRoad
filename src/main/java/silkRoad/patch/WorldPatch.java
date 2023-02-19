package silkRoad.patch;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.save.LoadData;
import necesse.engine.world.World;
import necesse.engine.world.WorldFile;
import net.bytebuddy.asm.Advice;
import silkRoad.SilkRoad;
import silkRoad.TradeRegistry;

public class WorldPatch {
    @ModMethodPatch(target = World.class, name = "init", arguments = {})
    public static class initPatch {
        @Advice.OnMethodExit
        public static void onExit(@Advice.This World world) {
            SilkRoad.broker.init(world);
        }
    }

    @ModMethodPatch(target = World.class, name = "serverTick", arguments = {})
    public static class serverTickPatch {
        @Advice.OnMethodExit
        public static void onExit() {
            SilkRoad.broker.tick();
        }
    }

    @ModMethodPatch(target = World.class, name = "saveWorldEntity", arguments = {})
    public static class saveWorldEntityPatch {
        @Advice.OnMethodExit
        public static void onExit(@Advice.This World world) {
            WorldFile file = world.fileSystem.file("silkRoadTrades.dat");
            TradeRegistry.getSave().saveScript(file);
        }
    }

    @ModMethodPatch(target = World.class, name = "loadWorldEntity", arguments = {boolean.class})
    public static class loadWorldEntityPatch {
        @Advice.OnMethodExit
        public static void onExit(@Advice.This World world, @Advice.Argument(0) boolean isSimple) {
            if (!isSimple) {
                WorldFile file = world.fileSystem.file("silkRoadTrades.dat");
                TradeRegistry.loadSave(new LoadData(file));
            }
        }
    }
}

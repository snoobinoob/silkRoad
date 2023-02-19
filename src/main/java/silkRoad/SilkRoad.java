package silkRoad;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.PacketRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import silkRoad.form.TradingPostContainerForm;
import silkRoad.packet.PacketTradeInfo;
import silkRoad.tradingPost.TradingPostContainer;
import silkRoad.tradingPost.TradingPostObject;
import silkRoad.tradingPost.TradingPostObjectEntity;

@ModEntry
public class SilkRoad {
    public static int TRADING_POST_CONTAINER;
    public static int TRADE_INFO_PACKET;

    public static Settings settings;
    public static TradeBroker broker;

    public void init() {
        TradingPostObject.registerTradingPost();

        broker = new TradeBroker();

        TRADING_POST_CONTAINER = ContainerRegistry.registerOEContainer(
                (client, uniqueSeed, oe, content) -> new TradingPostContainerForm(client,
                        new TradingPostContainer(client.getClient(), uniqueSeed,
                                (TradingPostObjectEntity) oe, new PacketReader(content)),
                        uniqueSeed),
                (serverClient, uniqueSeed, oe, content, serverObject) -> new TradingPostContainer(
                        serverClient, uniqueSeed, (TradingPostObjectEntity) oe,
                        new PacketReader(content)));

        TRADE_INFO_PACKET = PacketRegistry.registerPacket(PacketTradeInfo.class);
    }

    public Settings initSettings() {
        settings = new Settings();
        return settings;
    }

    public void postInit() {
        Recipes.registerModRecipe(new Recipe("tradingpost", 1,
                RecipeTechRegistry.ADVANCED_WORKSTATION,
                new Ingredient[] {new Ingredient("anylog", 10), new Ingredient("ironbar", 2)}));
    }
}

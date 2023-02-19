package silkRoad;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import silkRoad.form.TradingPostForm;
import silkRoad.tradingPost.TradingPostContainer;
import silkRoad.tradingPost.TradingPostObject;
import silkRoad.tradingPost.TradingPostObjectEntity;

@ModEntry
public class SilkRoad {
    public static int TRADING_POST_CONTAINER;

    public void init() {
        System.out.println("Hello world from Silk Road!");

        ObjectRegistry.registerObject("tradingpost", new TradingPostObject(), 100, true);

        TRADING_POST_CONTAINER = ContainerRegistry.registerOEContainer(
                (client, uniqueSeed, oe, content) -> new TradingPostForm(client,
                        new TradingPostContainer((NetworkClient) client.getClient(), uniqueSeed,
                                (TradingPostObjectEntity) oe, new PacketReader(content))),
                (client, uniqueSeed, oe, content, serverObject) -> new TradingPostContainer(
                        (NetworkClient) client, uniqueSeed, (TradingPostObjectEntity) oe,
                        new PacketReader(content)));
    }

    public void postInit() {
        Recipes.registerModRecipe(new Recipe("tradingpost", 1,
                RecipeTechRegistry.ADVANCED_WORKSTATION,
                new Ingredient[] {new Ingredient("anylog", 10), new Ingredient("ironbar", 2)}));
    }
}

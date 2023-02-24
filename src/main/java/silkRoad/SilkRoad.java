package silkRoad;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.events.ServerClientConnectedEvent;
import necesse.engine.events.ServerStartEvent;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.PacketRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.gfx.ui.ButtonIcon;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import silkRoad.form.TradingPostContainerForm;
import silkRoad.packet.PacketAddTrade;
import silkRoad.packet.PacketConnectionTradeList;
import silkRoad.packet.PacketRemoveTrade;
import silkRoad.packet.PacketTradeInfo;
import silkRoad.tradingPost.TradingPostContainer;
import silkRoad.tradingPost.TradingPostObject;
import silkRoad.tradingPost.TradingPostObjectEntity;

@ModEntry
public class SilkRoad {
    public static int TRADING_POST_CONTAINER;
    public static int TRADE_INFO_PACKET;
    public static int TRADE_LIST_CONNECTION_PACKET;
    public static int ADD_TRADE_PACKET;
    public static int REMOVE_TRADE_PACKET;

    public static ButtonIcon addButtonIcon;

    public static Settings settings;
    public static TradeBroker broker;
    public static List<TradeMetadata> clientTrades; // Note: Destination data never filled

    public void init() {
        TradingPostObject.registerTradingPost();

        broker = new TradeBroker();
        clientTrades = new ArrayList<>();

        TRADING_POST_CONTAINER = ContainerRegistry.registerOEContainer(
                (client, uniqueSeed, oe, content) -> new TradingPostContainerForm(client,
                        new TradingPostContainer(client.getClient(), uniqueSeed,
                                (TradingPostObjectEntity) oe, new PacketReader(content)),
                        uniqueSeed),
                (serverClient, uniqueSeed, oe, content, serverObject) -> new TradingPostContainer(
                        serverClient, uniqueSeed, (TradingPostObjectEntity) oe,
                        new PacketReader(content)));

        TRADE_INFO_PACKET = PacketRegistry.registerPacket(PacketTradeInfo.class);
        TRADE_LIST_CONNECTION_PACKET =
                PacketRegistry.registerPacket(PacketConnectionTradeList.class);
        ADD_TRADE_PACKET = PacketRegistry.registerPacket(PacketAddTrade.class);
        REMOVE_TRADE_PACKET = PacketRegistry.registerPacket(PacketRemoveTrade.class);

        GameEvents.addListener(ServerClientConnectedEvent.class,
                new GameEventListener<ServerClientConnectedEvent>() {
                    @Override
                    public void onEvent(ServerClientConnectedEvent event) {
                        event.client.sendPacket(new PacketConnectionTradeList(
                                TradeRegistry.allTrades().stream().toList()));
                    }
                });

        GameEvents.addListener(ServerStartEvent.class, new GameEventListener<ServerStartEvent>() {
            @Override
            public void onEvent(ServerStartEvent event) {
                broker.init(event.server.world);
            }
        });

    }

    public void initResources() {
        addButtonIcon = new ButtonIcon(necesse.engine.Settings.UI, "button_add_20", false);
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

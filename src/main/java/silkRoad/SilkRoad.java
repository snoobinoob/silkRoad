package silkRoad;

import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.events.ServerClientConnectedEvent;
import necesse.engine.events.ServerStartEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.PacketRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonIcon;
import necesse.inventory.item.Item;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import silkRoad.form.TradingPostContainerForm;
import silkRoad.packet.*;
import silkRoad.tradingPost.TradingPostContainer;
import silkRoad.tradingPost.TradingPostObject;
import silkRoad.tradingPost.TradingPostObjectEntity;

import java.util.ArrayList;

@ModEntry
public class SilkRoad {
    public static int TRADING_POST_CONTAINER;

    public static ButtonIcon addButtonIcon;
    public static ListGameTooltips exportTooltips;
    public static ListGameTooltips importTooltips;
    public static StringTooltips noSpaceTooltip;

    public static Settings settings;
    public static TradeBroker broker;

    public void init() {
        TradingPostObject.registerTradingPost();

        broker = new TradeBroker();

        TRADING_POST_CONTAINER = ContainerRegistry.registerSettlementDependantOEContainer(
            (client, uniqueSeed, settlement, oe, content) -> new TradingPostContainerForm(
                client,
                new TradingPostContainer(
                    client.getClient(),
                    uniqueSeed,
                    settlement,
                    (TradingPostObjectEntity) oe,
                    new PacketReader(content)
                ),
                uniqueSeed
            ),
            (serverClient, uniqueSeed, settlement, oe, content, serverObject) -> new TradingPostContainer(
                serverClient,
                uniqueSeed,
                settlement,
                (TradingPostObjectEntity) oe,
                new PacketReader(content)
            )
        );

        PacketRegistry.registerPacket(PacketTradeInfo.class);
        PacketRegistry.registerPacket(PacketConnectionTradeList.class);
        PacketRegistry.registerPacket(PacketSyncSettings.class);
        PacketRegistry.registerPacket(PacketAddTrade.class);
        PacketRegistry.registerPacket(PacketRemoveTrade.class);

        GameEvents.addListener(
            ServerClientConnectedEvent.class,
            new GameEventListener<ServerClientConnectedEvent>() {
                @Override
                public void onEvent(ServerClientConnectedEvent event) {
                    ArrayList<TradeMetadata> allTrades = new ArrayList<>(TradeRegistry.allTrades());
                    event.client.sendPacket(new PacketConnectionTradeList(allTrades));
                    event.client.sendPacket(new PacketSyncSettings(settings));
                }
            }
        );

        GameEvents.addListener(
            ServerStartEvent.class,
            new GameEventListener<ServerStartEvent>() {
                @Override
                public void onEvent(ServerStartEvent event) {
                    broker.init(event.server.world);
                }
            }
        );

    }

    public void initResources() {
        addButtonIcon = new ButtonIcon(necesse.engine.Settings.UI, "button_add_20", false);

        exportTooltips = new ListGameTooltips();
        exportTooltips.add(new StringTooltips(
            Localization.translate("ui", "exportitem"),
            Item.Rarity.RARE.color
        ));
        exportTooltips.add(new LocalMessage("ui", "exporthelp"));

        importTooltips = new ListGameTooltips();
        importTooltips.add(new StringTooltips(
            Localization.translate("ui", "importitem"),
            Item.Rarity.RARE.color
        ));
        importTooltips.add(new LocalMessage("ui", "importhelp"));

        noSpaceTooltip = new StringTooltips(Localization.translate("ui", "notradespace"));
    }

    public Settings initSettings() {
        settings = new Settings();
        return settings;
    }

    public void postInit() {
        Recipes.registerModRecipe(
            new Recipe(
                "tradingpost",
                1,
                RecipeTechRegistry.DEMONIC_WORKSTATION,
                new Ingredient[]{
                    new Ingredient("anylog", 50),
                    new Ingredient("wool", 10),
                    new Ingredient("goldbar", 20)
                }
            ).showAfter("settlementflag"));
    }
}

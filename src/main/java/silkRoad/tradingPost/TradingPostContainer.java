package silkRoad.tradingPost;

import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.object.OEInventoryContainer;
import silkRoad.SilkRoad;
import silkRoad.Trade;
import silkRoad.TradeRegistry;
import silkRoad.action.TradeCustomAction;

public class TradingPostContainer extends OEInventoryContainer {
    public TradingPostObjectEntity objectEntity;

    public TradeCustomAction addTradeAction;
    public IntCustomAction removeTradeAction;
    public IntCustomAction subscribeAction;
    public IntCustomAction unsubscribeAction;

    public TradingPostContainer(NetworkClient client, int uniqueSeed, TradingPostObjectEntity oe,
            PacketReader reader) {
        super(client, uniqueSeed, oe, reader);
        objectEntity = oe;

        addTradeAction = registerAction(new TradeCustomAction() {
            @Override
            public void run(Trade trade) {
                if (client.isServerClient()) {
                    TradeRegistry.register(trade, objectEntity);
                }
            }
        });

        removeTradeAction = registerAction(new IntCustomAction() {
            @Override
            protected void run(int tradeId) {
                if (client.isServerClient()) {
                    TradeRegistry.removeTrade(tradeId, objectEntity);
                }
            }
        });

        subscribeAction = registerAction(new IntCustomAction() {
            @Override
            protected void run(int tradeId) {
                if (client.isServerClient()) {
                    TradeRegistry.subscribe(tradeId, objectEntity);
                }
            }
        });

        unsubscribeAction = registerAction(new IntCustomAction() {
            @Override
            protected void run(int tradeId) {
                if (client.isServerClient()) {
                    TradeRegistry.unsubscribe(tradeId, objectEntity);
                }
            }
        });
    }

    public boolean canAddOutgoing() {
        return settlementObjectManager.hasSettlementAccess
                && objectEntity.trades.outgoingTrades.size() < SilkRoad.settings.maxOutgoingTrades;
    }

    public boolean canAddIncoming() {
        return settlementObjectManager.hasSettlementAccess
                && objectEntity.trades.incomingTrades.size() < SilkRoad.settings.maxIncomingTrades;
    }

    public GameTooltips getSettlementAccessTooltip(GameTooltips defaultTooltips) {
        if (!settlementObjectManager.foundSettlement) {
            return new StringTooltips(Localization.translate("ui", "settlementnotfound"));
        }
        if (!settlementObjectManager.hasSettlementAccess) {
            StringTooltips tooltips =
                    new StringTooltips(Localization.translate("ui", "settlementispriv"));
            tooltips.add(Localization.translate("ui", "settlementprivatetip"), GameColor.LIGHT_GRAY,
                    400);
            return tooltips;
        }
        return defaultTooltips;
    }
}

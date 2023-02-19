package silkRoad.tradingPost;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.object.OEInventoryContainer;
import silkRoad.Trade;
import silkRoad.TradeRegistry;
import silkRoad.action.TradeCustomAction;

public class TradingPostContainer extends OEInventoryContainer {
    public TradingPostObjectEntity objectEntity;

    public TradeCustomAction addTradeAction;
    public IntCustomAction removeTradeAction;
    public IntCustomAction subscribeAction;
    public IntCustomAction unsubscribeAction;
    public EmptyCustomAction openAvailableTradesAction;

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

        openAvailableTradesAction = registerAction(new EmptyCustomAction() {
            @Override
            protected void run() {
                if (client.isServerClient()) {
                    TradeRegistry.updateAvailableTrades(objectEntity);
                }
            }
        });
    }
}

package silkRoad.tradingPost;

import java.awt.Point;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.object.OEInventoryContainer;
import silkRoad.Trade;
import silkRoad.TradeRegistry;
import silkRoad.actions.TradeCustomAction;

public class TradingPostContainer extends OEInventoryContainer {
    public TradeCustomAction addTradeAction;
    public IntCustomAction removeTradeAction;
    public IntCustomAction subscribeTradeAction;
    public IntCustomAction unsubscribeTradeAction;
    public TradingPostObjectEntity objectEntity;

    public TradingPostContainer(NetworkClient client, int uniqueSeed,
            TradingPostObjectEntity objectEntity, PacketReader reader) {
        super(client, uniqueSeed, objectEntity, reader);
        this.objectEntity = objectEntity;

        this.addTradeAction = registerAction(new TradeCustomAction() {
            @Override
            protected void run(Trade trade) {
                if (TradingPostContainer.this.client.isServerClient()) {
                    objectEntity.addOutgoingTrade(TradeRegistry.register(trade));
                }
            }
        });

        this.removeTradeAction = registerAction(new IntCustomAction() {
            @Override
            protected void run(int tradeId) {
                if (TradingPostContainer.this.client.isServerClient()) {
                    TradeRegistry.removeTrade(tradeId);
                    objectEntity.removeOutgoingTrade(tradeId);
                }
            }
        });

        this.subscribeTradeAction = registerAction(new IntCustomAction() {
            @Override
            protected void run(int tradeId) {
                if (TradingPostContainer.this.client.isServerClient()) {
                    objectEntity.addIncomingTrade(tradeId);
                    TradeRegistry.getTrade(tradeId).addDestination(getIslandLocation());
                }
            }
        });

        this.unsubscribeTradeAction = registerAction(new IntCustomAction() {
            @Override
            protected void run(int tradeId) {
                if (TradingPostContainer.this.client.isServerClient()) {
                    objectEntity.removeIncomingTrade(tradeId);
                    TradeRegistry.getTrade(tradeId).removeDestination(getIslandLocation());
                }
            }
        });
    }

    public Point getIslandLocation() {
        return new Point(objectEntity.getLevel().getIdentifier().getIslandX(),
                objectEntity.getLevel().getIdentifier().getIslandY());
    }
}

package silkRoad.action;

import necesse.engine.network.PacketReader;
import necesse.inventory.container.customAction.ContainerCustomAction;
import silkRoad.Trade;

public abstract class TradeCustomAction extends ContainerCustomAction {
    public void runAndSend(Trade trade) {
        runAndSendAction(trade.getContentPacket());
    }

    public void executePacket(PacketReader reader) {
        run(Trade.fromPacket(reader));
    }

    public abstract void run(Trade trade);
}

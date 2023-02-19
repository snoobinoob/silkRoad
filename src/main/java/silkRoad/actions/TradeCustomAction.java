package silkRoad.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;
import silkRoad.Trade;

public abstract class TradeCustomAction extends ContainerCustomAction {
    public void runAndSend(Trade trade) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        trade.setupContentPacket(writer);
        runAndSendAction(content);
    }

    public void executePacket(PacketReader reader) {
        run(Trade.fromPacket(reader));
    }

    protected abstract void run(Trade trade);
}

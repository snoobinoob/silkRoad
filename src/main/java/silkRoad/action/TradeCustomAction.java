package silkRoad.action;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;
import silkRoad.Trade;

public abstract class TradeCustomAction extends ContainerCustomAction {
    public void runAndSend(Trade trade) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        trade.writePacket(writer);
        runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        run(Trade.readPacket(reader));
    }

    public abstract void run(Trade trade);
}

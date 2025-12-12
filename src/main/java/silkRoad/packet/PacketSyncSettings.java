package silkRoad.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import silkRoad.Settings;
import silkRoad.SilkRoad;

public class PacketSyncSettings extends Packet {
    private final Settings settings;

    public PacketSyncSettings(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        settings = Settings.fromPacket(reader);
    }

    public PacketSyncSettings(Settings settings) {
        this.settings = settings;
        PacketWriter writer = new PacketWriter(this);
        settings.writePacket(writer);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        SilkRoad.settings = settings;
    }
}

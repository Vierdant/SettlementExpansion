package settlementexpansion.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketPlayerEnablePvP extends Packet {

    public final boolean shouldEnable;

    public PacketPlayerEnablePvP(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.shouldEnable = reader.getNextBoolean();
    }

    public PacketPlayerEnablePvP(boolean shouldEnable) {
        this.shouldEnable = shouldEnable;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextBoolean(this.shouldEnable);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.pvpSetCooldown = System.currentTimeMillis() - 1L;
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.setPvP(this.shouldEnable);
    }
}

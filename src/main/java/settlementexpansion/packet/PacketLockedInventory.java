package settlementexpansion.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import settlementexpansion.object.entity.LockedInventoryObjectEntity;

public class PacketLockedInventory extends Packet {
    private int x;
    private int y;
    private Packet content;

    public PacketLockedInventory(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        x = reader.getNextShortUnsigned();
        y = reader.getNextShortUnsigned();
        content = reader.getNextContentPacket();
    }

    public PacketLockedInventory(LockedInventoryObjectEntity objectEntity) {
        x = objectEntity.getX();
        y = objectEntity.getY();
        content = objectEntity.getContentPacket();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextShortUnsigned(x);
        writer.putNextShortUnsigned(y);
        writer.putNextContentPacket(content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        LockedInventoryObjectEntity objectEntity = (LockedInventoryObjectEntity) client.getLevel().entityManager.getObjectEntity(x, y);
        if (objectEntity != null) {
            objectEntity.applyContentPacket(content);
        }
    }
}

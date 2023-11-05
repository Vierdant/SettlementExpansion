package settlementexpansion.registry;

import necesse.engine.registries.PacketRegistry;
import settlementexpansion.packet.PacketLockedInventory;

public class PacketModRegistry {

    public static void registerPackets() {
        PacketRegistry.registerPacket(PacketLockedInventory.class);
    }
}

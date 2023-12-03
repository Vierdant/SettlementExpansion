package settlementexpansion.registry;

import necesse.engine.registries.PacketRegistry;
import settlementexpansion.packet.PacketLockedInventory;
import settlementexpansion.packet.PacketPlayerEnablePvP;

public class PacketModRegistry {

    public static void registerPackets() {
        PacketRegistry.registerPacket(PacketLockedInventory.class);
        PacketRegistry.registerPacket(PacketPlayerEnablePvP.class);
    }
}

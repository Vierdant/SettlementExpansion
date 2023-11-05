package settlementexpansion.mob.friendly;

import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.friendly.human.humanShop.BlacksmithHumanMob;
import necesse.inventory.container.mob.MageContainer;
import settlementexpansion.inventory.container.BlacksmithContainer;
import settlementexpansion.registry.ContainerModRegistry;

public class BlacksmithModHumanMob extends BlacksmithHumanMob {

    public BlacksmithModHumanMob() {
        super();
    }

    public PacketOpenContainer getOpenShopPacket(Server server, ServerClient client) {
        return PacketOpenContainer.Mob(ContainerModRegistry.BLACKSMITH_CONTAINER, this, BlacksmithContainer.getBlacksmithContainerContent(this, client));
    }
}

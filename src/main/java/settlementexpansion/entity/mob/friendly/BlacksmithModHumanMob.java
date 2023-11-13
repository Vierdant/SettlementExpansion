package settlementexpansion.entity.mob.friendly;

import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.humanShop.BlacksmithHumanMob;
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

package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import net.bytebuddy.asm.Advice;
import settlementexpansion.inventory.container.BlacksmithContainer;
import settlementexpansion.registry.ContainerModRegistry;

public class MobChangesPatch {

    @ModMethodPatch(target = HumanShop.class, name = "getOpenShopPacket", arguments = {Server.class, ServerClient.class})
    public static class HumanShopPacketPatch {
        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        static boolean onEnter() {
            return true;
        }

        @Advice.OnMethodExit()
        static void onExit(@Advice.This HumanShop mob, @Advice.Argument(0) Server server, @Advice.Argument(1) ServerClient client, @Advice.Return(readOnly = false) PacketOpenContainer container) {
            if (mob.settlerStringID != null && mob.settlerStringID.equals("blacksmith")) {
                container = PacketOpenContainer.Mob(ContainerModRegistry.BLACKSMITH_CONTAINER, mob, BlacksmithContainer.getBlacksmithContainerContent(mob, client));
            } else {
                container = PacketOpenContainer.Mob(ContainerRegistry.SHOP_CONTAINER, mob, mob.getShopItemsContentPacket(client));
            }
        }
    }


    @ModConstructorPatch(target = MageHumanMob.class, arguments = {})
    public static class MageHumanMobConstructPatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.This MageHumanMob mob) {
            mob.jobTypeHandler.getPriority("studying").disabledBySettler = false;
        }
    }
}

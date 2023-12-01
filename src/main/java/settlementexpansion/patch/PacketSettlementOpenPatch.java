package settlementexpansion.patch;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.packet.PacketSettlementOpen;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.objectEntity.SettlementFlagObjectEntity;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import net.bytebuddy.asm.Advice;
import settlementexpansion.registry.ContainerModRegistry;

@ModMethodPatch(target = PacketSettlementOpen.class, name = "processServer", arguments = {NetworkPacket.class, Server.class, ServerClient.class})
public class PacketSettlementOpenPatch {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean onEnter() {
        return true;
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.Argument(1) Server server, @Advice.Argument(2) ServerClient client) {
        Level level = server.world.getLevel(client);
        if (level.isIslandPosition() && level.getIslandDimension() == 0) {
            SettlementLevelData settlementData = SettlementLevelData.getSettlementData(level);
            if (settlementData != null) {
                SettlementFlagObjectEntity oe = settlementData.getObjectEntity();
                if (oe != null) {
                    PacketOpenContainer p = PacketOpenContainer.ObjectEntity(ContainerModRegistry.SETTLEMENT_CONTAINER, oe, oe.getContainerContentPacket(client));
                    ContainerRegistry.openAndSendContainer(client, p);
                } else {
                    client.sendChatMessage(new LocalMessage("ui", "settlementnone"));
                }
            } else {
                client.sendChatMessage(new LocalMessage("ui", "settlementnone"));
            }

        } else {
            client.sendChatMessage(new LocalMessage("ui", "settlementsurface"));
        }
    }

}

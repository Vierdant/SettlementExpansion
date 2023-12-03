package settlementexpansion.patch;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketPlayerLevelChange;
import necesse.engine.network.packet.PacketPlayerPvP;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import net.bytebuddy.asm.Advice;
import settlementexpansion.map.settlement.SettlementModData;
import settlementexpansion.packet.PacketPlayerEnablePvP;

@ModMethodPatch(target = PacketPlayerLevelChange.class, name = "processServer", arguments = {NetworkPacket.class, Server.class, ServerClient.class})
public class PacketLevelChangePatch {

    @Advice.OnMethodExit
    static void onExit(@Advice.This PacketPlayerLevelChange packet, @Advice.Argument(1) Server server, @Advice.Argument(2) ServerClient client) {
        if (client.slot == packet.slot) {
            SettlementLevelData data = SettlementLevelData.getSettlementData(client.getLevel());
            if (data != null) {
                SettlementModData layerData = SettlementModData.getSettlementModDataCreateIfNonExist(data.getLevel());
                if (layerData.isPvpFlagged) {
                    if (!client.pvpEnabled()) {
                        server.network.sendPacket(new PacketPlayerEnablePvP(true), client);
                    }

                    if (!data.getLevel().settlementLayer.doesClientHaveAccess(client)) {
                        data.getLevel().settlementLayer.getTeam().streamOnlineMembers(server).forEach((c) -> {
                            c.sendChatMessage(new LocalMessage("misc", "settlementhostileapproaching", "player", c.playerMob.getDisplayName()));
                        });
                        data.getLevel().settlementLayer.streamTeamMembersAndOnLevel().forEach((c) -> {
                            if (!c.pvpEnabled()) {
                                server.network.sendPacket(new PacketPlayerEnablePvP(true), c);
                            }
                        });
                    }
                }
            }
        }
    }

}

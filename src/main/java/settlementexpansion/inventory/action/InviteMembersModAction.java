package settlementexpansion.inventory.action;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.teams.PvPTeamsContainer;
import settlementexpansion.inventory.container.PvPTeamsModContainer;

public class InviteMembersModAction extends ContainerCustomAction {
    public final PvPTeamsModContainer container;

    public InviteMembersModAction(PvPTeamsModContainer container) {
        this.container = container;
    }

    public void runAndSend(ClientClient... clients) {
        Packet customContent = new Packet();
        PacketWriter writer = new PacketWriter(customContent);
        writer.putNextShortUnsigned(clients.length);
        ClientClient[] var4 = clients;
        int var5 = clients.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            ClientClient client = var4[var6];
            writer.putNextByteUnsigned(client.slot);
        }

        this.runAndSendAction(customContent);
    }

    public void executePacket(PacketReader reader) {
        if (this.container.client.isServerClient()) {
            ServerClient serverClient = this.container.client.getServerClient();
            PlayerTeam playerTeam = serverClient.getPlayerTeam();
            if (playerTeam != null) {
                int clients = reader.getNextShortUnsigned();

                for(int i = 0; i < clients; ++i) {
                    int slot = reader.getNextByteUnsigned();
                    ServerClient target = serverClient.getServer().getClient(slot);
                    if (target != null) {
                        PlayerTeam.invitePlayer(serverClient.getServer(), playerTeam, target);
                    }
                }

            }
        }
    }
}

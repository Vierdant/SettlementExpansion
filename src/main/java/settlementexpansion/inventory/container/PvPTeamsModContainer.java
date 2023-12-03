package settlementexpansion.inventory.container;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.*;
import necesse.inventory.container.teams.*;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import settlementexpansion.inventory.action.InviteMembersModAction;
import settlementexpansion.inventory.event.PvPSettlementFlaggedEvent;
import settlementexpansion.map.settlement.SettlementModData;

public class PvPTeamsModContainer extends Container {
    public PvPCurrentTeamUpdateEvent data;
    public boolean isSettlementFlagged;
    public final LongCustomAction kickMemberAction;
    public final LongCustomAction passOwnershipAction;
    public final InviteMembersModAction inviteMembersAction;
    public final StringCustomAction changeTeamNameAction;
    public final BooleanCustomAction setPublicAction;
    public final EmptyCustomAction askForExistingTeams;
    public final IntCustomAction requestToJoinTeam;
    public final EmptyCustomAction leaveTeamButton;
    public final EmptyCustomAction createTeamButton;

    public PvPTeamsModContainer(final NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed);
        PacketReader reader = new PacketReader(content);
        this.data = new PvPCurrentTeamUpdateEvent(reader);
        this.isSettlementFlagged = false;
        this.subscribeEvent(PvPSettlementFlaggedEvent.class, (e) -> true, () -> true);
        this.onEvent(PvPSettlementFlaggedEvent.class, (e) -> {
            this.isSettlementFlagged = e.isFlagged;
        });

        this.subscribeEvent(PvPCurrentTeamUpdateEvent.class, (e) -> true, () -> true);
        this.onEvent(PvPCurrentTeamUpdateEvent.class, (e) -> {
            this.data = e;
        });
        this.subscribeEvent(PvPOwnerUpdateEvent.class, (e) -> true, () -> true);
        this.onEvent(PvPOwnerUpdateEvent.class, (e) -> {
            if (this.data.currentTeam != null) {
                this.data.currentTeam.owner = e.ownerAuth;
            }

        });
        this.subscribeEvent(PvPPublicUpdateEvent.class, (e) -> true, () -> true);
        this.onEvent(PvPPublicUpdateEvent.class, (e) -> {
            if (this.data.currentTeam != null) {
                this.data.currentTeam.isPublic = e.isPublic;
            }

        });
        this.subscribeEvent(PvPMemberUpdateEvent.class, (e) -> true, () -> true);
        this.onEvent(PvPMemberUpdateEvent.class, (e) -> {
            if (e.added) {
                PvPTeamsContainer.MemberData last = this.data.members.stream().filter((m) -> m.auth == e.auth).findFirst().orElse(null);
                if (last != null) {
                    last.name = e.name;
                } else {
                    this.data.members.add(new PvPTeamsContainer.MemberData(e.auth, e.name));
                }
            } else {
                this.data.members.removeIf((m) -> m.auth == e.auth);
            }

        });
        this.subscribeEvent(PvPJoinRequestUpdateEvent.class, (e) -> true, () -> true);
        this.onEvent(PvPJoinRequestUpdateEvent.class, (e) -> {
            if (e.added) {
                PvPTeamsContainer.MemberData last = this.data.joinRequests.stream().filter((m) -> m.auth == e.auth).findFirst().orElse(null);
                if (last != null) {
                    last.name = e.name;
                } else {
                    this.data.joinRequests.add(new PvPTeamsContainer.MemberData(e.auth, e.name));
                }
            } else {
                this.data.joinRequests.removeIf((m) -> m.auth == e.auth);
            }

        });
        this.subscribeEvent(PvPAllTeamsUpdateEvent.class, (e) -> true, () -> true);
        this.kickMemberAction = this.registerAction(new LongCustomAction() {
            protected void run(long value) {
                if (client.isServerClient()) {
                    ServerClient serverClient = client.getServerClient();
                    PlayerTeam playerTeam = serverClient.getPlayerTeam();
                    if (playerTeam != null && playerTeam.getOwner() == serverClient.authentication) {
                        PlayerTeam.removeMember(serverClient.getServer(), playerTeam, value, true);
                    }
                }

            }
        });
        this.passOwnershipAction = this.registerAction(new LongCustomAction() {
            protected void run(long value) {
                if (client.isServerClient()) {
                    ServerClient serverClient = client.getServerClient();
                    PlayerTeam playerTeam = serverClient.getPlayerTeam();
                    if (playerTeam != null && playerTeam.getOwner() == serverClient.authentication) {
                        PlayerTeam.changeOwner(serverClient.getServer(), playerTeam, value);
                    }
                }

            }
        });
        this.inviteMembersAction = this.registerAction(new InviteMembersModAction(this));
        this.changeTeamNameAction = this.registerAction(new StringCustomAction() {
            protected void run(String value) {
                if (client.isServerClient()) {
                    ServerClient serverClient = client.getServerClient();
                    PlayerTeam playerTeam = serverClient.getPlayerTeam();
                    if (playerTeam != null && playerTeam.getOwner() == serverClient.authentication && !playerTeam.getName().equals(value)) {
                        playerTeam.setName(value);
                        PvPTeamsContainer.sendUpdates(serverClient, playerTeam);
                    }
                }

            }
        });
        this.setPublicAction = this.registerAction(new BooleanCustomAction() {
            protected void run(boolean value) {
                if (client.isServerClient()) {
                    ServerClient serverClient = client.getServerClient();
                    PlayerTeam playerTeam = serverClient.getPlayerTeam();
                    if (playerTeam != null && playerTeam.getOwner() == serverClient.authentication) {
                        PlayerTeam.changePublic(serverClient.getServer(), playerTeam, value);
                    }
                }

            }
        });
        this.askForExistingTeams = this.registerAction(new EmptyCustomAction() {
            protected void run() {
                if (client.isServerClient()) {
                    ServerClient serverClient = client.getServerClient();
                    (new PvPAllTeamsUpdateEvent(serverClient.getServer())).applyAndSendToClient(serverClient);
                }

            }
        });
        this.requestToJoinTeam = this.registerAction(new IntCustomAction() {
            protected void run(int value) {
                if (client.isServerClient()) {
                    ServerClient serverClient = client.getServerClient();
                    PlayerTeam team = serverClient.getServer().world.getTeams().getTeam(value);
                    if (team != null) {
                        if (team.isPublic()) {
                            PlayerTeam.addMember(serverClient.getServer(), team, serverClient.authentication);
                        } else {
                            PlayerTeam.addJoinRequest(serverClient.getServer(), team, serverClient.authentication);
                        }
                    }
                }

            }
        });
        this.leaveTeamButton = this.registerAction(new EmptyCustomAction() {
            protected void run() {
                if (client.isServerClient()) {
                    ServerClient serverClient = client.getServerClient();
                    PlayerTeam.removeMember(serverClient.getServer(), serverClient.getPlayerTeam(), serverClient.authentication, false);
                }

            }
        });
        this.createTeamButton = this.registerAction(new EmptyCustomAction() {
            protected void run() {
                if (client.isServerClient()) {
                    ServerClient serverClient = client.getServerClient();
                    if (serverClient.getTeamID() == -1) {
                        Server server = serverClient.getServer();
                        server.world.getTeams().createNewTeam(serverClient);
                    }
                }

            }
        });

        if (this.client.isServerClient()) {
            SettlementLevelData data = SettlementLevelData.getSettlementData(this.client.getServerClient().getLevel());
            if (data != null) {
                SettlementModData layerData = SettlementModData.getSettlementModDataCreateIfNonExist(data.getLevel());
                new PvPSettlementFlaggedEvent(layerData.isPvpFlagged).applyAndSendToClient(this.client.getServerClient());
            }
        }
    }

    public static Packet getContainerContent(ServerClient client) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        (new PvPCurrentTeamUpdateEvent(client)).write(writer);
        return packet;
    }

    public static void sendSingleUpdate(ServerClient client) {
        (new PvPCurrentTeamUpdateEvent(client)).applyAndSendToClient(client);
    }

    public static void sendUpdates(ServerClient client, PlayerTeam team) {
        client.getServer().streamClients().filter((c) -> {
            return c == client || c.getTeamID() == team.teamID;
        }).forEach((c) -> {
            (new PvPCurrentTeamUpdateEvent(c)).applyAndSendToClient(c);
        });
    }

    public static void sendUpdates(Server server, PlayerTeam team) {
        server.streamClients().filter((c) -> {
            return c.getTeamID() == team.teamID;
        }).forEach((c) -> {
            (new PvPCurrentTeamUpdateEvent(c)).applyAndSendToClient(c);
        });
    }

    public static class MemberData {
        public long auth;
        public String name;

        public MemberData(PacketReader reader) {
            this.auth = reader.getNextLong();
            this.name = reader.getNextString();
        }

        public MemberData(long auth, String name) {
            this.auth = auth;
            this.name = name;
        }

        public MemberData(Server server, long auth) {
            this.auth = auth;
            this.name = server.getNameByAuth(auth, "Unknown");
        }

        public void writeToPacket(PacketWriter writer) {
            writer.putNextLong(this.auth);
            writer.putNextString(this.name);
        }
    }

    public static class TeamData {
        public int teamID;
        public String name;
        public long owner;
        public boolean isPublic;
        public int memberCount;

        public TeamData(PlayerTeam team) {
            this.teamID = team.teamID;
            this.name = team.getName();
            this.owner = team.getOwner();
            this.isPublic = team.isPublic();
            this.memberCount = team.getMemberCount();
        }

        public TeamData(PacketReader reader) {
            this.teamID = reader.getNextShort();
            this.name = reader.getNextString();
            this.owner = reader.getNextLong();
            this.isPublic = reader.getNextBoolean();
            this.memberCount = reader.getNextInt();
        }

        public void writeToPacket(PacketWriter writer) {
            writer.putNextShort((short)this.teamID);
            writer.putNextString(this.name);
            writer.putNextLong(this.owner);
            writer.putNextBoolean(this.isPublic);
            writer.putNextInt(this.memberCount);
        }
    }

    public static class InviteData {
        public int teamID;
        public String teamName;

        public InviteData(PlayerTeam team) {
            this.teamID = team.teamID;
            this.teamName = team.getName();
        }

        public InviteData(PacketReader reader) {
            this.teamID = reader.getNextShort();
            this.teamName = reader.getNextString();
        }

        public void writeToPacket(PacketWriter writer) {
            writer.putNextShort((short)this.teamID);
            writer.putNextString(this.teamName);
        }
    }
}

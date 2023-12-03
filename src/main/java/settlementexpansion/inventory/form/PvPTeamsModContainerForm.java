package settlementexpansion.inventory.form;

import necesse.engine.GameAuth;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketPlayerTeamInviteReply;
import necesse.engine.network.packet.PacketPlayerTeamRequestReply;
import necesse.engine.state.MainGame;
import necesse.engine.state.State;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.*;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.teams.*;
import settlementexpansion.inventory.container.PvPTeamsModContainer;
import settlementexpansion.inventory.event.PvPSettlementFlaggedEvent;

import java.awt.*;
import java.util.Iterator;
import java.util.stream.Collectors;

public class PvPTeamsModContainerForm extends ContainerFormSwitcher<PvPTeamsModContainer> {
    public static boolean pauseGameOnClose = false;
    private boolean isOwner;
    public Form main;
    public Form invites;
    public Form changeName;
    public Form joinTeam;
    public ConfirmationForm confirmForm;
    public FormLocalLabel ownerLabel;
    public FormContentBox membersContent;
    public FormContentBox invitesContent;
    public FormContentBox joinTeamContent;
    public FormTextInput changeNameInput;
    public FormContentIconButton isPublicButton;
    public FormHorizontalToggle pvpToggle;

    public PvPTeamsModContainerForm(Client client, PvPTeamsModContainer container) {
        super(client, container);
        this.setupMainForm();
        FormFlow joinTeamFlow = new FormFlow(5);
        this.joinTeam = this.addComponent(new Form("jointeam", 300, 400));
        this.joinTeam.addComponent(joinTeamFlow.next(new FormLocalLabel("ui", "teamjoinateam", new FontOptions(20), 0, this.joinTeam.getWidth() / 2, 0, this.joinTeam.getWidth() - 20), 10));
        this.joinTeamContent = this.joinTeam.addComponent(new FormContentBox(0, joinTeamFlow.next(350), this.joinTeam.getWidth(), 350));
        this.joinTeamContent.alwaysShowVerticalScrollBar = true;
        (this.joinTeam.addComponent(new FormLocalTextButton("ui", "backbutton", 4, joinTeamFlow.next(40), this.joinTeam.getWidth() - 8, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE))).onClicked((e) -> {
            this.makeCurrent(this.main);
        });
        this.joinTeam.setHeight(joinTeamFlow.next());
        this.confirmForm = this.addComponent(new ConfirmationForm("teamconfirm"));
        this.changeName = this.addComponent(new Form(300, 110));
        this.changeName.addComponent(new FormLocalLabel("ui", "teamchangename", new FontOptions(20), 0, this.changeName.getWidth() / 2, 5));
        this.changeNameInput = this.changeName.addComponent(new FormTextInput(4, 30, FormInputSize.SIZE_32_TO_40, this.changeName.getWidth() - 8, 20));
        (this.changeName.addComponent(new FormLocalTextButton("ui", "confirmbutton", 4, this.changeName.getHeight() - 40, this.changeName.getWidth() / 2 - 6))).onClicked((e) -> {
            container.changeTeamNameAction.runAndSend(this.changeNameInput.getText());
            this.makeCurrent(this.main);
        });
        (this.changeName.addComponent(new FormLocalTextButton("ui", "backbutton", this.changeName.getWidth() / 2 + 2, this.changeName.getHeight() - 40, this.changeName.getWidth() / 2 - 6))).onClicked((e) -> {
            this.makeCurrent(this.main);
        });
        container.onEvent(PvPCurrentTeamUpdateEvent.class, (e) -> {
            this.onFullUpdate();
        });
        container.onEvent(PvPOwnerUpdateEvent.class, (e) -> {
            this.onOwnerUpdate();
        });
        container.onEvent(PvPPublicUpdateEvent.class, (e) -> {
            this.onPublicUpdate();
        });
        container.onEvent(PvPMemberUpdateEvent.class, (e) -> {
            this.updateMembersContent();
        });
        container.onEvent(PvPJoinRequestUpdateEvent.class, (e) -> {
            this.updateMembersContent();
        });
        container.onEvent(PvPAllTeamsUpdateEvent.class, this::updateJoinTeamsContent);
        this.makeCurrent(this.main);
    }

    @Override
    protected void init() {
        super.init();
        this.container.onEvent(PvPSettlementFlaggedEvent.class, this::onPvPActiveUpdate);
    }

    public void setupMainForm() {
        if (this.main != null) {
            this.removeComponent(this.main);
        }

        this.main = this.addComponent(new Form(300, 400));
        this.ownerLabel = null;
        this.membersContent = null;
        this.isOwner = false;
        FormFlow flow = new FormFlow(10);
        this.main.addComponent(new FormLocalLabel("ui", "pvplabel", new FontOptions(16), 0, this.main.getWidth() / 2, flow.next(20)));
        this.pvpToggle = this.main.addComponent(new FormHorizontalToggle(this.main.getWidth() / 2 - 16, flow.next(30)));
        this.pvpToggle.setToggled(this.client.pvpEnabled());
        this.pvpToggle.onToggled((e) -> {
            this.client.setPvP(e.from.isToggled());
        });
        this.pvpToggle.setCooldown(5000);
        long pvpCooldown = System.currentTimeMillis() - this.client.pvpChangeTime;
        if (pvpCooldown < 5000L) {
            this.pvpToggle.startCooldown((int)(5000L - pvpCooldown));
        }

        this.pvpToggle.setActive(!this.client.worldSettings.forcedPvP);
        PvPTeamsContainer.TeamData team = this.container.data.currentTeam;
        FormLocalLabel invitesLabel;
        if (team == null) {
            this.main.addComponent(flow.next(new FormLocalLabel("ui", "teamnocurrent", new FontOptions(16), 0, this.main.getWidth() / 2, 0, this.main.getWidth() - 10), 10));
            (this.main.addComponent(new FormLocalTextButton("ui", "teamcreate", 4, flow.next(40), this.main.getWidth() - 8))).onClicked((e) -> {
                this.container.createTeamButton.runAndSend();
                e.from.startCooldown(5000);
            });
            (this.main.addComponent(new FormLocalTextButton("ui", "teamjoinateam", 4, flow.next(40), this.main.getWidth() - 8))).onClicked((e) -> {
                this.joinTeamContent.clearComponents();
                this.joinTeamContent.addComponent(new FormLocalLabel("ui", "loadingdotdot", new FontOptions(16), 0, this.joinTeamContent.getWidth() / 2, 10, this.joinTeamContent.getWidth() - 20));
                this.joinTeamContent.setContentBox(new Rectangle(this.joinTeamContent.getWidth(), this.joinTeamContent.getHeight()));
                this.makeCurrent(this.joinTeam);
                this.container.askForExistingTeams.runAndSend();
            });
            flow.next(5);
            invitesLabel = this.main.addComponent(new FormLocalLabel("ui", "teaminvites", new FontOptions(20), 0, this.main.getWidth() / 2, flow.next(), this.main.getWidth() - 10));
            flow.next(invitesLabel.getHeight() + 10);
            this.invitesContent = this.main.addComponent(new FormContentBox(0, flow.next(120), this.main.getWidth(), 120));
            this.updateInvitesContent();
        } else {
            invitesLabel = this.main.addComponent(new FormLocalLabel(new StaticMessage(team.name), new FontOptions(20), 0, this.main.getWidth() / 2, flow.next(), this.main.getWidth() - 10));
            flow.next(invitesLabel.getHeight() + 5);
            long owner = team.owner;
            PvPTeamsContainer.MemberData ownerMember = this.container.data.members.stream().filter((m) -> m.auth == owner).findFirst().orElse(null);
            String ownerName = ownerMember == null ? "Unknown" : ownerMember.name;
            this.ownerLabel = this.main.addComponent(new FormLocalLabel(new LocalMessage("ui", "teamowner", "owner", ownerName), new FontOptions(12), 0, this.main.getWidth() / 2, flow.next(), this.main.getWidth() - 10));
            flow.next(this.ownerLabel.getHeight() + 5);
            this.isOwner = owner == GameAuth.getAuthentication();
            FormLocalLabel membersLabel;
            if (this.isOwner) {
                flow.next(5);
                membersLabel = this.main.addComponent(flow.next(new FormLocalLabel("ui", "teamcanselfjoin", new FontOptions(16), -1, 28, 0, this.main.getWidth() - 32), 10));
                this.isPublicButton = this.main.addComponent(new FormContentIconButton(4, membersLabel.getY() + membersLabel.getHeight() / 2 - 10, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_checked_20, new GameMessage[0]));
                this.isPublicButton.onClicked((e) -> {
                    this.container.setPublicAction.runAndSend(!this.container.data.currentTeam.isPublic);
                });
                this.onPublicUpdate();
                (this.main.addComponent(new FormLocalTextButton("ui", "teamchangename", 4, flow.next(40), this.main.getWidth() - 8))).onClicked((e) -> {
                    this.changeNameInput.setText(team.name);
                    this.makeCurrent(this.changeName);
                });
            }

            (this.main.addComponent(new FormLocalTextButton("ui", "teaminvite", 4, flow.next(40), this.main.getWidth() - 8))).onClicked((e) -> {
                this.openInvites();
            });
            flow.next(5);
            membersLabel = this.main.addComponent(new FormLocalLabel("ui", "teammembers", new FontOptions(20), 0, this.main.getWidth() / 2, flow.next(), this.main.getWidth() - 10));
            flow.next(membersLabel.getHeight() + 10);
            this.membersContent = this.main.addComponent(new FormContentBox(0, flow.next(120), this.main.getWidth(), 120));
            this.updateMembersContent();
            (this.main.addComponent(new FormLocalTextButton("ui", "teamleave", 4, flow.next(40), this.main.getWidth() - 8))).onClicked((e) -> {
                GameMessageBuilder builder = (new GameMessageBuilder()).append("ui", "teamleaveconf");
                if (this.container.data.currentTeam != null && !this.container.data.currentTeam.isPublic) {
                    builder.append("\n\n").append("ui", "teamleaveneedinvite");
                }

                this.confirmForm.setupConfirmation(builder, () -> {
                    this.container.leaveTeamButton.runAndSend();
                    this.setupMainForm();
                    this.makeCurrent(this.main);
                }, () -> {
                    this.makeCurrent(this.main);
                });
                this.makeCurrent(this.confirmForm);
            });
        }

        (this.main.addComponent(new FormLocalTextButton("ui", "closebutton", 4, flow.next(40), this.main.getWidth() - 8))).onClicked((e) -> {
            this.client.closeContainer(true);
            if (pauseGameOnClose) {
                State currentState = GlobalData.getCurrentState();
                if (currentState instanceof MainGame) {
                    currentState.setRunning(false);
                }

                pauseGameOnClose = false;
            }

        });
        this.main.setHeight(flow.next());
        this.main.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
    }

    public void updateInvitesContent() {
        if (this.invitesContent != null) {
            this.invitesContent.clearComponents();
            FormFlow invitesFlow = new FormFlow();
            if (this.container.data.invites.isEmpty()) {
                invitesFlow.next(10);
                this.invitesContent.addComponent(invitesFlow.next(new FormLocalLabel("ui", "teamnoinvites", new FontOptions(16), 0, this.invitesContent.getWidth() / 2, 10, this.invitesContent.getWidth() - 20), 10));
            } else {
                int colorCounter = 0;

                for(Iterator<PvPTeamsContainer.InviteData> var3 = this.container.data.invites.iterator(); var3.hasNext(); ++colorCounter) {
                    PvPTeamsContainer.InviteData invite = var3.next();
                    Color color = colorCounter % 2 == 0 ? new Color(220, 220, 220) : new Color(180, 180, 180);
                    this.invitesContent.addComponent(new FormTeamInvite(4, invitesFlow.next(20), this.main.getWidth() - 8, 20, invite, color) {
                        public void onAccept(PvPTeamsContainer.InviteData invite) {
                            client.network.sendPacket(new PacketPlayerTeamInviteReply(invite.teamID, true));
                        }

                        public void onDecline(PvPTeamsContainer.InviteData invite) {
                            container.data.invites.removeIf((i) -> i.teamID == invite.teamID);
                            client.network.sendPacket(new PacketPlayerTeamInviteReply(invite.teamID, false));
                            updateInvitesContent();
                        }
                    });
                }
            }

            this.invitesContent.setContentBox(new Rectangle(0, 0, this.main.getWidth(), invitesFlow.next()));
        }

    }

    public void updateJoinTeamsContent(PvPAllTeamsUpdateEvent event) {
        this.joinTeamContent.clearComponents();
        FormFlow flow = new FormFlow();
        if (event.teams.isEmpty()) {
            flow.next(10);
            this.joinTeamContent.addComponent(flow.next(new FormLocalLabel("ui", "teamnoteams", new FontOptions(16), 0, this.joinTeamContent.getWidth() / 2, 0, this.joinTeamContent.getWidth() - 20), 10));
        } else {

            for (PvPTeamsContainer.TeamData team : event.teams) {
                this.joinTeamContent.addComponent(flow.next(new FormLabel(team.name, new FontOptions(20), -1, 5, 0), 2));
                this.joinTeamContent.addComponent(flow.next(new FormLocalLabel(new LocalMessage("ui", "teammembercount", new Object[]{"count", team.memberCount}), new FontOptions(16), -1, 5, 0), 2));
                if (team.isPublic) {
                    this.joinTeamContent.addComponent(flow.next(new FormLocalLabel("ui", "teampublic", new FontOptions(16), -1, 5, 0), 2));
                    (this.joinTeamContent.addComponent(flow.next(new FormLocalTextButton("ui", "teamjoin", 4, 0, this.joinTeamContent.getWidth() - 8 - this.joinTeamContent.getScrollBarWidth(), FormInputSize.SIZE_24, ButtonColor.BASE)))).onClicked((e) -> {
                        this.container.requestToJoinTeam.runAndSend(team.teamID);
                        e.from.startCooldown(5000);
                    });
                } else {
                    this.joinTeamContent.addComponent(flow.next(new FormLocalLabel("ui", "teamprivate", new FontOptions(16), -1, 5, 0), 2));
                    (this.joinTeamContent.addComponent(flow.next(new FormLocalTextButton("ui", "teamrequestjoin", 4, 0, this.joinTeamContent.getWidth() - 8 - this.joinTeamContent.getScrollBarWidth(), FormInputSize.SIZE_24, ButtonColor.BASE)))).onClicked((e) -> {
                        this.container.requestToJoinTeam.runAndSend(team.teamID);
                        e.from.startCooldown(5000);
                    });
                }

                flow.next(8);
                this.joinTeamContent.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 4, flow.next(), this.joinTeamContent.getWidth() - 8 - this.joinTeamContent.getScrollBarWidth(), true));
                flow.next(8);
            }
        }

        this.joinTeamContent.setContentBox(new Rectangle(0, 0, this.joinTeamContent.getWidth(), flow.next()));
    }

    public void updateMembersContent() {
        if (this.membersContent != null) {
            this.membersContent.clearComponents();
            FormFlow membersFlow = new FormFlow();
            int colorCounter = 0;

            Iterator var3;
            PvPTeamsContainer.MemberData request;
            Color color;
            for(var3 = this.container.data.members.iterator(); var3.hasNext(); ++colorCounter) {
                request = (PvPTeamsContainer.MemberData) var3.next();
                color = colorCounter % 2 == 0 ? new Color(220, 220, 220) : new Color(180, 180, 180);
                this.membersContent.addComponent(new FormTeamMember(4, membersFlow.next(20), this.main.getWidth() - 8, 20, request, this.isOwner, color) {
                    public void onKickMember(PvPTeamsContainer.MemberData member) {
                        container.kickMemberAction.runAndSend(member.auth);
                    }

                    public void onPassOwnership(PvPTeamsContainer.MemberData member) {
                        container.passOwnershipAction.runAndSend(member.auth);
                    }
                });
            }

            if (!this.container.data.joinRequests.isEmpty()) {
                membersFlow.next(10);
                this.membersContent.addComponent(new FormLocalLabel("ui", "teamjoinrequests", new FontOptions(16), -1, 5, membersFlow.next(20) + 2));

                for(var3 = this.container.data.joinRequests.iterator(); var3.hasNext(); ++colorCounter) {
                    request = (PvPTeamsContainer.MemberData) var3.next();
                    color = colorCounter % 2 == 0 ? new Color(220, 220, 220) : new Color(180, 180, 180);
                    this.membersContent.addComponent(new FormTeamJoinRequest(4, membersFlow.next(20), this.main.getWidth() - 8, 20, request, color) {
                        public void onAccept(PvPTeamsContainer.MemberData request) {
                            client.network.sendPacket(new PacketPlayerTeamRequestReply(request.auth, true));
                        }

                        public void onDecline(PvPTeamsContainer.MemberData request) {
                            client.network.sendPacket(new PacketPlayerTeamRequestReply(request.auth, false));
                            container.data.joinRequests.removeIf((i) -> i.auth == request.auth);
                            updateMembersContent();
                        }
                    });
                }
            }

            this.membersContent.setContentBox(new Rectangle(0, 0, this.main.getWidth(), membersFlow.next()));
        }

    }

    public void openInvites() {
        if (this.invites != null) {
            this.removeComponent(this.invites);
        }

        this.invites = this.addComponent(new Form(300, 300));
        this.invites.addComponent(new FormLocalLabel("ui", "teaminvite", new FontOptions(20), 0, this.invites.getWidth() / 2, 5));
        FormContentBox content = this.invites.addComponent(new FormContentBox(0, 30, this.invites.getWidth(), this.invites.getHeight() - 110));
        java.util.List<FormTeamPlayerInvite> inviteComps = this.client.streamClients().filter((c) -> c != null && c.slot != this.client.getSlot()).map((c) -> new FormTeamPlayerInvite(0, 0, c, this.invites.getWidth(), null)).collect(Collectors.toList());
        FormFlow invitesFlow = new FormFlow();
        int colorCounter = 0;

        for(Iterator<FormTeamPlayerInvite> var5 = inviteComps.iterator(); var5.hasNext(); ++colorCounter) {
            FormTeamPlayerInvite inviteComp = var5.next();
            inviteComp.backgroundColor = colorCounter % 2 == 0 ? new Color(160, 160, 160) : new Color(180, 180, 180);
            inviteComp.setPosition(0, invitesFlow.next(16));
            content.addComponent(inviteComp);
        }

        (this.invites.addComponent(new FormLocalTextButton("ui", "teaminviteselected", 4, this.invites.getHeight() - 80, this.invites.getWidth() - 8))).onClicked((e) -> {
            ClientClient[] clients = inviteComps.stream().filter((c) -> c.selected && this.client.getClient(c.client.slot) == c.client).map((c) -> c.client).toArray(ClientClient[]::new);
            if (clients.length != 0) {
                this.container.inviteMembersAction.runAndSend(clients);
            }

            this.makeCurrent(this.main);
            this.removeComponent(this.invites);
            this.invites = null;
        });
        (this.invites.addComponent(new FormLocalTextButton("ui", "backbutton", 4, this.invites.getHeight() - 40, this.invites.getWidth() - 8))).onClicked((e) -> {
            this.makeCurrent(this.main);
            this.removeComponent(this.invites);
            this.invites = null;
        });
        this.invites.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
        this.makeCurrent(this.invites);
    }

    public void onFullUpdate() {
        boolean mainCurrent = this.isCurrent(this.main);
        this.setupMainForm();
        if (mainCurrent) {
            this.makeCurrent(this.main);
        } else if (this.container.data.currentTeam != null) {
            if (this.isCurrent(this.joinTeam)) {
                this.makeCurrent(this.main);
            }

            if (this.container.data.currentTeam.owner != GameAuth.getAuthentication() && this.isCurrent(this.changeName)) {
                this.makeCurrent(this.main);
            }
        } else if (this.invites != null && this.isCurrent(this.invites) || this.isCurrent(this.changeName)) {
            this.makeCurrent(this.main);
        }

    }

    public void onOwnerUpdate() {
        if (!this.isOwner && this.container.data.currentTeam.owner != GameAuth.getAuthentication()) {
            if (this.ownerLabel != null) {
                long owner = this.container.data.currentTeam.owner;
                PvPTeamsContainer.MemberData ownerMember = this.container.data.members.stream().filter((m) -> m.auth == owner).findFirst().orElse(null);
                String ownerName = ownerMember == null ? "Unknown" : ownerMember.name;
                this.ownerLabel.setLocalization(new LocalMessage("ui", "teamowner", "owner", ownerName));
            }
        } else {
            this.onFullUpdate();
        }
    }

    public void onPvPActiveUpdate(PvPSettlementFlaggedEvent event) {
        this.pvpToggle.setActive(!event.isFlagged);
    }

    public void onPublicUpdate() {
        if (this.isPublicButton != null && this.container.data.currentTeam != null) {
            this.isPublicButton.setIcon(this.container.data.currentTeam.isPublic ? Settings.UI.button_checked_20 : Settings.UI.button_escaped_20);
        }

    }

    public void onWindowResized() {
        super.onWindowResized();
        this.main.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
        if (this.invites != null) {
            this.invites.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
        }

        this.changeName.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
        this.joinTeam.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
    }

    public boolean shouldOpenInventory() {
        return false;
    }

    public boolean shouldShowToolbar() {
        return false;
    }
}

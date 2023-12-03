package settlementexpansion.inventory.container;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketPlayerPvP;
import necesse.engine.network.server.ServerClient;
import necesse.entity.TileDamageType;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import settlementexpansion.inventory.event.SettlementModDataUpdateEvent;
import settlementexpansion.map.settlement.SettlementModData;
import settlementexpansion.object.entity.SettlementFlagModObjectEntity;
import settlementexpansion.packet.PacketPlayerEnablePvP;

public class SettlementModContainer extends SettlementContainer {

    public EmptyCustomAction togglePvpFlag;
    public EmptyCustomAction destroyFlag;
    public boolean isPvpFlagged;
    public SettlementFlagModObjectEntity flagObjectEntity;
    public boolean settlementSafe;

    public SettlementModContainer(NetworkClient client, int uniqueSeed, SettlementFlagModObjectEntity objectEntity, Packet contentPacket) {
        super(client, uniqueSeed, objectEntity, contentPacket);
        this.flagObjectEntity = objectEntity;

        this.subscribeEvent(SettlementModDataUpdateEvent.class, (e) -> true, () -> true);
        this.onEvent(SettlementModDataUpdateEvent.class, (event) -> {
           this.isPvpFlagged = event.isPvpFlagged;
           this.settlementSafe = event.isSettlementSafe;
           if (this.isPvpFlagged && event.shouldStartCooldown) {
               this.flagObjectEntity.startCooldown();
           }
        });


        if (client.isServerClient()) {
            this.isPvpFlagged = getLevelModData().isPvpFlagged;
            this.settlementSafe = isSettlementSafe();

            new SettlementModDataUpdateEvent(getLevelModData(), client.getServerClient(), false, this.settlementSafe).applyAndSendToClient(client.getServerClient());
            new SettlementBasicsEvent(getLevelData()).applyAndSendToClient(client.getServerClient());
        }

        this.togglePvpFlag = this.registerAction(new EmptyCustomAction() {
            @Override
            protected void run() {
                if (client.isServerClient()) {
                    getLevelModData().togglePvpFlag();
                    boolean shouldStartCooldown = getLevelModData().isPvpFlagged;
                    if (shouldStartCooldown) {
                        getLevel().settlementLayer.streamTeamMembersAndOnLevel().forEach((c) -> {
                            if (!c.pvpEnabled()) {
                                System.out.println("enabling");
                                getLevel().getServer().network.sendPacket(new PacketPlayerEnablePvP(true), c);
                            }
                        });
                    }

                    new SettlementModDataUpdateEvent(getLevelModData(), client.getServerClient(), shouldStartCooldown, settlementSafe).applyAndSendToAllClients(client.getServerClient().getServer());
                    new SettlementBasicsEvent(getLevelData()).applyAndSendToClient(client.getServerClient());
                }
            }
        });

        this.destroyFlag = this.registerAction(new EmptyCustomAction() {
            @Override
            protected void run() {
                int x = objectEntity.getTileX();
                int y = objectEntity.getTileY();
                getLevel().entityManager.doDamage(x, y, 100, TileDamageType.Object, -1, null);
                close();
            }
        });
    }

    public boolean isSettlementSafe() {
        if (!this.client.isServerClient()) {
            throw new IllegalStateException("Cannot check settlement safety client side");
        }
        for (ServerClient c : getLevel().getServer().getClients()) {
            if (c.getLevelIdentifier().equals(getLevel().getIdentifier())
                    && c.pvpEnabled() && getLevelModData().isPvpFlagged && !getLevelLayer().doesClientHaveAccess(c)) {
                return false;
            }
        }
        return true;
    }

    public SettlementModData getLevelModData() {
        if (!this.client.isServerClient()) {
            throw new IllegalStateException("Cannot get level data client side");
        } else {
            return SettlementModData.getSettlementModData(this.getLevel());
        }
    }
}

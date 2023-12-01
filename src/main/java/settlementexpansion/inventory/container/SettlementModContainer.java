package settlementexpansion.inventory.container;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.entity.TileDamageType;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import settlementexpansion.inventory.event.SettlementPvpToggleEvent;
import settlementexpansion.map.settlement.SettlementModData;
import settlementexpansion.object.entity.SettlementFlagModObjectEntity;

public class SettlementModContainer extends SettlementContainer {

    public EmptyCustomAction togglePvpFlag;
    public EmptyCustomAction destroyFlag;
    public boolean isPvpFlagged;
    public SettlementFlagModObjectEntity flagObjectEntity;

    public SettlementModContainer(NetworkClient client, int uniqueSeed, SettlementFlagModObjectEntity objectEntity, Packet contentPacket) {
        super(client, uniqueSeed, objectEntity, contentPacket);
        this.flagObjectEntity = objectEntity;

        this.subscribeEvent(SettlementPvpToggleEvent.class, (e) -> true, () -> true);
        this.onEvent(SettlementPvpToggleEvent.class, (event) -> {
           this.isPvpFlagged = event.isPvpFlagged;
           if (this.isPvpFlagged && event.shouldStartCooldown) {
               this.flagObjectEntity.startCooldown();
           }
        });


        if (client.isServerClient()) {
            this.isPvpFlagged = getLevelModData().isPvpFlagged;
            new SettlementPvpToggleEvent(getLevelModData(), client.getServerClient(), false).applyAndSendToClient(client.getServerClient());
            new SettlementBasicsEvent(getLevelData()).applyAndSendToClient(client.getServerClient());
        }

        this.togglePvpFlag = this.registerAction(new EmptyCustomAction() {
            @Override
            protected void run() {
                if (client.isServerClient()) {
                    getLevelModData().togglePvpFlag();
                    boolean shouldStartCooldown = getLevelModData().isPvpFlagged;

                    new SettlementPvpToggleEvent(getLevelModData(), client.getServerClient(), shouldStartCooldown).applyAndSendToAllClients(client.getServerClient().getServer());
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

    public SettlementModData getLevelModData() {
        if (!this.client.isServerClient()) {
            throw new IllegalStateException("Cannot get level data client side");
        } else {
            return SettlementModData.getSettlementModData(this.getLevel());
        }
    }
}

package settlementexpansion.inventory.container;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import settlementexpansion.inventory.event.SettlementPvpToggleEvent;
import settlementexpansion.map.settlement.SettlementModData;
import settlementexpansion.object.entity.SettlementFlagModObjectEntity;

public class SettlementModContainer extends SettlementContainer {

    public EmptyCustomAction togglePvpFlag;
    public boolean isPvpFlagged;

    public SettlementModContainer(NetworkClient client, int uniqueSeed, SettlementFlagModObjectEntity objectEntity, Packet contentPacket) {
        super(client, uniqueSeed, objectEntity, contentPacket);
        if (client.isServerClient()) {
            this.isPvpFlagged = getLevelModData().isPvpFlagged;
        }

        this.subscribeEvent(SettlementPvpToggleEvent.class, (e) -> true, () -> true);
        this.onEvent(SettlementPvpToggleEvent.class, (event) -> {
           this.isPvpFlagged = event.isPvpFlagged;
        });

        this.togglePvpFlag = this.registerAction(new EmptyCustomAction() {
            @Override
            protected void run() {
                if (client.isServerClient()) {
                    getLevelModData().togglePvpFlag();
                    new SettlementPvpToggleEvent(getLevelModData(), client.getServerClient()).applyAndSendToAllClients(client.getServerClient().getServer());
                    new SettlementBasicsEvent(getLevelData()).applyAndSendToClient(client.getServerClient());
                }
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

package settlementexpansion.inventory.action;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import settlementexpansion.inventory.container.SettlementClaimContainer;

public class ForceChangeClaimAction extends EmptyCustomAction {

    public SettlementClaimContainer container;

    public ForceChangeClaimAction(SettlementClaimContainer container) {
        this.container = container;
    }

    @Override
    protected void run() {
        if (this.container.client.isServer()) {
            SettlementLevelData levelData = this.container.getLevelData();
            if (levelData != null) {
                ServerClient serverClient = this.container.client.getServerClient();
                int max = serverClient.getServer().world.settings.maxSettlementsPerPlayer;
                if (max > 0) {
                    long current = serverClient.getServer().levelCache.getSettlements().stream().filter((s) -> s.ownerAuth == serverClient.authentication).count();
                    if (current >= (long) max) {
                        serverClient.sendChatMessage(new LocalMessage("misc", "maxsettlementsreached", "count", max));
                        return;
                    }
                }

                serverClient.playerMob.getInv().main.removeItems(serverClient.playerMob.getLevel(), serverClient.playerMob, ItemRegistry.getItem("claimingscroll"), 1, "forceclaim");
                this.container.getLevelModData().setPvpFlagged(false);
                this.container.getLevelLayer().setOwner(serverClient);
                levelData.sendEvent(SettlementBasicsEvent.class);
            }
        }
    }
}

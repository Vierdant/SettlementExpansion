package settlementexpansion.listener;

import necesse.engine.GameEventListener;
import necesse.engine.events.players.MobInteractEvent;
import necesse.level.maps.layers.SettlementLevelLayer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import settlementexpansion.SettlementExpansion;
import settlementexpansion.map.settlement.SettlementModData;

public class MobInteractSettlementListener extends GameEventListener<MobInteractEvent> {

    @Override
    public void onEvent(MobInteractEvent event) {
        if (event.player != null && event.player.isServerClient() && SettlementExpansion.getSettings().enableSettlementLevelModification) {
            SettlementLevelData data = SettlementLevelData.getSettlementData(event.player.getLevel());
            if (data != null) {
                SettlementLevelLayer layer = event.player.getLevel().settlementLayer;
                SettlementModData layerData = SettlementModData.getSettlementModDataCreateIfNonExist(event.player.getLevel());
                if (!layer.doesClientHaveAccess(event.player.getServerClient()) && !layerData.isPvpFlagged) {
                    event.preventDefault();
                }
            }
        }
    }
}

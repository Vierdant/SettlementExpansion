package settlementexpansion.listener;

import necesse.engine.GameEventListener;
import necesse.engine.events.players.DamageTileEvent;
import necesse.level.maps.layers.SettlementLevelLayer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import settlementexpansion.SettlementExpansion;
import settlementexpansion.map.settlement.SettlementModData;

public class DamageTileSettlementListener extends GameEventListener<DamageTileEvent> {

    @Override
    public void onEvent(DamageTileEvent event) {
        if (event.client != null && event.client.isServerClient() && SettlementExpansion.getSettings().enableSettlementLevelModification) {
            SettlementLevelData data = SettlementLevelData.getSettlementData(event.level);
            if (data != null) {
                SettlementLevelLayer layer = event.level.settlementLayer;
                SettlementModData layerData = SettlementModData.getSettlementModDataCreateIfNonExist(event.level);
                if (!layer.doesClientHaveAccess(event.client) && !layerData.isPvpFlagged) {
                    event.preventDefault();
                }
            }
        }
    }
}

package settlementexpansion.registry;

import necesse.engine.registries.LevelDataRegistry;
import settlementexpansion.map.settlement.SettlementModData;

public class LevelDataModRegistry {


    public static void registerLevelData() {
        LevelDataRegistry.registerLevelData("settlementExpanded", SettlementModData.class);
    }
}

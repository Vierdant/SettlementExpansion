package settlementexpansion.inventory.lootTable;

import settlementexpansion.inventory.lootTable.preset.GeodeLootTable;
import settlementexpansion.inventory.lootTable.preset.JobResultLootTable;

public class LootTableModPresets {

    public static GeodeLootTable geode;
    public static JobResultLootTable studyingResult;

    public LootTableModPresets() {}

    static {
        geode = GeodeLootTable.geode;
        studyingResult = JobResultLootTable.studyingResult;
    }
}

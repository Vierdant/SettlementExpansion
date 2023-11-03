package settlementexpansion.inventory.lootTable;

import settlementexpansion.inventory.lootTable.preset.GeodeLootTable;

public class LootTableModPresets {

    public static GeodeLootTable geode;

    public LootTableModPresets() {}

    static {
        geode = GeodeLootTable.geode;
    }
}

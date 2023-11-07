package settlementexpansion.inventory.lootTable.preset;

import necesse.engine.network.server.ServerClient;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;

public class GeodeLootTable extends LootTable {

    public static final Object[] base = new Object[]{
            100, LootItem.between("stone", 2, 15),
            100, LootItem.between("copperore", 2, 6),
            75, LootItem.between("ironore", 2, 4),
            50, LootItem.between("goldore", 1, 4),
            25, new ConditionLootItem("quartz", (r, o) -> {
        ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
        return client.characterStats().mob_kills.getKills("evilsprotector") > 0;
    })
    };
    public static final Object[] casual = new Object[]{
            15, new LootItem("alamite"),
            15, new LootItem("amethyst"),
            15, new LootItem("calcite"),
            15, new LootItem("celestine"),
            15, new LootItem("jagoite"),
            15, new LootItem("malachite"),
            15, new LootItem("orpiment"),
            5, new LootItem("earthcrystal"),
    };

    public static final GeodeLootTable geode;


    private GeodeLootTable(Object[] ...loot) {
        super();
        Object[] finalObj = new Object[getTableSize(loot)];
        int round = 0;

        for (Object[] entry : loot) {
            for (Object item : entry) {
                finalObj[round] = item;
                round++;
            }
        }

        this.items.add(new OneOfTicketLootItems(finalObj));
    }

    private int getTableSize(Object[] ...loot) {
        int size = 0;
        for (Object[] entry : loot) {
            size += entry.length;
        }
        return size;
    }

    static {
        geode = new GeodeLootTable(base, casual);
    }

}

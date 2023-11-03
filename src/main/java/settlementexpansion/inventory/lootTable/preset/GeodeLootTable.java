package settlementexpansion.inventory.lootTable.preset;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import java.util.Collections;

public class GeodeLootTable extends LootTable {

    public static final Object[] base = new Object[]{
            75, new LootItem("stone"),
            100, new LootItem("torch")
    };
    public static final Object[] special = new Object[]{
            1, new LootItem("goldbar"),
            50, new LootItem("ironbar")
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
        geode = new GeodeLootTable(base, special);
    }

}

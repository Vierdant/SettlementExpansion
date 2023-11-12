package settlementexpansion.inventory.lootTable.preset;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;

public class JobResultLootTable extends LootTable {

    public static OneOfTicketLootItems studyingTable = new OneOfTicketLootItems(100, new LootItem("recallscroll"), 75, new LootItem("travelscroll"), 25, new LootItem("enchantingscroll"));
    public static JobResultLootTable studyingResult;


    public JobResultLootTable(LootItemInterface table) {
        this.items.add(table);
    }

    static {
        studyingResult = new JobResultLootTable(studyingTable);
    }

}

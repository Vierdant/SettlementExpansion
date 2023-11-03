package settlementexpansion.item.geode;

import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import settlementexpansion.inventory.lootTable.LootTableModPresets;

import java.util.ArrayList;
import java.util.List;

public class GeodeItem extends Item {
    private final String tooltipKey;
    private final int breakCost;

    public GeodeItem(Rarity rarity, String tooltipKey, int breakCost) {
        super(5);
        this.dropsAsMatDeathPenalty = true;
        this.setItemCategory("misc", "geodes");
        this.keyWords.add("geode");
        this.rarity = rarity;
        this.tooltipKey = tooltipKey;
        this.breakCost = breakCost;
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        if (this.tooltipKey != null) {
            tooltips.add(Localization.translate("itemtooltip", this.tooltipKey));
        }

        return tooltips;
    }

    public int getBreakCost() {
        return this.breakCost;
    }

    public int getRandomBreakCost(GameRandom random, int happiness) {
        return HumanShop.getRandomHappinessMiddlePrice(random, happiness, getBreakCost(), 3, 3);
    }

    public List<InventoryItem> getLootTable() {
        List<InventoryItem> list = new ArrayList<>();
        LootTableModPresets.geode.addItems(list, GameRandom.globalRandom, 1f);
        return list;
    }


}

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

    public float getBreakCostModifier() {
        List<Item> lootTable = this.getLootTable();
        float mod = 1F;

        for (Item entry : lootTable) {
            mod += getRarityCostModifier(entry.getRarity());
        }

        return mod;
    }

    public int getFinalBreakCost() {
        System.out.println((int)(getBreakCost() * getBreakCostModifier()));
        return (int)(getBreakCost() * getBreakCostModifier());
    }

    public int getRandomBreakCost(GameRandom random, int happiness) {
        return HumanShop.getRandomHappinessMiddlePrice(random, happiness, this.getFinalBreakCost(), 3, 3);
    }

    public List<Item> getLootTable() {
        List<Item> list = new ArrayList<>();
        String[] ids = new String[]{"leather", "torch", "stone"};
        for (String id : ids) {
            if (!ItemRegistry.itemExists(id)) {
                GameLog.warn.print("Could not add item with id " + id + " to Geode loot");
                continue;
            }

            list.add(ItemRegistry.getItem(id));
        }

        return list;
    }

    public float getRarityWeightModifier(Rarity rarity) {
        switch (rarity) {
            case COMMON: return 1F;
            case UNCOMMON: return 0.8F;
            case RARE: return 0.7F;
            case EPIC: return 0.4F;
            case LEGENDARY: return 0.2F;
            default: return 1.2F;
        }
    }

    public float getRarityCostModifier(Rarity rarity) {
        switch (rarity) {
            case COMMON: return 0.2F;
            case UNCOMMON: return 0.3F;
            case RARE: return 0.4F;
            case EPIC: return 0.5F;
            case LEGENDARY: return 0.8F;
            default: return 0.1F;
        }
    }


}

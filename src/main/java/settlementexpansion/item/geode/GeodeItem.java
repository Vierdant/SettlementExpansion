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
    private final float costModifier;
    private final int breakCost;

    public GeodeItem(Rarity rarity, String tooltipKey, int breakCost, float costModifier) {
        super(5);
        this.dropsAsMatDeathPenalty = true;
        this.setItemCategory("misc", "geodes");
        this.keyWords.add("geode");
        this.rarity = rarity;
        this.tooltipKey = tooltipKey;
        this.breakCost = breakCost;
        this.costModifier = costModifier;
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        if (this.tooltipKey != null) {
            tooltips.add(Localization.translate("itemtooltip", this.tooltipKey));
        }

        return tooltips;
    }

    public float getCostModifier() {
        return this.costModifier;
    }

    public int getBreakCost() {
        return this.breakCost;
    }

    public int getFinalBreakCost() {
        return (int)((float)this.getBreakCost() * this.getCostModifier());
    }

    public int getRandomBreakCost(GameRandom random, int happiness) {
        return HumanShop.getRandomHappinessMiddlePrice(random, happiness, this.getFinalBreakCost(), 6, 3);
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

    public float getRarityModifier(Rarity rarity) {
        switch (rarity) {
            case COMMON: return 1F;
            case UNCOMMON: return 0.8F;
            case RARE: return 0.7F;
            case EPIC: return 0.4F;
            case LEGENDARY: return 0.2F;
            default: return 1.2F;
        }
    }


}

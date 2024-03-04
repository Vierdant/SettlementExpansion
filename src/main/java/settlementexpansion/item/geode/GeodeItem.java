package settlementexpansion.item.geode;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
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
    private final float maxMultiplier;
    private float multiplier;

    public GeodeItem(Rarity rarity, String tooltipKey, int breakCost, float maxMultiplier) {
        super(20);
        this.dropsAsMatDeathPenalty = true;
        this.setItemCategory("misc", "geodes");
        this.keyWords.add("geode");
        this.rarity = rarity;
        this.tooltipKey = tooltipKey;
        this.breakCost = breakCost;
        this.maxMultiplier = maxMultiplier;
        updateMultiplier();
    }

    public GeodeItem(String tooltipKey, int breakCost, float maxMultiplier) {
        this(Rarity.NORMAL, tooltipKey, breakCost, maxMultiplier);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        if (this.tooltipKey != null) {
            tooltips.add(Localization.translate("itemtooltip", this.tooltipKey));
        }

        return tooltips;
    }

    public void updateMultiplier() {
        this.multiplier = GameRandom.globalRandom.getFloatBetween(1F, Math.max(maxMultiplier, 1F));
    }

    public int getBreakCost() {
        return (int)(this.breakCost * this.multiplier);
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

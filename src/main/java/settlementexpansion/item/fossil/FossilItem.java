package settlementexpansion.item.fossil;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class FossilItem extends Item {
    private final String tooltipKey;

    public FossilItem(Rarity rarity, String tooltipKey) {
        super(20);
        this.dropsAsMatDeathPenalty = true;
        this.setItemCategory("misc", "fossils");
        this.keyWords.add("fossil");
        this.rarity = rarity;
        this.tooltipKey = tooltipKey;
    }

    public FossilItem(String tooltipKey) {
        this(Rarity.NORMAL, tooltipKey);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        if (this.tooltipKey != null) {
            tooltips.add(Localization.translate("itemtooltip", this.tooltipKey));
        }

        return tooltips;
    }

}

package settlementexpansion.item.trinket;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.SimpleTrinketItem;

public class SimpleDescriptiveTrinketItem extends SimpleTrinketItem {
    private final String tooltipKey;

    public SimpleDescriptiveTrinketItem(Rarity rarity, String[] buffStringIDs, String tooltipKey, int enchantCost) {
        super(rarity, buffStringIDs, enchantCost);
        this.tooltipKey = tooltipKey;
    }

    public SimpleDescriptiveTrinketItem(Rarity rarity, String buffStringID, String tooltipKey, int enchantCost) {
        super(rarity, buffStringID, enchantCost);
        this.tooltipKey = tooltipKey;
    }

    public ListGameTooltips getTrinketTooltips(InventoryItem item, PlayerMob perspective, boolean equipped) {
        ListGameTooltips tooltips = super.getTrinketTooltips(item, perspective, equipped);
        tooltips.add(Localization.translate("itemtooltip", this.tooltipKey));
        return tooltips;
    }
}

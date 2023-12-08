package settlementexpansion.item.trinket;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.SimpleTrinketItem;

public class SimpleTippedTrinketItem extends SimpleTrinketItem {

    public SimpleTippedTrinketItem(Rarity rarity, String buffStringIDs, int enchantCost) {
        super(rarity, buffStringIDs, enchantCost);
    }

    @Override
    public ListGameTooltips getTrinketTooltips(InventoryItem item, PlayerMob perspective, boolean equipped) {
        ListGameTooltips tooltips = super.getTrinketTooltips(item, perspective, equipped);
        tooltips.add(Localization.translate("itemtooltip", this.getStringID()));
        return tooltips;
    }
}

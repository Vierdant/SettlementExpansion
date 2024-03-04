package settlementexpansion.item.trinket;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.SimpleTrinketItem;

public class SimpleTippedTrinketItem extends SimpleTrinketItem {

    public SimpleTippedTrinketItem(Rarity rarity, String buffStringIDs, int enchantCost) {
        super(rarity, buffStringIDs, enchantCost);
    }

    /*@Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getToolTips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) ;
        tooltips.add(Localization.translate("itemtooltip", this.getStringID()));
        return tooltips;
    }*/
}

package settlementexpansion.item.tool;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.GameDamage;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;

public class BrushToolItem extends ToolDamageItem {
    public BrushToolItem(int enchantCost) {
        super(enchantCost);
        this.setItemCategory("equipment", "tools", "brushes");
        this.keyWords.add("brush");
        this.toolType = ToolType.SHOVEL;
        this.animInverted = true;
        this.animAttacks = 2;
        this.width = 10.0F;
        this.animSpeed = 500;
        this.attackDamage = new GameDamage(10);
        this.attackRange = 50;
        this.knockback = 50;
    }

    protected void addToolTooltips(ListGameTooltips tooltips) {
        tooltips.add(Localization.translate("itemtooltip", "brushtip"));
    }
}

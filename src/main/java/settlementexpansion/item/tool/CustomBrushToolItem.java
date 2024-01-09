package settlementexpansion.item.tool;

import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import settlementexpansion.tile.MudFossilTile;

public class CustomBrushToolItem extends BrushToolItem {

    public CustomBrushToolItem(int toolDps, int toolTier) {
        super(100);
        this.toolDps = toolDps;
        this.toolTier = toolTier;
    }

    public CustomBrushToolItem(int toolDps, int toolTier, int enchantCost, Item.Rarity rarity) {
        this(toolDps, toolTier);
        this.rarity = rarity;
    }

    @Override
    public boolean canSmartMineTile(Level level, int tileX, int tileY, PlayerMob player, InventoryItem item) {
        GameTile tile = level.getTile(tileX, tileY);
        return tile instanceof MudFossilTile;
    }

    @Override
    public boolean canDamageTile(Level level, int tileX, int tileY, PlayerMob player, InventoryItem item) {
        GameTile tile = level.getTile(tileX, tileY);
        return tile instanceof MudFossilTile;
    }

    @Override
    public boolean isEnchantable(InventoryItem item) {
        return false;
    }
}

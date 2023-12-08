package settlementexpansion.object.furniture;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;

import java.awt.*;

public class SafeBoxInventoryObject extends LockedInventoryObject {
    public SafeBoxInventoryObject(String textureName, int slots, ToolType toolType, Color mapColor) {
        super(textureName, slots, new Rectangle(32, 32), toolType, mapColor);
    }

    public SafeBoxInventoryObject(String textureName, int slots, Color mapColor) {
        super(textureName, slots, new Rectangle(32, 32), mapColor);
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        return rotation % 2 == 0 ? new Rectangle(x * 32 + 3, y * 32 + 6, 26, 20) : new Rectangle(x * 32 + 6, y * 32 + 4, 20, 24);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "safebox"));
        return tooltips;
    }
}

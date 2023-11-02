package settlementexpansion.object.furniture;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.furniture.InventoryObject;
import necesse.level.maps.Level;
import settlementexpansion.inventory.container.OELockedInventoryContainer;
import settlementexpansion.object.entity.LockedInventoryObjectEntity;
import settlementexpansion.registry.ContainerModRegistry;
import settlementexpansion.registry.ModResources;

import java.awt.*;

public class LockedInventoryObject extends InventoryObject {

    public LockedInventoryObject(String textureName, int slots, Rectangle collision, ToolType toolType, Color mapColor) {
        super(textureName, slots, collision, toolType, mapColor);
    }

    public LockedInventoryObject(String textureName, int slots, Rectangle collision, Color mapColor) {
        super(textureName, slots, collision, mapColor);
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServerLevel()) {
            ObjectEntity entity = level.entityManager.getObjectEntity(x, y);
            LockedInventoryObjectEntity oe = ((LockedInventoryObjectEntity)entity);

            if (oe.getLockState() && oe.getCurrentOwner() != player.getNetworkClient().authentication) {
                return;
            }
            OELockedInventoryContainer.openAndSendContainer(ContainerModRegistry.OE_LOCKED_INVENTORY_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    public void playOpenSound(Level level, int tileX, int tileY) {
        if (this.openTexture != null) {
            Screen.playSound(ModResources.safe_open, SoundEffect.effect((float)(tileX * 32 + 16), (float)(tileY * 32 + 16)));
        }

    }

    public void playCloseSound(Level level, int tileX, int tileY) {
        if (this.openTexture != null) {
            Screen.playSound(ModResources.safe_close, SoundEffect.effect((float)(tileX * 32 + 16), (float)(tileY * 32 + 16)));
        }

    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new LockedInventoryObjectEntity(level, x, y, this.slots);
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }
}

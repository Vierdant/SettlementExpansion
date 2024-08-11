package settlementexpansion.object.furniture;

import necesse.engine.localization.Localization;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.furniture.InventoryObject;
import necesse.level.maps.Level;
import settlementexpansion.inventory.container.LockedInventoryContainer;
import settlementexpansion.object.entity.LockedInventoryObjectEntity;
import settlementexpansion.registry.ContainerModRegistry;
import settlementexpansion.ModResources;

import java.awt.*;

public class LockedInventoryObject extends InventoryObject {

    public LockedInventoryObject(String textureName, int slots, Rectangle collision, ToolType toolType, Color mapColor) {
        super(textureName, slots, collision, toolType, mapColor);
    }

    public LockedInventoryObject(String textureName, int slots, Rectangle collision, Color mapColor) {
        super(textureName, slots, collision, mapColor);
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer()) {
            ObjectEntity entity = level.entityManager.getObjectEntity(x, y);
            LockedInventoryObjectEntity oe = ((LockedInventoryObjectEntity)entity);

            if (oe.getLockState() && oe.getCurrentOwner() != player.getNetworkClient().authentication) {
                return;
            }
            LockedInventoryContainer.openAndSendContainer(ContainerModRegistry.OE_LOCKED_INVENTORY_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    @Override
    public void playOpenSound(Level level, int tileX, int tileY) {
        if (this.openTexture != null) {
            SoundManager.playSound(ModResources.safe_open, SoundEffect.effect((float)(tileX * 32 + 16), (float)(tileY * 32 + 16)));
        }

    }

    @Override
    public void playCloseSound(Level level, int tileX, int tileY) {
        if (this.openTexture != null) {
            SoundManager.playSound(ModResources.safe_close, SoundEffect.effect((float)(tileX * 32 + 16), (float)(tileY * 32 + 16)));
        }

    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new LockedInventoryObjectEntity(level, x, y, this.slots);
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }
}

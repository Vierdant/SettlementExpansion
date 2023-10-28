package settlementexpansion.object;

import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.DresserObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.WorkstationObject;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class DryingRackObject extends GameObject {
    public GameTexture texture;

    public DryingRackObject() {
        super(new Rectangle(32, 32));
        this.toolType = ToolType.ALL;
        this.mapColor = new Color(0, 0, 0);
        this.objectHealth = 50;
        this.drawDmg = false;
        this.isLightTransparent = true;
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/dryingrack");
    }

    public void addDrawables(java.util.List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        final TextureDrawOptions options = this.texture.initDraw().sprite(rotation % 4, 0, 32, this.texture.getHeight()).light(light).pos(drawX, drawY - this.texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().sprite(rotation % 4, 0, 32, this.texture.getHeight()).alpha(alpha).draw(drawX, drawY - this.texture.getHeight() + 32);
    }

    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 2, y * 32 + 12, 28, 18);
        } else if (rotation == 1) {
            return new Rectangle(x * 32, y * 32 + 2, 22, 28);
        } else {
            return rotation == 2 ? new Rectangle(x * 32 + 2, y * 32 + 2, 28, 18) : new Rectangle(x * 32 + 10, y * 32 + 2, 22, 28);
        }
    }

    public java.util.List<ObjectHoverHitbox> getHoverHitboxes(Level level, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, tileX, tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        if (rotation == 0) {
            list.add(new ObjectHoverHitbox(tileX, tileY, 0, -12, 32, 12));
        } else if (rotation == 1) {
            list.add(new ObjectHoverHitbox(tileX, tileY, 0, -16, 22, 16));
        } else if (rotation == 2) {
            list.add(new ObjectHoverHitbox(tileX, tileY, 0, -16, 32, 16));
        } else {
            list.add(new ObjectHoverHitbox(tileX, tileY, 10, -16, 22, 16));
        }

        return list;
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return false;
    }

//    public void interact(Level level, int x, int y, PlayerMob player) {
//        if (level.isServerLevel()) {
//            OEInventoryContainer.openAndSendContainer(ContainerRegistry.DRESSER_CONTAINER, player.getServerClient(), level, x, y);
//        }
//
//    }
//
//    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
//        return new DresserObjectEntity(level, x, y);
//    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "dryingracktip"));
        return tooltips;
    }
}

package settlementexpansion.object.trap;

import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.TrapObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import settlementexpansion.object.entity.CannonTrapObjectEntity;

import java.awt.*;
import java.util.List;

public class CannonTrapObject extends TrapObject {

    public GameTexture texture;

    public CannonTrapObject() {
        super(new Rectangle(32, 32));
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/cannontrap");
    }

    @Override
    public void addDrawables(java.util.List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameLight light = level.getLightLevel(tileX, tileY);
        final TextureDrawOptions drawOptions = this.texture.initDraw().sprite(rotation % 4, 0, 32, this.texture.getHeight()).light(light).pos(drawX, drawY - this.texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().sprite(rotation % 4, 0, 32, this.texture.getHeight()).alpha(alpha).draw(drawX, drawY - this.texture.getHeight() + 32);
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 8, y * 32 + 18, 16, 6);
        } else if (rotation == 1) {
            return new Rectangle(x * 32 + 8, y * 32 + 10, 6, 14);
        } else if (rotation == 2) {
            return new Rectangle(x * 32 + 8, y * 32 + 12, 16, 6);
        } else {
            return rotation == 3 ? new Rectangle(x * 32 + 18, y * 32 + 10, 6, 14) : new Rectangle(0, 0);
        }
    }

    @Override
    public java.util.List<ObjectHoverHitbox> getHoverHitboxes(Level level, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, tileX, tileY);
        list.add(new ObjectHoverHitbox(tileX, tileY, 0, -16, 32, 16));
        return list;
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new CannonTrapObjectEntity(level, x, y, 10000L);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "cannontrap"));
        tooltips.add(Localization.translate("itemtooltip", "activatedwiretip"));
        return tooltips;
    }
}

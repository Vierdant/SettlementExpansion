package settlementexpansion.object.furniture;

import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;

import java.awt.*;
import java.util.List;

public class StudyTable2Object extends FurnitureObject {

    protected int counterID;
    public GameTexture texture;

    public StudyTable2Object() {
        super(new Rectangle(32, 32));
        this.objectHealth = 50;
        this.drawDmg = false;
        this.isLightTransparent = true;
        this.furnitureType = "studytable";
        this.toolType = ToolType.ALL;
    }

    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(1, 0, 2, 1, rotation, false, this.counterID, this.getID());
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/studytable");
    }

    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32, y * 32 + 6, 20, 20);
        } else if (rotation == 1) {
            return new Rectangle(x * 32 + 5, y * 32 + 16, 22, 16);
        } else {
            return rotation == 2 ? new Rectangle(x * 32 + 12, y * 32 + 6, 20, 20) : new Rectangle(x * 32 + 5, y * 32, 22, 26);
        }
    }

    public java.util.List<ObjectHoverHitbox> getHoverHitboxes(Level level, int tileX, int tileY) {
        java.util.List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, tileX, tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        if (rotation == 1 || rotation == 3) {
            list.add(new ObjectHoverHitbox(tileX, tileY, 0, -16, 32, 16));
        }

        return list;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int rotation = level.getObjectRotation(tileX, tileY);

        int bookAddition = this.getMultiTile(rotation).getMasterLevelObject(level, tileX, tileY).map(StudyTableObject::hasBook).orElse(false) ? 2 : 0;

        final DrawOptionsList options = new DrawOptionsList();
        if (rotation == 0) {
            options.add(this.texture.initDraw().sprite(1 + bookAddition, 4, 32).light(light).pos(drawX, drawY - 24));
            options.add(this.texture.initDraw().sprite(1 + bookAddition, 5, 32).light(light).pos(drawX, drawY + 8));
        } else if (rotation == 1) {
            options.add(this.texture.initDraw().sprite(bookAddition, 1, 32).light(light).pos(drawX, drawY + 2));
        } else if (rotation == 2) {
            options.add(this.texture.initDraw().sprite(bookAddition, 2, 32).light(light).pos(drawX, drawY - 24));
            options.add(this.texture.initDraw().sprite(bookAddition, 3, 32).light(light).pos(drawX, drawY + 8));
        } else {
            options.add(this.texture.initDraw().sprite(1 + bookAddition, 0, 32).light(light).pos(drawX, drawY + 2));
        }

        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        this.getMultiTile(level.getObjectRotation(x, y)).getMasterLevelObject(level, x, y).ifPresent((e) -> {
            e.interact(player);
        });
    }
}

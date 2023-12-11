package settlementexpansion.object;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.CraftingStationObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;
import settlementexpansion.registry.RecipeTechModRegistry;

import java.awt.*;
import java.util.List;

public class BlueprintTableObject extends CraftingStationObject {
    public GameTexture texture;
    protected int counterID;

    public BlueprintTableObject() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(150, 119, 70);
        this.drawDamage = false;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 1, 1, 2, rotation, true, this.counterID, this.getID());
    }

    @Override
    public int getPlaceRotation(Level level, int levelX, int levelY, PlayerMob player, int playerDir) {
        return Math.floorMod(super.getPlaceRotation(level, levelX, levelY, player, playerDir) - 1, 4);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/blueprinttable");
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 5, y * 32, 22, 26);
        } else if (rotation == 1) {
            return new Rectangle(x * 32 + 12, y * 32 + 6, 20, 20);
        } else {
            return rotation == 2 ? new Rectangle(x * 32 + 5, y * 32 + 16, 22, 16) : new Rectangle(x * 32, y * 32 + 6, 20, 20);
        }
    }

    @Override
    public java.util.List<ObjectHoverHitbox> getHoverHitboxes(Level level, int tileX, int tileY) {
        java.util.List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, tileX, tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        if (rotation == 1 || rotation == 3) {
            list.add(new ObjectHoverHitbox(tileX, tileY, 0, -16, 32, 16));
        }

        return list;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int rotation = level.getObjectRotation(tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        if (rotation == 0) {
            options.add(this.texture.initDraw().sprite(1, 1, 32).light(light).pos(drawX, drawY + 2));
        } else if (rotation == 1) {
            options.add(this.texture.initDraw().sprite(1, 2, 32).mirrorX().light(light).pos(drawX, drawY - 24));
            options.add(this.texture.initDraw().sprite(1, 3, 32).mirrorX().light(light).pos(drawX, drawY + 8));
        } else if (rotation == 2) {
            options.add(this.texture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY + 2));
        } else {
            options.add(this.texture.initDraw().sprite(1, 2, 32).light(light).pos(drawX, drawY - 24));
            options.add(this.texture.initDraw().sprite(1, 3, 32).light(light).pos(drawX, drawY + 8));
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

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        if (rotation == 0) {
            this.texture.initDraw().sprite(1, 1, 32).alpha(alpha).draw(drawX, drawY + 2);
            this.texture.initDraw().sprite(1, 0, 32).alpha(alpha).draw(drawX, drawY - 32 + 2);
        } else if (rotation == 1) {
            this.texture.initDraw().sprite(0, 2, 32).mirrorX().alpha(alpha).draw(drawX + 32, drawY - 24);
            this.texture.initDraw().sprite(1, 2, 32).mirrorX().alpha(alpha).draw(drawX, drawY - 24);
            this.texture.initDraw().sprite(0, 3, 32).mirrorX().alpha(alpha).draw(drawX + 32, drawY + 8);
            this.texture.initDraw().sprite(1, 3, 32).mirrorX().alpha(alpha).draw(drawX, drawY + 8);
        } else if (rotation == 2) {
            this.texture.initDraw().sprite(0, 0, 32).alpha(alpha).draw(drawX, drawY + 2);
            this.texture.initDraw().sprite(0, 1, 32).alpha(alpha).draw(drawX, drawY + 32 + 2);
        } else {
            this.texture.initDraw().sprite(0, 2, 32).alpha(alpha).draw(drawX - 32, drawY - 24);
            this.texture.initDraw().sprite(1, 2, 32).alpha(alpha).draw(drawX, drawY - 24);
            this.texture.initDraw().sprite(0, 3, 32).alpha(alpha).draw(drawX - 32, drawY + 8);
            this.texture.initDraw().sprite(1, 3, 32).alpha(alpha).draw(drawX, drawY + 8);
        }

    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechModRegistry.BLUEPRINTTABLE};
    }

    public static void registerBlueprintTable() {
        BlueprintTableObject cb1o = new BlueprintTableObject();
        BlueprintTable2Object cb2o = new BlueprintTable2Object();
        cb1o.counterID = ObjectRegistry.registerObject("blueprinttable2", cb2o, 0.0F, false);
        cb2o.counterID = ObjectRegistry.registerObject("blueprinttable", cb1o, 20.0F, true);
    }
}

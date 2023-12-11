package settlementexpansion.object;

import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;
import settlementexpansion.object.entity.ToolsRackObjectEntity;
import settlementexpansion.registry.ContainerModRegistry;

import java.awt.*;
import java.util.List;

public class ToolsRackObject extends GameObject {
    private GameTexture texture;
    private int counterID;

    public ToolsRackObject() {
        super(new Rectangle(32, 32));
        this.toolType = ToolType.ALL;
        this.mapColor = new Color(0, 0, 0);
        this.objectHealth = 50;
        this.drawDamage = false;
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
        this.texture = GameTexture.fromFile("objects/toolsrack");
    }

    @Override
    public void addDrawables(java.util.List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int rotation = level.getObjectRotation(tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        int toolsDisplay = isInventoryFull(level, tileX, tileY) ? 2 : 0;

        if (rotation == 0) {
            options.add(this.texture.initDraw().sprite(1 + toolsDisplay, 1, 32).light(light).pos(drawX, drawY + 2));
        } else if (rotation == 1) {
            options.add(this.texture.initDraw().sprite(1 + toolsDisplay, 2, 32).mirrorX().light(light).pos(drawX, drawY - 24));
            options.add(this.texture.initDraw().sprite(1 + toolsDisplay, 3, 32).mirrorX().light(light).pos(drawX, drawY + 8));
        } else if (rotation == 2) {
            options.add(this.texture.initDraw().sprite(toolsDisplay, 0, 32).light(light).pos(drawX, drawY + 2));
        } else {
            options.add(this.texture.initDraw().sprite(1 + toolsDisplay, 2, 32).light(light).pos(drawX, drawY - 24));
            options.add(this.texture.initDraw().sprite(1 + toolsDisplay, 3, 32).light(light).pos(drawX, drawY + 8));
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
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, tileX, tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        if (rotation == 1 || rotation == 3) {
            list.add(new ObjectHoverHitbox(tileX, tileY, 0, -16, 32, 16));
        }

        return list;
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer()) {
            OEInventoryContainer.openAndSendContainer(ContainerModRegistry.TOOLSRACK_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new ToolsRackObjectEntity(level, x, y);
    }

    protected static boolean isInventoryFull(Level level, int tileX, int tileY) {
        ToolsRackObjectEntity ent = (ToolsRackObjectEntity) level.entityManager.getObjectEntity(tileX, tileY);
        if (ent != null) {
            return ent.isInventoryFull();
        }

        return false;
    }

    protected static boolean isInventoryFull(LevelObject levelObject) {
        return isInventoryFull(levelObject.level, levelObject.tileX, levelObject.tileY);
    }

    public static void registerToolsRack() {
        ToolsRackObject cb1o = new ToolsRackObject();
        ToolsRack2Object cb2o = new ToolsRack2Object();
        cb1o.counterID = ObjectRegistry.registerObject("toolsrack2", cb2o, 0.0F, false);
        cb2o.counterID = ObjectRegistry.registerObject("toolsrack", cb1o, 20.0F, true);
    }
}

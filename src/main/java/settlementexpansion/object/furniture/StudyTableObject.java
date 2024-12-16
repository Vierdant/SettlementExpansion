package settlementexpansion.object.furniture;

import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;
import settlementexpansion.object.entity.StudyTableObjectEntity;
import settlementexpansion.registry.ContainerModRegistry;

import java.awt.*;
import java.util.List;

public class StudyTableObject extends FurnitureObject {

    protected int counterID;
    public GameTexture texture;

    public StudyTableObject() {
        super(new Rectangle(32, 32));
        this.objectHealth = 50;
        this.drawDamage = false;
        this.isLightTransparent = true;
        this.furnitureType = "studytable";
        this.toolType = ToolType.ALL;
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 0, 2, 1, rotation, true, this.getID(), this.counterID);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/studytable");
    }

    @Override
    public void addDrawables(java.util.List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int rotation = level.getObjectRotation(tileX, tileY);
        int bookAddition = hasBook(level, tileX, tileY) ? 2 : 0;

        final DrawOptionsList options = new DrawOptionsList();
        if (rotation == 0) {
            options.add(this.texture.initDraw().sprite(bookAddition, 4, 32).light(light).pos(drawX, drawY - 24));
            options.add(this.texture.initDraw().sprite(bookAddition, 5, 32).light(light).pos(drawX, drawY + 8));
        } else if (rotation == 1) {
            options.add(this.texture.initDraw().sprite(bookAddition, 0, 32).light(light).pos(drawX, drawY + 2));
        } else if (rotation == 2) {
            options.add(this.texture.initDraw().sprite(1 + bookAddition, 2, 32).light(light).pos(drawX, drawY - 24));
            options.add(this.texture.initDraw().sprite(1 + bookAddition, 3, 32).light(light).pos(drawX, drawY + 8));
        } else {
            options.add(this.texture.initDraw().sprite(1 + bookAddition, 1, 32).light(light).pos(drawX, drawY + 2));
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
            this.texture.initDraw().sprite(0, 4, 32).alpha(alpha).draw(drawX, drawY - 24);
            this.texture.initDraw().sprite(1, 4, 32).alpha(alpha).draw(drawX + 32, drawY - 24);
            this.texture.initDraw().sprite(0, 5, 32).alpha(alpha).draw(drawX, drawY + 8);
            this.texture.initDraw().sprite(1, 5, 32).alpha(alpha).draw(drawX + 32, drawY + 8);
        } else if (rotation == 1) {
            this.texture.initDraw().sprite(0, 0, 32).alpha(alpha).draw(drawX, drawY + 2);
            this.texture.initDraw().sprite(0, 1, 32).alpha(alpha).draw(drawX, drawY + 32 + 2);
        } else if (rotation == 2) {
            this.texture.initDraw().sprite(0, 2, 32).alpha(alpha).draw(drawX - 32, drawY - 24);
            this.texture.initDraw().sprite(1, 2, 32).alpha(alpha).draw(drawX, drawY - 24);
            this.texture.initDraw().sprite(0, 3, 32).alpha(alpha).draw(drawX - 32, drawY + 8);
            this.texture.initDraw().sprite(1, 3, 32).alpha(alpha).draw(drawX, drawY + 8);
        } else {
            this.texture.initDraw().sprite(1, 1, 32).alpha(alpha).draw(drawX, drawY + 2);
            this.texture.initDraw().sprite(1, 0, 32).alpha(alpha).draw(drawX, drawY - 32 + 2);
        }

    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 12, y * 32 + 6, 20, 20);
        } else if (rotation == 1) {
            return new Rectangle(x * 32 + 5, y * 32, 22, 26);
        } else {
            return rotation == 2 ? new Rectangle(x * 32, y * 32 + 6, 20, 20) : new Rectangle(x * 32 + 5, y * 32 + 16, 22, 16);
        }
    }

    @Override
    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int layerID, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, layerID, tileX, tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        if (rotation == 1 || rotation == 3) {
            list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 0, -16, 32, 16));
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
            OEInventoryContainer.openAndSendContainer(ContainerModRegistry.STUDYTABLE_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new StudyTableObjectEntity(level, x, y);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "dressertip"));
        return tooltips;
    }

    protected static boolean hasBook(Level level, int tileX, int tileY) {
        StudyTableObjectEntity ent = (StudyTableObjectEntity) level.entityManager.getObjectEntity(tileX, tileY);
        if (ent != null) {
            return ent.getMaterialCount() > 0;
        }

        return false;
    }

    protected static boolean hasBook(LevelObject levelObject) {
        return hasBook(levelObject.level, levelObject.tileX, levelObject.tileY);
    }

    public static void registerSeedlingTable() {
        StudyTableObject cb1o = new StudyTableObject();
        StudyTable2Object cb2o = new StudyTable2Object();
        cb1o.counterID = ObjectRegistry.registerObject("studytable2", cb2o, 0.0F, false);
        cb2o.counterID = ObjectRegistry.registerObject("studytable", cb1o, 20.0F, true);
    }
}

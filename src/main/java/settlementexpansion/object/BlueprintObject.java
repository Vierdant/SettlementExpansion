package settlementexpansion.object;

import necesse.engine.GameEvents;
import necesse.engine.events.loot.ObjectLootTableDropsEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import settlementexpansion.map.preset.BlueprintPresetID;
import settlementexpansion.object.entity.BlueprintObjectEntity;
import settlementexpansion.registry.ContainerModRegistry;
import settlementexpansion.registry.RecipeTechModRegistry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlueprintObject extends GameObject {

    public GameTexture texture;
    private final BlueprintPresetID presetId;
    private final String blueprintKey;
    private final String furnitureType;
    private final boolean canChangeWalls;
    private final boolean canChangeFloor;
    private final boolean canPlaceOnLiquid;
    private final boolean canPlaceOnShore;

    public BlueprintObject(BlueprintPresetID presetId, String blueprintKey, String furnitureType, boolean canChangeWalls, boolean canChangeFloor, boolean canPlaceOnShore, boolean canPlaceOnLiquid) {
        super(new Rectangle(32, 32));
        this.toolType = ToolType.ALL;
        this.mapColor = new Color(42, 59, 171);
        this.objectHealth = 50;
        this.drawDamage = false;
        this.isLightTransparent = true;
        this.presetId = presetId;
        this.blueprintKey = blueprintKey;
        this.furnitureType = furnitureType;
        this.canChangeWalls = canChangeWalls;
        this.canChangeFloor = canChangeFloor;
        this.canPlaceOnShore = canPlaceOnShore;
        this.canPlaceOnLiquid = canPlaceOnLiquid;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/blueprint");
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", blueprintKey);
    }

    @Override
    public GameTexture generateItemTexture() {
        return GameTexture.fromFile("items/blueprint");
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
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer()) {
            OEInventoryContainer.openAndSendContainer(ContainerModRegistry.BLUEPRINT_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new BlueprintObjectEntity(level, presetId, x, y, furnitureType, canChangeWalls, canChangeFloor, canPlaceOnLiquid, canPlaceOnShore);
    }

    @Override
    public void onDestroyed(Level level, int x, int y, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        ObjectLootTableDropsEvent dropsEvent;
        if (itemsDropped != null && client != null) {
            ArrayList<InventoryItem> drops = this.getDroppedItems(level, x, y);
            GameEvents.triggerEvent(dropsEvent = new ObjectLootTableDropsEvent(new LevelObject(level, x, y), new Point(x * 32 + 16, y * 32 + 16), drops));
            if (dropsEvent.dropPos != null && dropsEvent.drops != null) {
                for (InventoryItem item : dropsEvent.drops) {
                    ItemPickupEntity droppedItem = item.getPickupEntity(level, (float) dropsEvent.dropPos.x, (float) dropsEvent.dropPos.y);
                    level.entityManager.pickups.add(droppedItem);
                    itemsDropped.add(droppedItem);
                }
            }
        }

        if (client != null) {
            client.newStats.objects_mined.increment(1);
        }

        if (!level.isServer()) {
            this.spawnDestroyedParticles(level, x, y);
        }

        ObjectEntity objectEntity = level.entityManager.getObjectEntity(x, y);
        level.setObject(x, y, 0);
        if (objectEntity != null) {
            objectEntity.onObjectDestroyed(this, client, itemsDropped);
            objectEntity.remove();
        }

    }

    public static void registerBlueprints() {
        ObjectRegistry.registerObject("blueprinthouse1empty", new BlueprintObject(BlueprintPresetID.HOUSE_1_EMPTY, "house1empty", null, true, true, false, false), 5, true);
        ObjectRegistry.registerObject("blueprinthouse1", new BlueprintObject(BlueprintPresetID.HOUSE_1, "house1", "spruce", true, true, false, false), 5, true);
        ObjectRegistry.registerObject("blueprinthouse2empty", new BlueprintObject(BlueprintPresetID.HOUSE_2_EMPTY, "house2empty", null, true, true, false, false), 5, true);
        ObjectRegistry.registerObject("blueprinthouse2", new BlueprintObject(BlueprintPresetID.HOUSE_2, "house2", "spruce", true, true, false, false), 5, true);
        ObjectRegistry.registerObject("blueprinthouse3empty", new BlueprintObject(BlueprintPresetID.HOUSE_3_EMPTY, "house3empty", null, true, true, false, false), 5, true);
        ObjectRegistry.registerObject("blueprinthouse3", new BlueprintObject(BlueprintPresetID.HOUSE_3, "house3", "spruce", true, true, false, false), 5, true);
        ObjectRegistry.registerObject("trainingarea", new BlueprintObject(BlueprintPresetID.TRAINING_AREA, "trainingarea", null, false, false, false, false), 5, true);
        ObjectRegistry.registerObject("storageroom", new BlueprintObject(BlueprintPresetID.STORAGE_ROOM, "storageroom", null, true, true, false, false), 5, true);
        ObjectRegistry.registerObject("storageroombig", new BlueprintObject(BlueprintPresetID.STORAGE_ROOM_BIG, "storageroombig", null, true, true, false, false), 5, true);
    }

    public static void registerBlueprintRecipes() {
        String[] list = new String[]{
                "blueprinthouse1empty", "blueprinthouse1",
                "blueprinthouse2empty", "blueprinthouse2",
                "blueprinthouse3empty", "blueprinthouse3",
                "storageroom",};

        for (String entry : list) {
            Recipes.registerModRecipe(new Recipe(
                    entry,
                    1,
                    RecipeTechModRegistry.BLUEPRINTTABLE,
                    new Ingredient[]{
                            new Ingredient("blueprintempty", 1)

                    }
            ));
        }
    }
}

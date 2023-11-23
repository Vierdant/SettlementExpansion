package settlementexpansion.object;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.tools.PresetPasteGameTool;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import settlementexpansion.map.preset.BlueprintPreset;
import settlementexpansion.map.preset.BlueprintPresetID;
import settlementexpansion.object.entity.BlueprintObjectEntity;
import settlementexpansion.object.entity.ToolsRackObjectEntity;
import settlementexpansion.registry.ContainerModRegistry;
import settlementexpansion.registry.RecipeTechModRegistry;

import java.awt.*;
import java.util.List;

public class BlueprintObject extends GameObject {

    public GameTexture texture;
    private final BlueprintPresetID presetId;

    public BlueprintObject(BlueprintPresetID presetId) {
        super(new Rectangle(32, 32));
        this.toolType = ToolType.ALL;
        this.mapColor = new Color(42, 59, 171);
        this.objectHealth = 50;
        this.drawDmg = false;
        this.isLightTransparent = true;
        this.presetId = presetId;
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/blueprint");
    }

    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "blueprint");
    }

    public GameTexture generateItemTexture() {
        return GameTexture.fromFile("items/blueprint");
    }

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

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().sprite(rotation % 4, 0, 32, this.texture.getHeight()).alpha(alpha).draw(drawX, drawY - this.texture.getHeight() + 32);
    }

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

    public java.util.List<ObjectHoverHitbox> getHoverHitboxes(Level level, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, tileX, tileY);
        list.add(new ObjectHoverHitbox(tileX, tileY, 0, -16, 32, 16));
        return list;
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServerLevel()) {
            OEInventoryContainer.openAndSendContainer(ContainerModRegistry.BLUEPRINT_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new BlueprintObjectEntity(level, presetId, x, y);
    }

    public static void registerBlueprints() {
        ObjectRegistry.registerObject("blueprinthouse1", new BlueprintObject(BlueprintPresetID.HOUSE_2), 5, true);
    }

    public static void registerBlueprintRecipes() {
        String[] list = new String[]{"blueprinthouse1"};

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

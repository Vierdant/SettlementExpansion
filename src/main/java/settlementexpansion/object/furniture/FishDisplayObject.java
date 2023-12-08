package settlementexpansion.object.furniture;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.DisplayStandObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
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
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import settlementexpansion.object.entity.FishDisplayObjectEntity;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class FishDisplayObject extends FurnitureObject {
    protected String textureName;
    public GameTexture texture;
    private final HashMap<String, GameTexture> displayableFish;
    protected int fishHeight;

    public FishDisplayObject(String textureName, ToolType toolType, Color mapColor, int fishHeight) {
        this.textureName = textureName;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.fishHeight = fishHeight;
        this.objectHealth = 50;
        this.drawDmg = false;
        this.isLightTransparent = true;
        this.furnitureType = "fishdisplay";
        this.displayableFish = new HashMap<>();
    }

    public FishDisplayObject(String textureName, int fishHeight) {
        // deadwood map color 153, 127, 98
        this(textureName, ToolType.ALL, new Color(153, 127, 98), fishHeight);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/" + this.textureName);
        for (String entry : new String[]{"furfish", "gobfish", "halffish", "icefish", "rockfish", "swampfish", "terrorfish"}) {
            this.displayableFish.put(entry, GameTexture.fromFile("objects/fishdisplay/" + entry));
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        int sprite = this.getSprite(level, tileX, tileY, rotation);

        if (sprite == 0) {
            drawY -= 16;
        } else if (sprite == 2) {
            drawY += 16;
        }

        ObjectEntity ent = level.entityManager.getObjectEntity(tileX, tileY);
        final DrawOptionsList fishOptions = new DrawOptionsList();
        if (ent != null && ent.implementsOEInventory()) {
            InventoryItem invFish = ((OEInventory)ent).getInventory().getItem(0);
            if (invFish != null && this.displayableFish.containsKey(invFish.item.getStringID())) {
                GameTexture fishTexture = this.displayableFish.get(invFish.item.getStringID());
                boolean active = this.isActive(level, tileX, tileY);
                if (sprite == 0) {
                    fishOptions.add(fishTexture.initDraw().sprite(active ? 3 : 0,0,32).light(light).pos(drawX - 32, drawY - 16 + this.fishHeight));
                    fishOptions.add(fishTexture.initDraw().sprite(active ? 4 : 1,0,32).light(light).pos(drawX, drawY - 16 + this.fishHeight));
                    fishOptions.add(fishTexture.initDraw().sprite(active ? 5 : 2,0,32).light(light).pos(drawX + 32, drawY - 16 + this.fishHeight));
                } else if (sprite == 1) {
                    fishOptions.add(fishTexture.initDraw().sprite(active ? 4 : 1,2,32).light(light).pos(drawX, drawY - 48 + this.fishHeight));
                    fishOptions.add(fishTexture.initDraw().sprite(active ? 4 : 1,3,32).light(light).pos(drawX, drawY - 16 + this.fishHeight));
                    fishOptions.add(fishTexture.initDraw().sprite(active ? 4 : 1,4,32).light(light).pos(drawX, drawY + 16 + this.fishHeight));
                } else if (sprite == 2) {
                    fishOptions.add(fishTexture.initDraw().sprite(active ? 3 : 0,1,32).light(light).pos(drawX - 32, drawY - 16 + this.fishHeight));
                    fishOptions.add(fishTexture.initDraw().sprite(active ? 4 : 1,1,32).light(light).pos(drawX, drawY - 16 + this.fishHeight));
                    fishOptions.add(fishTexture.initDraw().sprite(active ? 5 : 2,1,32).light(light).pos(drawX + 32, drawY - 16 + this.fishHeight));
                } else {
                    fishOptions.add(fishTexture.initDraw().sprite(active ? 3 : 0,2,32).light(light).pos(drawX, drawY - 48 + this.fishHeight));
                    fishOptions.add(fishTexture.initDraw().sprite(active ? 3 : 0,3,32).light(light).pos(drawX, drawY - 16 + this.fishHeight));
                    fishOptions.add(fishTexture.initDraw().sprite(active ? 3 : 0,4,32).light(light).pos(drawX, drawY + 16 + this.fishHeight));
                }
            }
        }

        final TextureDrawOptions options = this.texture.initDraw().sprite(0, sprite, 32).light(light).pos(drawX, drawY - 16);
        final byte sortY;
        if (sprite == 0) {
            sortY = 0;
        } else if (sprite == 2) {
            sortY = 32;
        } else {
            sortY = 16;
        }

        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return sortY;
            }

            public void draw(TickManager tickManager) {
                options.draw();
                fishOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int sprite = this.getSprite(level, tileX, tileY, rotation);
        if (sprite == 0) {
            drawY -= 16;
        } else if (sprite == 2) {
            drawY += 16;
        }

        this.texture.initDraw().sprite(0, sprite, 32).alpha(alpha).draw(drawX, drawY - 16);
    }

    private int getSprite(Level level, int tileX, int tileY, int rotation) {
        GameObject topObject = level.getObject(tileX, tileY - 1);
        GameObject rightObject = level.getObject(tileX + 1, tileY);
        GameObject botObject = level.getObject(tileX, tileY + 1);
        GameObject leftObject = level.getObject(tileX - 1, tileY);
        boolean attachTop = topObject.isWall && !topObject.isDoor;
        boolean attachRight = rightObject.isWall && !rightObject.isDoor;
        boolean attachBot = botObject.isWall && !botObject.isDoor;
        boolean attachLeft = leftObject.isWall && !leftObject.isDoor;
        if (attachTop) {
            if (rotation == 0 && attachBot) {
                return 2;
            } else if (rotation == 1 && attachRight) {
                return 1;
            } else {
                return rotation == 3 && attachLeft ? 3 : 0;
            }
        } else if (attachBot) {
            if (rotation == 1 && attachRight) {
                return 1;
            } else {
                return rotation == 3 && attachLeft ? 3 : 2;
            }
        } else if (attachRight) {
            return rotation == 3 && attachLeft ? 3 : 1;
        } else {
            return attachLeft ? 3 : 0;
        }
    }

    @Override
    public int getPlaceRotation(Level level, int levelX, int levelY, PlayerMob player, int playerDir) {
        if (playerDir == 1) {
            return 3;
        } else {
            return playerDir == 3 ? 1 : super.getPlaceRotation(level, levelX, levelY, player, playerDir);
        }
    }

    @Override
    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, tileX, tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        int sprite = this.getSprite(level, tileX, tileY, rotation);
        if (sprite == 0) {
            list.add(new ObjectHoverHitbox(tileX, tileY, 0, -32, 32, 32, 0));
        } else if (sprite == 1 || sprite == 3) {
            list.add(new ObjectHoverHitbox(tileX, tileY, 0, -16, 32, 16, 0));
        }

        return list;
    }

    @Override
    public String canPlace(Level level, int x, int y, int rotation) {
        if (level.getObjectID(x, y) != 0 && !level.getObject(x, y).isGrass) {
            return "occupied";
        } else {
            return !isValid(level, x, y) ? "nowall" : null;
        }
    }

    @Override
    public boolean isValid(Level level, int x, int y) {
        boolean hasWall = false;
        if (level.getObject(x - 1, y).isWall && !level.getObject(x - 1, y).isDoor) {
            hasWall = true;
        } else if (level.getObject(x + 1, y).isWall && !level.getObject(x + 1, y).isDoor) {
            hasWall = true;
        } else if (level.getObject(x, y - 1).isWall && !level.getObject(x, y - 1).isDoor) {
            hasWall = true;
        } else if (level.getObject(x, y + 1).isWall && !level.getObject(x, y + 1).isDoor) {
            hasWall = true;
        }

        return hasWall;
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
        if (level.isServerLevel()) {
            OEInventoryContainer.openAndSendContainer(ContainerRegistry.OE_INVENTORY_CONTAINER, player.getServerClient(), level, x, y);
        }

    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new FishDisplayObjectEntity(level, x, y);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "fishdisplaytip"));
        return tooltips;
    }

    public boolean isActive(Level level, int x, int y) {
        byte rotation = level.getObjectRotation(x, y);
        return this.getMultiTile(rotation).streamIDs(x, y)
                .anyMatch((c) -> level.wireManager.isWireActiveAny(c.tileX, c.tileY));
    }

    @Override
    public void onWireUpdate(Level level, int x, int y, int wireID, boolean active) {
        if (level.isClientLevel()) {
            playSwitchSound(x, y);
        }
    }

    public void playSwitchSound(int x, int y) {
        Screen.playSound(GameResources.tick, SoundEffect.effect((float)(x * 32 + 16), (float)(y * 32 + 16)).pitch(0.8F));
    }
}

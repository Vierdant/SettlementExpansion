package settlementexpansion.object;

import necesse.engine.Settings;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundManager;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.*;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WallSecureDoorObject extends SecureDoorObject {

    protected final WallObject wallObject;
    protected GameTexture texture;
    protected final String texturePath;

    public WallSecureDoorObject(String texturePath, WallObject wallObject, int counterID, boolean isOpen) {
        super(new Rectangle(), counterID, isOpen);
        this.texturePath = texturePath;
        this.wallObject = wallObject;
        this.toolTier = wallObject.toolTier;
        this.toolType = wallObject.toolType;
        this.setItemCategory("objects", "wallsanddoors");
        this.mapColor = wallObject.mapColor;
        this.objectHealth = wallObject.objectHealth * 2;
        this.isWall = true;
        this.replaceCategories.add("door");
        this.canReplaceCategories.add("door");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("fence");
        this.canReplaceCategories.add("fencegate");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/" + this.texturePath);
    }

    public boolean shouldMirror(Level level, int tileX, int tileY, int rotation) {
        if (rotation == 0) {
            return level.getObject(tileX + 1, tileY) instanceof WallDoorObject;
        } else if (rotation == 1) {
            return level.getObject(tileX, tileY - 1) instanceof WallDoorObject;
        } else if (rotation == 2) {
            return level.getObject(tileX - 1, tileY) instanceof WallDoorObject;
        } else {
            return rotation == 3 && level.getObject(tileX, tileY + 1) instanceof WallDoorObject;
        }
    }

    private boolean testSolid(Level level, int tileX, int tileY) {
        GameObject object = level.getObject(tileX, tileY);
        return object.isWall || object.isDoor || object.isFence || object.isRock || object.isSolid;
    }

    @Override
    public boolean isOpen(Level level, int tileX, int tileY, int rotation) {
        if (rotation != 0 && rotation != 2) {
            if (this.isSwitched && this.testSolid(level, tileX - 1, tileY) && this.testSolid(level, tileX + 1, tileY)) {
                return false;
            }

            return this.isSwitched || !this.testSolid(level, tileX, tileY - 1) || !this.testSolid(level, tileX, tileY + 1);
        } else {
            if (!this.isSwitched && this.testSolid(level, tileX - 1, tileY) && this.testSolid(level, tileX + 1, tileY)) {
                return false;
            }

            return !this.isSwitched || !this.testSolid(level, tileX, tileY - 1) || !this.testSolid(level, tileX, tileY + 1);
        }
    }

    private boolean isWall(Level level, int tileX, int tileY) {
        GameObject object = level.getObject(tileX, tileY);
        return object.isWall || object.isDoor || object.isFence || object.isRock;
    }

    @Override
    public ArrayList<ObjectPlaceOption> getPlaceOptions(Level level, int levelX, int levelY, PlayerMob playerMob, int playerDir, boolean offsetMultiTile) {
        Point offset = offsetMultiTile ? this.getPlaceOffset(playerDir) : null;
        int tileX = (levelX + (offset == null ? 0 : offset.x)) / 32;
        int tileY = (levelY + (offset == null ? 0 : offset.y)) / 32;

        boolean forcedHorizontal = this.isWall(level, levelX, levelY - 1) || this.isWall(level, levelX, levelY + 1);
        boolean forcedVertical = !forcedHorizontal && (this.isWall(level, levelX - 1, levelY) || this.isWall(level, levelX + 1, levelY));
        int value = playerDir;
        if (forcedHorizontal) {
            if (playerDir == 0) {
                value = 1;
            }

            if (playerDir == 2) {
                value = 3;
            }
        } else if (forcedVertical) {
            if (playerDir == 1) {
                value = 0;
            }

            if (playerDir == 3) {
                value = 2;
            }
        }

        return new ArrayList<>(Collections.singleton(new ObjectPlaceOption(tileX, tileY, this, value, false)));
    }

    @Override
    public void addDrawables(java.util.List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameLight light = level.getLightLevelWall(tileX, tileY);
        int rotation = level.getObjectRotation(tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        float alpha = 1.0F;
        GameWindow window = WindowManager.getWindow();
        if (perspective != null && !Settings.hideUI) {
            Rectangle alphaRec = new Rectangle(tileX * 32 - 16, tileY * 32 - 48, 64, 80);
            if (rotation == 0) {
                alphaRec.height -= 26;
            } else if (rotation == 2) {
                alphaRec.y += 28;
                alphaRec.height -= 28;
            }

            if (perspective.getCollision().intersects(alphaRec)) {
                alpha = 0.5F;
            } else if (alphaRec.contains(camera.getX() + window.mousePos().sceneX, camera.getY() + window.mousePos().sceneY)) {
                alpha = 0.5F;
            }
        }

        boolean shouldMirror = this.shouldMirror(level, tileX, tileY, rotation);
        final byte sortY;
        if (rotation == 0) {
            options.add(this.texture.initDraw().sprite(0, 0, 32, 64).mirror(shouldMirror, false).alpha(alpha).light(light).pos(drawX, drawY - 32));
            options.add(this.texture.initDraw().sprite(0, 0, 32, 64).alpha(alpha).light(light).pos(drawX, drawY - 32));
            sortY = 4;
        } else if (rotation == 1) {
            options.add(this.texture.initDraw().sprite(2, 1, 32, 64).alpha(alpha).light(light).pos(drawX, drawY - 32));
            options.add(this.texture.initDraw().sprite(2, 1, 32, 64).alpha(alpha).light(light).pos(drawX, drawY - 32));
            sortY = 20;
        } else if (rotation == 2) {
            options.add(this.texture.initDraw().sprite(0, 1, 32, 64).mirror(shouldMirror, false).alpha(alpha).light(light).pos(drawX, drawY - 32));
            options.add(this.texture.initDraw().sprite(0, 1, 32, 64).alpha(alpha).light(light).pos(drawX, drawY - 32));
            sortY = 28;
        } else {
            options.add(this.texture.initDraw().sprite(2, 0, 32, 64).alpha(alpha).light(light).pos(drawX, drawY - 32));
            options.add(this.texture.initDraw().sprite(2, 0, 32, 64).alpha(alpha).light(light).pos(drawX, drawY - 32));
            sortY = 20;
        }

        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return sortY;
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
        boolean shouldMirror = this.shouldMirror(level, tileX, tileY, rotation);
        if (rotation == 0) {
            this.texture.initDraw().sprite(0, 0, 32, 64).mirror(shouldMirror, false).alpha(alpha).draw(drawX, drawY - 32);
            this.texture.initDraw().sprite(0, 0, 32, 64).alpha(alpha).draw(drawX, drawY - 32);
        } else if (rotation == 1) {
            this.texture.initDraw().sprite(2, 1, 32, 64).alpha(alpha).draw(drawX, drawY - 32);
            this.texture.initDraw().sprite(2, 1, 32, 64).alpha(alpha).draw(drawX, drawY - 32);
        } else if (rotation == 2) {
            this.texture.initDraw().sprite(0, 1, 32, 64).mirror(shouldMirror, false).alpha(alpha).draw(drawX, drawY - 32);
            this.texture.initDraw().sprite(0, 1, 32, 64).alpha(alpha).draw(drawX, drawY - 32);
        } else {
            this.texture.initDraw().sprite(2, 0, 32, 64).alpha(alpha).draw(drawX, drawY - 32);
            this.texture.initDraw().sprite(2, 0, 32, 64).alpha(alpha).draw(drawX, drawY - 32);
        }

    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32, y * 32, 32, 12);
        } else if (rotation == 1) {
            return new Rectangle(x * 32 + 28, y * 32, 4, 32);
        } else {
            return rotation == 2 ? new Rectangle(x * 32, y * 32 + 28, 32, 4) : new Rectangle(x * 32, y * 32, 4, 32);
        }
    }

    @Override
    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int layerID, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, layerID, tileX, tileY);
        int rotation = level.getObjectRotation(tileX, tileY);
        if (rotation == 0) {
            list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 0, -16, 32, 16));
        } else if (rotation == 1) {
            list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 0, -24, 32, 24));
        } else {
            if (rotation == 2) {
                return list;
            }

            list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 0, -24, 32, 24));
        }

        return list;
    }

    @Override
    public boolean shouldSnapSmartMining(Level level, int x, int y) {
        return true;
    }

    @Override
    public boolean isSolid(Level level, int x, int y) {
        return !this.isOpen(level, x, y, level.getObjectRotation(x, y));
    }

    @Override
    public int getLightLevelMod(Level level, int x, int y) {
        return this.isSolid(level, x, y) && !this.isLightTransparent(level, x, y) && !this.isOpen(level, x, y, level.getObjectRotation(x, y)) ? 40 : 10;
    }

    @Override
    public boolean stopsTerrainSplatting() {
        return true;
    }

    @Override
    public void playSwitchSound(Level level, int x, int y) {
        if (level.isClient()) {
            SoundManager.playSound(this.isSwitched ? GameResources.doorclose : GameResources.dooropen, SoundEffect.effect((float)(x * 32 + 16), (float)(y * 32 + 16)));
        }
    }

    public static void registerSecureDoorPair(String stringIDPrefix, WallObject wallObject, float brokerValue, boolean itemObtainable) {
        WallSecureDoorObject closed = new WallSecureDoorObject(stringIDPrefix, wallObject, 0, false);
        int closedDoor = ObjectRegistry.registerObject(stringIDPrefix, closed, brokerValue, itemObtainable);
        closed.counterID = ObjectRegistry.registerObject(stringIDPrefix + "open", new WallSecureDoorOpenObject(stringIDPrefix, wallObject, closedDoor), 0.0F, false);
    }

    private static class WallSecureDoorOpenObject extends WallSecureDoorObject {
        private WallSecureDoorOpenObject(String texturePath, WallObject wallObject, int counterID) {
            super(texturePath, wallObject, counterID, true);
        }

        @Override
        public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
            return ObjectRegistry.getObject(this.counterID).getLootTable(level, layerID, tileX, tileY);
        }

        public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            GameLight light = level.getLightLevelWall(tileX, tileY);
            int rotation = level.getObjectRotation(tileX, tileY);
            final DrawOptionsList options = new DrawOptionsList();
            float alpha = 1.0F;
            GameWindow window = WindowManager.getWindow();
            if (perspective != null) {
                Rectangle alphaRec = new Rectangle(tileX * 32 - 16, tileY * 32 - 48, 64, 80);
                if (perspective.getCollision().intersects(alphaRec)) {
                    alpha = 0.5F;
                } else if (alphaRec.contains(camera.getX() + window.mousePos().sceneX, camera.getY() + window.mousePos().sceneY)) {
                    alpha = 0.5F;
                }
            }

            boolean shouldMirror = this.shouldMirror(level, tileX, tileY, rotation);
            final byte sortY;
            if (rotation == 0) {
                options.add(this.texture.initDraw().sprite(1, 1, 32, 64).mirror(shouldMirror, false).alpha(alpha).light(light).pos(drawX, drawY - 32));
                options.add(this.texture.initDraw().sprite(1, 1, 32, 64).mirror(shouldMirror, false).alpha(alpha).light(light).pos(drawX, drawY - 32));
                sortY = 20;
            } else if (rotation == 1) {
                if (shouldMirror) {
                    if (level.getObject(tileX, tileY + 1).isWall && !level.getObject(tileX, tileY + 1).isDoor) {
                        drawY += 8;
                    }

                    drawY += 26;
                }

                options.add(this.texture.initDraw().sprite(3, 1, 32, 64).alpha(alpha).light(light).pos(drawX, drawY - 32));
                options.add(this.texture.initDraw().sprite(3, 1, 32, 64).alpha(alpha).light(light).pos(drawX, drawY - 32));
                if (shouldMirror) {
                    sortY = 28;
                } else {
                    sortY = 4;
                }
            } else if (rotation == 2) {
                options.add(this.texture.initDraw().sprite(1, 0, 32, 64).mirror(shouldMirror, false).alpha(alpha).light(light).pos(drawX, drawY - 32));
                options.add(this.texture.initDraw().sprite(1, 0, 32, 64).mirror(shouldMirror, false).alpha(alpha).light(light).pos(drawX, drawY - 32));
                sortY = 20;
            } else {
                if (level.getObject(tileX, tileY + 1).isWall && !level.getObject(tileX, tileY + 1).isDoor) {
                    drawY += 8;
                }

                if (shouldMirror) {
                    drawY -= 26;
                }

                options.add(this.texture.initDraw().sprite(3, 0, 32, 64).alpha(alpha).light(light).pos(drawX, drawY - 32));
                options.add(this.texture.initDraw().sprite(3, 0, 32, 64).alpha(alpha).light(light).pos(drawX, drawY - 32));
                if (shouldMirror) {
                    sortY = 4;
                } else {
                    sortY = 28;
                }
            }

            list.add(new LevelSortedDrawable(this, tileX, tileY) {
                public int getSortY() {
                    return sortY;
                }

                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
        }

        public Rectangle getCollision(Level level, int x, int y, int rotation) {
            boolean shouldMirror = this.shouldMirror(level, x, y, rotation);
            if (rotation == 0) {
                return shouldMirror ? new Rectangle(x * 32, y * 32, 4, 32) : new Rectangle(x * 32 + 28, y * 32, 4, 32);
            } else if (rotation == 1) {
                return shouldMirror ? new Rectangle(x * 32, y * 32 + 28, 32, 4) : new Rectangle(x * 32, y * 32, 32, 4);
            } else if (rotation == 2) {
                return shouldMirror ? new Rectangle(x * 32 + 28, y * 32, 4, 32) : new Rectangle(x * 32, y * 32, 4, 32);
            } else {
                return shouldMirror ? new Rectangle(x * 32, y * 32, 32, 4) : new Rectangle(x * 32, y * 32 + 28, 32, 4);
            }
        }

        @Override
        public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int layerID, int tileX, int tileY) {
            List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, layerID, tileX, tileY);
            int rotation = level.getObjectRotation(tileX, tileY);
            if (rotation == 2) {
                list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 0, -16, 32, 16));
            }

            return list;
        }
    }
}

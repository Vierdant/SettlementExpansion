package settlementexpansion.object;

import necesse.engine.network.server.ServerClient;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class ReinforcedOuterWallObject extends GameObject {

    private final String texturePath;
    private GameTexture texture;

    public ReinforcedOuterWallObject(String texturePath, int health, ToolType tooltype, Color color) {
        super(new Rectangle(32, 32));
        this.isLightTransparent = true;
        this.objectHealth = health;
        this.toolType = tooltype;
        this.mapColor = color;
        this.texturePath = texturePath;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/" + texturePath);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        LevelObject[] adj = level.getAdjacentLevelObjects(tileX, tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        int y = getConnectionSprite(rotation, adj);
        final DrawOptionsList options = new DrawOptionsList();
        options.add(this.texture.initDraw().sprite(rotation % 4, y, 32).light(light).pos(drawX, drawY - 32));
        options.add(this.texture.initDraw().sprite(rotation % 4, y + 1, 32).light(light).pos(drawX, drawY));
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
        LevelObject[] adj = level.getAdjacentLevelObjects(tileX, tileY);
        int y = getConnectionSprite(rotation, adj);
        this.texture.initDraw().sprite(rotation % 4, y, 32).alpha(alpha).draw(drawX, drawY - 32);
        this.texture.initDraw().sprite(rotation % 4, y + 1, 32).alpha(alpha).draw(drawX, drawY);
    }

    private int getConnectionSprite(int rotation, LevelObject[] adj) {
        int y = 0;
        if (rotation == 0 || rotation == 2) {
            if (isSame(rotation, adj[3]) || isSame(rotation, adj[4])) {
                if (isSame(rotation, adj[3]) && isSame(rotation, adj[4])) {
                    y = 2;
                } else {
                    y = isSame(rotation, adj[3]) ? 4 : 6;
                }
            }
        } else {
            if (isSame(rotation, adj[1]) || isSame(rotation, adj[6])) {
                if (isSame(rotation, adj[1]) && isSame(rotation, adj[6])) {
                    y = 2;
                } else {
                    y = isSame(rotation, adj[1]) ? 4 : 6;
                }
            }
        }
        return y;
    }

    private boolean isSame(int rotation, LevelObject compareTo) {
        return rotation == compareTo.rotation && this.getStringID().equals(compareTo.object.getStringID());
    }

    private boolean isNotSameId(LevelObject compareTo){
        return !this.getStringID().equals(compareTo.object.getStringID());
    }

    private boolean canApplyDamage(int rotation, Point origin, ServerClient client) {
        Point pos = client.playerMob.getTilePoint();
        boolean atPoint = origin.y == pos.y && origin.x == pos.x;
        if (rotation == 0 && ((origin.y - 1 == pos.y && origin.x == pos.x) || atPoint)) return true;
        if (rotation == 1 && ((origin.y == pos.y && origin.x + 1 == pos.x) || atPoint)) return true;
        if (rotation == 2 && ((origin.y + 1 == pos.y && origin.x == pos.x) || atPoint)) return true;
        return rotation == 3 && ((origin.y == pos.y && origin.x - 1 == pos.x) || atPoint);
    }

    @Override
    protected Rectangle getCollision(Level level, int x, int y, int rotation) {
        Rectangle out = super.getCollision(level, x, y, rotation);
        LevelObject[] adj = level.getAdjacentLevelObjects(x, y);
        if (rotation == 0) {
            if (isNotSameId(adj[0]) && isNotSameId(adj[2])) {
                out.height -= 16;
                out.y += 16;
            }
        } else if (rotation == 2) {
            if (isNotSameId(adj[5]) && isNotSameId(adj[7])) {
                out.height -= 16;
            }
        } else if (rotation == 1) {
            if (isNotSameId(adj[2]) && isNotSameId(adj[6])) {
                out.width -= 16;
            }
        } else {
            if (isNotSameId(adj[0]) && isNotSameId(adj[5])) {
                out.width -= 16;
                out.x += 16;
            }
        }
        return out;
    }

    @Override
    public boolean onDamaged(Level level, int layerID, int x, int y, int damage, Attacker attacker, ServerClient client, boolean showEffect, int mouseX, int mouseY) {
        if (client != null) {
            int rotation = level.getLevelObject(x, y).rotation;
            boolean canDamage = canApplyDamage(rotation, new Point(x, y), client);
            if (!canDamage) {
                level.entityManager.doObjectDamageOverride(layerID, x, y, damage / 4);
                return false;
            }
        }
        return true;
    }
}

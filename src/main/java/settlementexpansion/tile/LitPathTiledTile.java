package settlementexpansion.tile;

import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.PathTiledTile;
import necesse.level.maps.Level;

import java.awt.*;

public class LitPathTiledTile extends PathTiledTile {

    private final GameTile parentTile;

    public LitPathTiledTile(String textureName, Color mapColor, int lightLevel, float lightHue, float lightSat, GameTile parent) {
        super(textureName, mapColor);
        this.lightLevel = lightLevel;
        this.lightHue = lightHue;
        this.lightSat = lightSat;
        this.roomProperties.add("lights");
        this.parentTile = parent;
    }

    public LitPathTiledTile(String textureName, Color mapColor, int lightLevel, float lightHue, float lightSat) {
        this(textureName, mapColor, lightLevel, lightHue, lightSat, null);
    }

    @Override
    protected boolean isMergeTile(Level level, int tileX, int tileY) {
        if (super.isMergeTile(level, tileX, tileY)) {
            return true;
        } else if (this.parentTile != null) {
            return level.getTileID(tileX, tileY) == this.parentTile.getID();
        } else {
            GameObject object = level.getObject(tileX, tileY);
            return object.isWall && object.isDoor;
        }
    }

}

package settlementexpansion.tile;

import necesse.level.gameTile.PathTiledTile;

import java.awt.*;

public class LitPathTiledTile extends PathTiledTile {

    public LitPathTiledTile(String textureName, Color mapColor, int lightLevel, float lightHue, float lightSat) {
        super(textureName, mapColor);
        this.lightLevel = lightLevel;
        this.lightHue = lightHue;
        this.lightSat = lightSat;
        this.roomProperties.add("lights");
    }

}

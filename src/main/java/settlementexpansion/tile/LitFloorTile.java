package settlementexpansion.tile;

import necesse.level.gameTile.SimpleFloorTile;

import java.awt.*;

public class LitFloorTile extends SimpleFloorTile {

    public LitFloorTile(String textureName, Color mapColor, int lightLevel, float lightHue, float lightSat) {
        super(textureName, mapColor);
        this.lightLevel = lightLevel;
        this.lightHue = lightHue;
        this.lightSat = lightSat;
        this.roomProperties.add("lights");
    }




}

package settlementexpansion.tile;

import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;

import java.awt.*;

public class GrassHoleTile extends TerrainSplatterTile {
    private final GameRandom drawRandom;

    public GrassHoleTile() {
        super(false, "grasshole");
        this.mapColor = new Color(66, 152, 66);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
    }

    @Override
    public GameTextureSection getTerrainTexture(Level level, int tileX, int tileY) {
        return this.terrainTexture;
    }

    @Override
    public Point getTerrainSprite(GameTextureSection gameTextureSection, Level level, int tileX, int tileY) {
        int tile;
        synchronized(this.drawRandom) {
            tile = this.drawRandom.seeded(this.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }

        return new Point(0, tile);
    }

    @Override
    public int getTerrainPriority() {
        return 200;
    }
}

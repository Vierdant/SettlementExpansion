package settlementexpansion.listener;

import necesse.engine.GameEventListener;
import necesse.engine.events.worldGeneration.GeneratedIslandFloraEvent;
import necesse.engine.registries.TileRegistry;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.generationModules.GenerationTools;
import settlementexpansion.registry.TileModRegistry;

public class GeneratedIslandFloraListener extends GameEventListener<GeneratedIslandFloraEvent> {

    @Override
    public void onEvent(GeneratedIslandFloraEvent event) {
        /*GenerationTools.generateRandomSmoothVeins(event.level, event.islandGeneration.random,
                0.5F, 4, 4, 4, 2, 2, (level, tileX, tileY) -> {

            int tile = level.getTileID(tileX, tileY);

            GameTile mudFossil = TileRegistry.getTile(TileModRegistry.mudFossilTile);
            GameTile grassHole = TileRegistry.getTile(TileModRegistry.grassHoleTile);

            if (tile == TileRegistry.mudID) {
                if (event.islandGeneration.random.getChance(0.1F)) {
                    mudFossil.placeTile(level, tileX, tileY);
                }
            }

            if (tile == TileRegistry.grassID) {
                if (event.islandGeneration.random.getChance(0.1F)) {
                    grassHole.placeTile(level, tileX, tileY);
                }
            }
        });*/
    }
}

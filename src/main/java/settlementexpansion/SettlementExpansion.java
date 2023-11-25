package settlementexpansion;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.ProjectileRegistry;
import settlementexpansion.registry.*;

@ModEntry
public class SettlementExpansion {

    public void init() {
        System.out.println("Settlement Expansion was enabled!");

        RecipeTechModRegistry.registerModdedTech();

        ObjectModRegistry.registerObjects();

        BuffModRegistry.registerBuffs();

        ItemModRegistry.registerCategories();
        ItemModRegistry.registerItems();

        TileModRegistry.registerTiles();

        MobModRegistry.registerMobs();

        SettlerModRegistry.registerSettlers();

        ProjectileModRegistry.registerProjectiles();

        ContainerModRegistry.registerContainers();

        PacketModRegistry.registerPackets();
    }

    public void initResources() {
        ModResources.loadTextures();
        ModResources.loadMobTextures();
        ModResources.loadSounds();
    }

    public void postInit() {
        ObjectModRegistry.registerRecipes();
        TileModRegistry.registerRecipes();
        ItemModRegistry.registerRecipes();
    }

}
package settlementexpansion;

import necesse.engine.GameEvents;
import necesse.engine.events.ServerClientConnectedEvent;
import necesse.engine.modLoader.annotations.ModEntry;
import settlementexpansion.registry.*;
import settlementexpansion.updater.UpdaterControlListener;

@ModEntry
public class SettlementExpansion {

    private static Settings settings;

    public void init() {
        System.out.println("Settlement Expansion was enabled!");

        addListeners();

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

    public Settings initSettings() {
        settings = new Settings();
        return settings;
    }

    public void postInit() {
        ObjectModRegistry.registerRecipes();
        TileModRegistry.registerRecipes();
        ItemModRegistry.registerRecipes();
    }

    public static Settings getSettings() {
        settings = new Settings();
        return settings;
    }

    private void addListeners() {
        GameEvents.addListener(ServerClientConnectedEvent.class, new UpdaterControlListener());
    }
}
package settlementexpansion;

import necesse.engine.GameEvents;
import necesse.engine.events.ServerClientConnectedEvent;
import necesse.engine.events.players.DamageTileEvent;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.events.players.MobInteractEvent;
import necesse.engine.events.players.ObjectInteractEvent;
import necesse.engine.events.worldGeneration.GeneratedIslandFloraEvent;
import necesse.engine.modLoader.annotations.ModEntry;
import settlementexpansion.bridge.Bridge;
import settlementexpansion.listener.*;
import settlementexpansion.registry.*;
import settlementexpansion.updater.UpdaterControlListener;

import java.util.HashMap;
import java.util.function.Supplier;

@ModEntry
public class SettlementExpansion {

    private static Settings settings;

    public void init() {
        System.out.println("Settlement Expansion was enabled!");

        registerCustomRegistries();
        ModBridgeRegistry.instance.loadBridges();

        addListeners();

        LevelDataModRegistry.registerLevelData();
        ContainerEventModRegistry.registerContainerEvents();

        RecipeTechModRegistry.registerModdedTech();

        ObjectModRegistry.replaceObjects();
        ObjectModRegistry.registerObjects();

        BuffModRegistry.registerBuffs();

        ItemModRegistry.registerCategories();
        ItemModRegistry.registerItems();

        TileModRegistry.registerTiles();

        MobModRegistry.registerMobs();

        JobModRegistry.registerJobTypes();
        JobModRegistry.registerLevelJobs();

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
        return settings;
    }

    private void registerCustomRegistries() {
        ModBridgeRegistry.instance.registerCore();
    }

    private void addListeners() {
        GameEvents.addListener(ServerClientConnectedEvent.class, new UpdaterControlListener());
        GameEvents.addListener(DamageTileEvent.class, new DamageTileSettlementListener());
        GameEvents.addListener(ItemPlaceEvent.class, new ItemPlaceSettlementListener());
        GameEvents.addListener(ObjectInteractEvent.class, new ObjectInteractSettlementListener());
        GameEvents.addListener(MobInteractEvent.class, new MobInteractSettlementListener());
        GameEvents.addListener(GeneratedIslandFloraEvent.class, new GeneratedIslandFloraListener());
    }
}
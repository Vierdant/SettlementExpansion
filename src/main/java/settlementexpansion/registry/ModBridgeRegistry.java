package settlementexpansion.registry;

import necesse.engine.GameLog;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import settlementexpansion.bridge.Bridge;
import settlementexpansion.bridge.HardenedSettlementsBridge;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class ModBridgeRegistry {

    public static final ModBridgeRegistry instance = new ModBridgeRegistry();
    public HashMap<String, Supplier<Bridge>> allBridges = new HashMap<>();
    public static HashMap<String, Bridge> loadedBridges = new HashMap<>();

    public ModBridgeRegistry() {}

    public void registerCore() {
        registerBridge("snoobinoob.hardenedsettlements", HardenedSettlementsBridge::new);
    }

    public void loadBridges() {
        for (Map.Entry<String, Supplier<Bridge>> bridge : allBridges.entrySet()) {
            loadBridge(bridge.getKey(), bridge.getValue());
        }
    }

    public void loadBridge(String id, Supplier<Bridge> bridgeSupplier) {
        LoadedMod mod = ModLoader.getEnabledMods().stream().filter((m) -> Objects.equals(m.id, id)).findFirst().orElse(null);
        if (mod == null) {
            return;
        }

        Bridge bridge;

        try {
            bridge = bridgeSupplier.get();
        } catch (Throwable ex) {
            GameLog.err.println("Cannot load bridge with id " + id + "...");
            GameLog.err.print(ex);
            return;
        }

        bridge.name = id;
        bridge.mod = mod;

        try {
            bridge.init();
        } catch (Throwable ex) {
            GameLog.err.println("Cannot load bridge with id " + id + "... Internal error was thrown!");
            GameLog.err.print(ex);
            return;
        }

        loadedBridges.put(id, bridge);
        GameLog.out.println("Settlement Expansion loaded a bridge for " + id + "!");
    }

    public void registerBridge(String id, Supplier<Bridge> bridgeSupplier) {
        allBridges.put(id, bridgeSupplier);
    }

    public static boolean hasBridge(String id) {
        return loadedBridges.containsKey(id);
    }

    public static Bridge getBridge(String id) {
        return loadedBridges.getOrDefault(id, null);
    }
}

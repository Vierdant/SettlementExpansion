package settlementexpansion.registry;

import necesse.engine.registries.SettlerRegistry;
import settlementexpansion.map.settlement.settler.ArchitectSettler;

public class SettlerModRegistry {

    public static void registerSettlers() {
        SettlerRegistry.registerSettler("architect", new ArchitectSettler());
    }
}

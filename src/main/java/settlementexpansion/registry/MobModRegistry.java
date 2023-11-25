package settlementexpansion.registry;

import necesse.engine.registries.MobRegistry;
import settlementexpansion.entity.mob.friendly.ArchitectHumanMob;

public class MobModRegistry {

    public static void registerMobs() {
        MobRegistry.registerMob("architecthuman", ArchitectHumanMob.class, true);
    }
}

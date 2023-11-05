package settlementexpansion.registry;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.SimpleTrinketBuff;

public class BuffModRegistry {

    public static void registerBuffs() {
        BuffRegistry.registerBuff("earthringtrinket", new SimpleTrinketBuff(new ModifierValue<>(BuffModifiers.MINING_SPEED, 0.3F)));
    }
}

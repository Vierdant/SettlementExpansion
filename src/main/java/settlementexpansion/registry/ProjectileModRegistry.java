package settlementexpansion.registry;

import necesse.engine.registries.ProjectileRegistry;
import settlementexpansion.entity.projectile.TrapCannonBallProjectile;

public class ProjectileModRegistry {


    public static void registerProjectiles() {

        ProjectileRegistry.registerProjectile("trapcannonball", TrapCannonBallProjectile.class, "cannonball", "cannonball_shadow");
    }
}

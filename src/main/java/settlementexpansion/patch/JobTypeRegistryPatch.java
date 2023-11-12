package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.registries.JobTypeRegistry;
import net.bytebuddy.asm.Advice;
import settlementexpansion.registry.JobModRegistry;

@ModMethodPatch(target = JobTypeRegistry.class, name = "registerCore", arguments = {})
public class JobTypeRegistryPatch {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean onEnter() { return true; }

    @Advice.OnMethodExit
    static void onExit() {
        JobModRegistry.registerJobTypes();
    }

}

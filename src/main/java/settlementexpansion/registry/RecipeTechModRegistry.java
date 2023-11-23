package settlementexpansion.registry;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Tech;

public class RecipeTechModRegistry {

    public static Tech DRYINGRACK;
    public static Tech SEEDLINGTABLE;
    public static Tech BLUEPRINTTABLE;

    public static void registerModdedTech() {
        DRYINGRACK = RecipeTechRegistry.registerTech("dryingrack", new LocalMessage("object", "dryingrack"));
        SEEDLINGTABLE = RecipeTechRegistry.registerTech("seedlingtable", new LocalMessage("object", "seedlingtable"));
        BLUEPRINTTABLE = RecipeTechRegistry.registerTech("blueprinttable", new LocalMessage("object", "blueprinttable"));

    }
}

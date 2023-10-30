package settlementexpansion.util;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Tech;

public class RecipeModTechRegistry {

    public static Tech DRYINGRACK;

    public static void registerModdedTech() {
        DRYINGRACK = RecipeTechRegistry.registerTech("dryingrack", new LocalMessage("object", "dryingrack"));
    }
}

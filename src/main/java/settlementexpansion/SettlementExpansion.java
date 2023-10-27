package settlementexpansion;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.level.gameObject.furniture.ChairObject;
import settlementexpansion.object.DryingRackObject;

@ModEntry
public class SettlementExpansion {

    public void init() {
        System.out.println("Settlement Expansion was enabled!");

        ObjectRegistry.registerObject("dryingrack", new DryingRackObject(), 50.0F, true);

    }

    public void initResources() {}

    public void postInit() {
        Recipes.registerModRecipe(new Recipe(
                "dryingrack",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anylog", 20)
                },
                false
        ));
    }

}
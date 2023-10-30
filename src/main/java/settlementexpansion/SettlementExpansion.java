package settlementexpansion;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import settlementexpansion.item.material.TannedLeatherMaterial;
import settlementexpansion.object.DryingRackObject;
import settlementexpansion.util.RecipeModTechRegistry;

@ModEntry
public class SettlementExpansion {

    public void init() {
        System.out.println("Settlement Expansion was enabled!");

        // Register essentials
        RecipeModTechRegistry.registerModdedTech();

        // Register objects
        ObjectRegistry.registerObject("dryingrack", new DryingRackObject(), 50.0F, true);

        // Register items
        ItemRegistry.registerItem("tannedleather", new TannedLeatherMaterial(), 10, true);
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

        Recipes.registerModRecipe(new Recipe(
                "tannedleather",
                1,
                RecipeModTechRegistry.DRYINGRACK,
                new Ingredient[]{
                        new Ingredient("leather", 1)
                }
        ));
    }

}
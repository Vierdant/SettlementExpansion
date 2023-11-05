package settlementexpansion.registry;

import necesse.engine.registries.ItemRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.matItem.MatItem;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import settlementexpansion.item.misc.GeodeItem;

public class ItemModRegistry {

    public static void registerCategories() {
        ItemCategory.createCategory("A-F-A", "misc", "geodes");
        ItemCategory.createCategory("C-H-B", "materials", "mobdrops", "processed");
    }

    public static void registerItems() {
        ItemRegistry.registerItem("tannedleather", (new MatItem(250, Item.Rarity.NORMAL, "craftingmat")).setItemCategory("materials", "mobdrops", "processed"), 10.0F, true);
        ItemRegistry.registerItem("examplegeode", new GeodeItem("examplegeode", 100), 10, true);
    }

    public static void registerRecipes() {
        Recipes.registerModRecipe(new Recipe(
                "tannedleather",
                1,
                RecipeTechModRegistry.DRYINGRACK,
                new Ingredient[]{
                        new Ingredient("leather", 1)
                }
        ));
    }

}

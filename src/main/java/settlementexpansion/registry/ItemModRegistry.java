package settlementexpansion.registry;

import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.matItem.MatItem;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import settlementexpansion.item.misc.GeodeItem;
import settlementexpansion.item.trinket.SimpleTippedTrinketItem;

public class ItemModRegistry {

    public static void registerCategories() {
        ItemCategory.createCategory("A-F-A", "misc", "geodes");
        ItemCategory.createCategory("C-H-B", "materials", "mobdrops", "processed");
        ItemCategory.createCategory("C-B-B", "materials", "minerals", "gems");
    }

    public static void registerItems() {
        ItemRegistry.registerItem("tannedleather", (new MatItem(250)).setItemCategory("materials", "mobdrops", "processed"), 10.0F, true);
        ItemRegistry.registerItem("geode", new GeodeItem("geode", 35, 1.5F), 25, true);

        ItemRegistry.registerItem("glass", (new MatItem(100).setItemCategory("materials")), 5, true);

        ItemRegistry.registerItem("alamite", (new MatItem(100, Item.Rarity.UNCOMMON, "alamite").setItemCategory("materials", "minerals", "gems")), 150, true);
        ItemRegistry.registerItem("amethyst", (new MatItem(100, Item.Rarity.UNCOMMON, "amethyst").setItemCategory("materials", "minerals", "gems")), 100, true);
        ItemRegistry.registerItem("calcite", (new MatItem(100, Item.Rarity.UNCOMMON, "calcite").setItemCategory("materials", "minerals", "gems")), 75, true);
        ItemRegistry.registerItem("celestine", (new MatItem(100, Item.Rarity.UNCOMMON, "celestine").setItemCategory("materials", "minerals", "gems")), 125, true);
        ItemRegistry.registerItem("jagoite", (new MatItem(100, Item.Rarity.UNCOMMON, "jagoite").setItemCategory("materials", "minerals", "gems")), 115, true);
        ItemRegistry.registerItem("malachite", (new MatItem(100, Item.Rarity.UNCOMMON, "malachite").setItemCategory("materials", "minerals", "gems")), 100, true);
        ItemRegistry.registerItem("orpiment", (new MatItem(100, Item.Rarity.UNCOMMON, "orpiment").setItemCategory("materials", "minerals", "gems")), 80, true);
        ItemRegistry.registerItem("earthcrystal", (new MatItem(100, Item.Rarity.UNCOMMON, "earthcrystal").setItemCategory("materials", "minerals", "gems")), 350, true);

        ItemRegistry.registerItem("earthring", (new SimpleTippedTrinketItem(Item.Rarity.UNCOMMON, "earthringtrinket", "earthring", 200)), 300, true);
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

        Recipes.registerModRecipe(new Recipe(
                "glass",
                2,
                RecipeTechRegistry.FORGE,
                new Ingredient[]{
                        new Ingredient("sandtile", 1)
                }
        ));

        Recipes.registerModRecipe(new Recipe(
                "earthring",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                        new Ingredient("earthcrystal", 1)
                }
        ).showAfter("explorersatchel"));
    }

}

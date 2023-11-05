package settlementexpansion.registry;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import settlementexpansion.tile.LitFloorTile;

import java.awt.*;

public class TileModRegistry {

    public static void registerTiles() {
        TileRegistry.registerTile("litwoodfloor", new LitFloorTile("litwoodfloor", new Color(153, 127, 98), 150, 50, 0.2f), 1, false);
    }

    public static void registerRecipes() {
        Recipes.registerModRecipe(new Recipe(
                "litwoodfloor",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anylog", 1),
                        new Ingredient("torch", 1)
                }
        ));
    }
}

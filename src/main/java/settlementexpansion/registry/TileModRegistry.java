package settlementexpansion.registry;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import settlementexpansion.tile.GrassHoleTile;
import settlementexpansion.tile.LitFloorTile;
import settlementexpansion.tile.LitPathTiledTile;
import settlementexpansion.tile.MudFossilTile;

import java.awt.*;

public class TileModRegistry {

    public static int mudFossilTile;
    public static int grassHoleTile;

    public static void registerTiles() {
        TileRegistry.registerTile("litwoodfloor", new LitFloorTile("litwoodfloor", new Color(232, 215, 149), 150, 50, 0.2f), 5, true);
        TileRegistry.registerTile("litwoodpathtile", new LitPathTiledTile("litwoodpath", new Color(232, 215, 149), 150, 50, 0.2f, TileRegistry.getTile("woodpathtile")), 5, true);
        TileRegistry.registerTile("litstonefloor", new LitFloorTile("litstonefloor", new Color(232, 215, 149), 150, 50, 0.2f), 5, true);
        TileRegistry.registerTile("litstonepathtile", new LitPathTiledTile("litstonepath", new Color(232, 215, 149), 150, 50, 0.2f, TileRegistry.getTile("stonepathtile")), 5, true);
        TileRegistry.registerTile("litswampstonefloor", new LitFloorTile("litswampstonefloor", new Color(232, 215, 149), 150, 50, 0.2f), 5, true);
        TileRegistry.registerTile("litswampstonepathtile", new LitPathTiledTile("litswampstonepath", new Color(232, 215, 149), 150, 50, 0.2f, TileRegistry.getTile("swampstonepathtile")), 5, true);

        grassHoleTile = TileRegistry.registerTile("grassholetile", new GrassHoleTile(), 0, false);
        mudFossilTile = TileRegistry.registerTile("mudfossiltile", new MudFossilTile(), 0, false);

    }

    public static void registerRecipes() {
        Recipes.registerModRecipe(new Recipe(
                "litwoodfloor",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anylog", 5),
                        new Ingredient("torch", 1)
                }
        ).showAfter("woodfloor"));
        Recipes.registerModRecipe(new Recipe(
                "litwoodpathtile",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anylog", 5),
                        new Ingredient("torch", 1)
                }
        ).showAfter("woodpathtile"));
        Recipes.registerModRecipe(new Recipe(
                "litstonefloor",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("stone", 5),
                        new Ingredient("torch", 1)
                }
        ).showAfter("stonefloor"));
        Recipes.registerModRecipe(new Recipe(
                "litstonepathtile",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("stone", 5),
                        new Ingredient("torch", 1)
                }
        ).showAfter("stonepathtile"));
        Recipes.registerModRecipe(new Recipe(
                "litswampstonefloor",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("swampstone", 5),
                        new Ingredient("torch", 1)
                }
        ).showAfter("swampstonefloor"));
        Recipes.registerModRecipe(new Recipe(
                "litswampstonepathtile",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("swampstone", 5),
                        new Ingredient("torch", 1)
                }
        ).showAfter("swampstonepathtile"));
    }
}

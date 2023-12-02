package settlementexpansion.registry;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import settlementexpansion.object.*;
import settlementexpansion.object.furniture.*;
import settlementexpansion.object.trap.CannonTrapObject;

import java.awt.*;

public class ObjectModRegistry {

    public static String[] woodFurnitureTypes = new String[]{"oak", "spruce", "pine", "palm", "deadwood"};
    public static String[] woodTypes = new String[]{"oak", "spruce", "pine", "palm", "deadwood", "willow"};

    public static void registerObjects() {
        Color woodFurniture = new Color(150, 119, 70);

        for (String type : woodFurnitureTypes) {
            ObjectRegistry.registerObject(type + "leatherchair", new LeatherChairObject(type + "leatherchair", woodFurniture), 6F, true);
            ObjectRegistry.registerObject(type + "tallmirror", new TallMirrorObject(type + "tallmirror", woodFurniture), 10F, true);
        }

        ObjectRegistry.registerObject("dryingrack", new DryingRackObject(), 50.0F, true);
        ObjectRegistry.registerObject("sprucefishwalldisplay", new FishDisplayObject("sprucefishwalldisplay", 0), 10, true);
        ObjectRegistry.registerObject("safebox", new SafeBoxInventoryObject("safebox", 40, new Color(49, 52, 70)), 40, true);
        ObjectRegistry.registerObject("cannontrap", new CannonTrapObject(), 50F, true);

        SeedlingTableObject.registerSeedlingTable();
        StudyTableObject.registerSeedlingTable();
        ToolsRackObject.registerToolsRack();
        BlueprintTableObject.registerBlueprintTable();
        BlueprintObject.registerBlueprints();
    }

    public static void replaceObjects() {
        ObjectRegistry.replaceObject("settlementflag", new SettlementFlagModObject(), 500, true);
    }

    public static void registerRecipes() {

        Recipes.registerModRecipe(new Recipe(
                "cannontrap",
                1,
                RecipeTechRegistry.ADVANCED_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("handcannon", 1),
                        new Ingredient("ironbar", 10),
                        new Ingredient("copperbar", 5)
                },
                false
        ).showAfter("upgradestation"));

        Recipes.registerModRecipe(new Recipe(
                "dryingrack",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anylog", 20)
                },
                false
        ).showAfter("net"));

        Recipes.registerModRecipe(new Recipe(
                "toolsrack",
                1,
                RecipeTechRegistry.CARPENTER,
                new Ingredient[]{
                        new Ingredient("anylog", 15)
                },
                false
        ).showAfter("barrel"));

        Recipes.registerModRecipe(new Recipe(
                "safebox",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("ironbar", 12)
                },
                false
        ).showAfter("storagebox"));

        Recipes.registerModRecipe(new Recipe(
                "sprucefishwalldisplay",
                1,
                RecipeTechRegistry.CARPENTER,
                new Ingredient[]{
                        new Ingredient("anylog", 10)
                },
                false
        ).showAfter("sprucechest"));

        Recipes.registerModRecipe(new Recipe(
                "seedlingtable",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[]{
                        new Ingredient("anylog", 10),
                        new Ingredient("farmland", 3),
                        new Ingredient("ironbar", 1)
                },
                false
        ).showAfter("dryingrack"));

        Recipes.registerModRecipe(new Recipe(
                "blueprinttable",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[]{
                        new Ingredient("anylog", 10),
                        new Ingredient("iceblossom", 5)
                },
                false
        ).showAfter("seedlingtable"));

        Recipes.registerModRecipe(new Recipe(
                "studytable",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[]{
                        new Ingredient("anylog", 20)
                },
                false
        ).showAfter("blueprinttable"));

        for (String type : woodFurnitureTypes) {
            Recipes.registerModRecipe(new Recipe(
                    type + "leatherchair",
                    1,
                    RecipeTechRegistry.CARPENTER,
                    new Ingredient[]{
                            new Ingredient(type + "log", 5),
                            new Ingredient("tannedleather", 1)

                    },
                    false
            ).showAfter(type + "chair"));

            Recipes.registerModRecipe(new Recipe(
                    type + "tallmirror",
                    1,
                    RecipeTechRegistry.CARPENTER,
                    new Ingredient[]{
                            new Ingredient(type + "log", 5),
                            new Ingredient("glass", 5)

                    },
                    false
            ).showAfter(type + "bookshelf"));
        }

        SeedlingTableObject.registerSeedlingRecipes();
        BlueprintObject.registerBlueprintRecipes();
    }
}

package settlementexpansion.registry;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.level.gameObject.WallObject;
import settlementexpansion.object.*;
import settlementexpansion.object.furniture.*;
import settlementexpansion.object.trap.CannonTrapObject;

import java.awt.*;

public class ObjectModRegistry {

    public static String[] woodFurnitureTypes = new String[]{"oak", "spruce", "pine", "palm", "deadwood"};

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

        ObjectRegistry.registerObject("reinforcedouterwoodwall", new ReinforcedOuterWallObject("reinforcedouterwoodwall", 100, ToolType.ALL, new Color(86, 69, 40)), 2F, true);
        ObjectRegistry.registerObject("reinforcedouterpinewall", new ReinforcedOuterWallObject("reinforcedouterpinewall", 100, ToolType.ALL, new Color(104, 69, 34)), 2F, true);
        ObjectRegistry.registerObject("reinforcedouterpalmwall", new ReinforcedOuterWallObject("reinforcedouterpalmwall", 100, ToolType.ALL, new Color(104, 79, 39)), 2F, true);
        ObjectRegistry.registerObject("reinforcedouterstonewall", new ReinforcedOuterWallObject("reinforcedouterstonewall", 100, ToolType.PICKAXE, new Color(105, 105, 105)), 2F, true);
        ObjectRegistry.registerObject("reinforcedoutersandstonewall", new ReinforcedOuterWallObject("reinforcedoutersandstonewall", 100, ToolType.PICKAXE, new Color(215, 215, 125)), 2F, true);
        ObjectRegistry.registerObject("reinforcedouterswampstonewall", new ReinforcedOuterWallObject("reinforcedouterswampstonewall", 100, ToolType.PICKAXE, new Color(56, 69, 53)), 2F, true);
        ObjectRegistry.registerObject("reinforcedoutersnowstonewall", new ReinforcedOuterWallObject("reinforcedoutersnowstonewall", 100, ToolType.PICKAXE, new Color(207, 207, 207)), 2F, true);
        ObjectRegistry.registerObject("reinforcedouterobsidianwall", new ReinforcedOuterWallObject("reinforcedouterobsidianwall", 100, ToolType.PICKAXE, new Color(37, 27, 40)), 2F, true);

        WallSecureDoorObject.registerSecureDoorPair("securewooddoor", (WallObject) ObjectRegistry.getObject("woodwall"), 5, true);
        WallSecureDoorObject.registerSecureDoorPair("securepalmdoor", (WallObject) ObjectRegistry.getObject("palmwall"), 5, true);
        WallSecureDoorObject.registerSecureDoorPair("securepinedoor", (WallObject) ObjectRegistry.getObject("pinewall"), 5, true);
        WallSecureDoorObject.registerSecureDoorPair("securestonedoor", (WallObject) ObjectRegistry.getObject("stonewall"), 5, true);
        WallSecureDoorObject.registerSecureDoorPair("securesandstonedoor", (WallObject) ObjectRegistry.getObject("sandstonewall"), 5, true);
        WallSecureDoorObject.registerSecureDoorPair("securesnowstonedoor", (WallObject) ObjectRegistry.getObject("snowstonewall"), 5, true);
        WallSecureDoorObject.registerSecureDoorPair("secureswampstonedoor", (WallObject) ObjectRegistry.getObject("swampstonewall"), 5, true);
        WallSecureDoorObject.registerSecureDoorPair("secureobsidiandoor", (WallObject) ObjectRegistry.getObject("obsidianwall"), 5, true);

        SeedlingTableObject.registerSeedlingTable();
        StudyTableObject.registerSeedlingTable();
        ToolsRackObject.registerToolsRack();
        BlueprintTableObject.registerBlueprintTable();
        BlueprintObject.registerBlueprints();
        InspectionTableObject.registerInspectionTable();
    }

    public static void registerRecipes() {

        Recipes.registerModRecipe(new Recipe(
                "reinforcedouterwoodwall",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anylog", 2),
                        new Ingredient("stone", 2),
                },
                false
        ).showAfter("woodwall"));

        Recipes.registerModRecipe(new Recipe(
                "reinforcedouterpinewall",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("pinelog", 2),
                        new Ingredient("stone", 2),
                },
                false
        ).showAfter("pinewall"));

        Recipes.registerModRecipe(new Recipe(
                "reinforcedouterpalmwall",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("palmlog", 2),
                        new Ingredient("stone", 2),
                },
                false
        ).showAfter("palmwall"));

        Recipes.registerModRecipe(new Recipe(
                "reinforcedouterstonewall",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("stone", 2),
                        new Ingredient("ironbar", 1),
                },
                false
        ).showAfter("stonewall"));

        Recipes.registerModRecipe(new Recipe(
                "reinforcedoutersandstonewall",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[]{
                        new Ingredient("sandstone", 2),
                        new Ingredient("ironbar", 1),
                },
                false
        ).showAfter("sandstonewall"));

        Recipes.registerModRecipe(new Recipe(
                "reinforcedouterswampstonewall",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[]{
                        new Ingredient("swampstone", 2),
                        new Ingredient("ironbar", 1),
                },
                false
        ).showAfter("swampstonewall"));

        Recipes.registerModRecipe(new Recipe(
                "reinforcedoutersnowstonewall",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[]{
                        new Ingredient("snowstone", 2),
                        new Ingredient("ironbar", 1),
                },
                false
        ).showAfter("snowstonewall"));

        Recipes.registerModRecipe(new Recipe(
                "reinforcedouterobsidianwall",
                1,
                RecipeTechRegistry.ADVANCED_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("obsidian", 2),
                        new Ingredient("goldbar", 1),
                },
                false
        ).showAfter("obsidianwall"));

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
                RecipeTechRegistry.DEMONIC,
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

        Recipes.registerModRecipe(new Recipe(
                "securewooddoor",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anylog", 4),
                        new Ingredient("ironbar", 1)
                },
                false
        ).showAfter("wooddoor"));

        Recipes.registerModRecipe(new Recipe(
                "securepalmdoor",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("palmlog", 4),
                        new Ingredient("ironbar", 1)
                },
                false
        ).showAfter("palmdoor"));

        Recipes.registerModRecipe(new Recipe(
                "securepinedoor",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("pinelog", 4),
                        new Ingredient("ironbar", 1)
                },
                false
        ).showAfter("pinedoor"));

        Recipes.registerModRecipe(new Recipe(
                "securestonedoor",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("stone", 15),
                        new Ingredient("ironbar", 1)
                },
                false
        ).showAfter("stonedoor"));

        Recipes.registerModRecipe(new Recipe(
                "secureswampstonedoor",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[]{
                        new Ingredient("swampstone", 15),
                        new Ingredient("ironbar", 1)
                },
                false
        ).showAfter("swampstonedoor"));

        Recipes.registerModRecipe(new Recipe(
                "securesnowstonedoor",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[]{
                        new Ingredient("snowstone", 15),
                        new Ingredient("ironbar", 1)
                },
                false
        ).showAfter("snowstonedoor"));

        Recipes.registerModRecipe(new Recipe(
                "securesandstonedoor",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[]{
                        new Ingredient("sandstone", 15),
                        new Ingredient("ironbar", 1)
                },
                false
        ).showAfter("sandstonedoor"));

        Recipes.registerModRecipe(new Recipe(
                "secureobsidiandoor",
                1,
                RecipeTechRegistry.ADVANCED_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("obsidian", 8),
                        new Ingredient("goldbar", 1)
                },
                false
        ).showAfter("obsidiandoor"));

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

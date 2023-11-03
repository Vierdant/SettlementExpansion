package settlementexpansion;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.*;
import necesse.engine.util.GameRandom;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.lootTable.presets.CrateLootTable;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import settlementexpansion.item.geode.ExampleGeode;
import settlementexpansion.item.material.TannedLeatherMaterial;
import settlementexpansion.mob.friendly.BlacksmithModHumanMob;
import settlementexpansion.object.DryingRackObject;
import settlementexpansion.object.furniture.FishDisplayObject;
import settlementexpansion.object.furniture.SafeBoxInventoryObject;
import settlementexpansion.packet.PacketLockedInventory;
import settlementexpansion.registry.ContainerModRegistry;
import settlementexpansion.registry.ModResources;
import settlementexpansion.registry.InterfaceModRegistry;
import settlementexpansion.tile.LitFloorTile;
import settlementexpansion.registry.RecipeTechModRegistry;

import java.awt.*;

@ModEntry
public class SettlementExpansion {

    public void init() {
        System.out.println("Settlement Expansion was enabled!");

        // Register essentials
        RecipeTechModRegistry.registerModdedTech();

        // Register objects
        ObjectRegistry.registerObject("dryingrack", new DryingRackObject(), 50.0F, true);
        ObjectRegistry.registerObject("fishwalldisplay", new FishDisplayObject("sprucefishwalldisplay", 0), 10, true);
        ObjectRegistry.registerObject("safebox", new SafeBoxInventoryObject("safebox", 40, new Color(150, 119, 70)), 40, true);

        // Register items
        ItemCategory.createCategory("A-F-A", "misc", "geodes");

        ItemRegistry.registerItem("tannedleather", new TannedLeatherMaterial(), 10, true);
        ItemRegistry.registerItem("examplegeode", new ExampleGeode(), 10, true);

        // Register tiles
        TileRegistry.registerTile("litwoodfloor", new LitFloorTile("litwoodfloor", new Color(153, 127, 98), 150, 50, 0.2f), 1, false);

        // Register Containers
        ContainerModRegistry.registerContainers();

        // Register Mobs

        // Register Packets
        PacketRegistry.registerPacket(PacketLockedInventory.class);

    }

    public void initResources() {
        InterfaceModRegistry.loadTextures();
        ModResources.loadSounds();
    }

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
                "examplegeode",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anylog", 1)
                },
                false
        ));

        Recipes.registerModRecipe(new Recipe(
                "safebox",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anylog", 20)
                },
                false
        ));

        Recipes.registerModRecipe(new Recipe(
                "fishwalldisplay",
                1,
                RecipeTechRegistry.CARPENTER,
                new Ingredient[]{
                        new Ingredient("anylog", 10)
                },
                false
        ));

        Recipes.registerModRecipe(new Recipe(
                "tannedleather",
                1,
                RecipeTechModRegistry.DRYINGRACK,
                new Ingredient[]{
                        new Ingredient("leather", 1)
                }
        ));

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
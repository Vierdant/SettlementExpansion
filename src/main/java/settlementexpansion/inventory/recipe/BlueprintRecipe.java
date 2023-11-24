package settlementexpansion.inventory.recipe;

import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.recipe.Ingredient;

import java.util.*;

public class BlueprintRecipe {

    ArrayList<Ingredient> ingredients;

    public BlueprintRecipe() {
        this.ingredients = new ArrayList<>();
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public boolean canBuild(PlayerMob player) {
        for (Ingredient ingredient : ingredients) {
            int amount = player.getInv().getAmount(ingredient.getDisplayItem(), false, false, false, "craftcheck");
            if (ingredient.getIngredientAmount() > amount) {
                return false;
            }
        }
        return true;
    }

    public void consumeBuildMaterials(PlayerInventoryManager inventory) {
        for (Ingredient ingredient : ingredients) {
            inventory.removeItems(ingredient.getDisplayItem(), ingredient.getIngredientAmount(), false, false, false, "buildblueprint");
        }
    }

    public GameTooltips getTooltip(PlayerMob player) {
        ListGameTooltips tooltips = new ListGameTooltips();
        for (Ingredient ingredient : this.ingredients) {
            int amount = player.getInv().getAmount(ingredient.getDisplayItem(), false, false, false, "craftcheck");
            tooltips.add(ingredient.getTooltips(amount, true));
        }

        return tooltips;
    }

}

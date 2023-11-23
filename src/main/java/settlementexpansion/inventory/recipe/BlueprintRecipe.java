package settlementexpansion.inventory.recipe;

import necesse.engine.localization.Localization;
import necesse.engine.util.HashMapSet;
import necesse.engine.util.MapIterator;
import necesse.engine.util.ObjectValue;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.InventoryRange;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.Level;

import java.util.*;

public class BlueprintRecipe {

    ArrayList<Ingredient> ingredients;

    public BlueprintRecipe() {
        this.ingredients = new ArrayList<>();
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public boolean canCraft(PlayerMob player) {
        for (Ingredient ingredient : ingredients) {
            int amount = player.getInv().getAmount(ingredient.getDisplayItem(), false, false, false, "craftcheck");
            if (ingredient.getIngredientAmount() > amount) {
                return false;
            }
        }
        return true;
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

package settlementexpansion.inventory.recipe;

import necesse.engine.Settings;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;

public class CanCraftBlueprint {
    private final BlueprintRecipe recipe;
    private int canCraft;
    private int hasAnyItems;
    public final int[] haveIngredients;
    public final boolean countAllIngredients;

    public CanCraftBlueprint(BlueprintRecipe recipe, boolean countAllIngredients) {
        this.recipe = recipe;
        this.haveIngredients = new int[recipe.ingredients.size()];
        this.countAllIngredients = countAllIngredients && Settings.showIngredientsAvailable;
    }

    public void addIngredient(int ingredientIndex, int amount) {
        Ingredient ingredient = this.recipe.ingredients.get(ingredientIndex);
        if (ingredient.getIngredientAmount() == 0) {
            if (this.haveIngredients[ingredientIndex] == 0) {
                this.haveIngredients[ingredientIndex] = -1;
                ++this.canCraft;
                ++this.hasAnyItems;
            }
        } else if (amount > 0) {
            if (this.haveIngredients[ingredientIndex] == 0) {
                ++this.hasAnyItems;
            }

            boolean haveEnoughBefore = this.haveIngredients[ingredientIndex] >= ingredient.getIngredientAmount();
            this.haveIngredients[ingredientIndex] += amount;
            boolean haveEnoughAfter = this.haveIngredients[ingredientIndex] >= ingredient.getIngredientAmount();
            if (!haveEnoughBefore && haveEnoughAfter) {
                ++this.canCraft;
            }
        }

    }

    public boolean hasAnyIngredients(int ingredientIndex) {
        Ingredient ingredient = this.recipe.ingredients.get(ingredientIndex);
        if (ingredient.getIngredientAmount() == 0) {
            return this.haveIngredients[ingredientIndex] == -1;
        } else {
            return this.haveIngredients[ingredientIndex] > 0;
        }
    }

    public boolean canCraft() {
        return this.canCraft >= this.haveIngredients.length;
    }

    public boolean hasAnyItems() {
        return this.hasAnyItems > 0;
    }

    public boolean hasAnyOfAllItems() {
        return this.hasAnyItems >= this.haveIngredients.length;
    }

    public static CanCraftBlueprint allTrue(BlueprintRecipe r) {
        CanCraftBlueprint out = new CanCraftBlueprint(r, false);

        for(int i = 0; i < r.ingredients.size(); ++i) {
            out.addIngredient(i, r.ingredients.get(i).getIngredientAmount());
        }

        return out;
    }
}

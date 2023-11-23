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

    public CanCraftBlueprint canCraft(Level level, PlayerMob player, Inventory inv, boolean countAllIngredients) {
        return this.canCraft(level, player, Collections.singletonList(inv), countAllIngredients);
    }

    public CanCraftBlueprint canCraft(Level level, PlayerMob player, Iterable<Inventory> invList, boolean countAllIngredients) {
        return this.canCraftRange(level, player, () ->
                new MapIterator<>(invList.iterator(), InventoryRange::new), countAllIngredients);
    }

    public CanCraftBlueprint canCraftRange(Level level, PlayerMob player, InventoryRange inv, boolean countAllIngredients) {
        return this.canCraftRange(level, player, Collections.singletonList(inv), countAllIngredients);
    }

    public CanCraftBlueprint canCraftRange(Level level, PlayerMob player, Iterable<InventoryRange> invList, boolean countAllIngredients) {
        CanCraftBlueprint out = new CanCraftBlueprint(this, countAllIngredients);
        HashMapSet<Inventory, Integer> usedSlots = new HashMapSet<>();
        LinkedList<ObjectValue<Integer, Ingredient>> sortedIngredients = new LinkedList<>();
        LinkedList<ObjectValue<Integer, Ingredient>> mustHaveIngredients = new LinkedList<>();

        for(int i = this.ingredients.size() - 1; i >= 0; --i) {
            Ingredient ingredient = this.ingredients.get(i);
            if (ingredient.getIngredientAmount() == 0) {
                mustHaveIngredients.add(new ObjectValue<>(i, ingredient));
            } else if (ingredient.isGlobalIngredient()) {
                sortedIngredients.addLast(new ObjectValue<>(i, ingredient));
            } else {
                sortedIngredients.addFirst(new ObjectValue<>(i, ingredient));
            }
        }

        Iterator var13 = mustHaveIngredients.iterator();

        while(var13.hasNext()) {
            ObjectValue<Integer, Ingredient> e = (ObjectValue)var13.next();
            sortedIngredients.addFirst(e);
        }

        var13 = invList.iterator();

        while(var13.hasNext()) {
            InventoryRange range = (InventoryRange)var13.next();
            if (range.inventory.canBeUsedForCrafting()) {
                range.inventory.countIngredientAmount(level, player, range.startSlot, range.endSlot, (inventory, slot, item) -> {
                    if (inventory == null || inventory.canBeUsedForCrafting()) {
                        if (!(usedSlots.get(inventory)).contains(slot)) {
                            int usedItems = 0;

                            for (ObjectValue<Integer, Ingredient> e : sortedIngredients) {
                                int index = e.object;
                                Ingredient ingredient = e.value;
                                if (ingredient.matchesItem(item.item)) {
                                    int itemsRemaining = item.getAmount() - usedItems;
                                    int foundItems = out.countAllIngredients ? itemsRemaining : Math.min(itemsRemaining, ingredient.getIngredientAmount());
                                    out.addIngredient(index, foundItems);
                                    usedItems += foundItems;
                                    if (!out.countAllIngredients && usedItems >= item.getAmount()) {
                                        break;
                                    }
                                }
                            }

                            usedSlots.add(inventory, slot);
                        }
                    }
                });
            }
        }

        return out;
    }

    public ArrayList<InventoryItemsRemoved> craft(Level level, PlayerMob player, Inventory inv) {
        return this.craft(level, player, Collections.singletonList(inv));
    }

    public ArrayList<InventoryItemsRemoved> craft(Level level, PlayerMob player, Iterable<Inventory> invList) {
        return this.craftRange(level, player, () -> {
            return new MapIterator<>(invList.iterator(), InventoryRange::new);
        });
    }

    public ArrayList<InventoryItemsRemoved> craftRange(Level level, PlayerMob player, InventoryRange inv) {
        return this.craftRange(level, player, Collections.singletonList(inv));
    }

    public ArrayList<InventoryItemsRemoved> craftRange(Level level, PlayerMob player, Iterable<InventoryRange> invList) {
        ArrayList<InventoryItemsRemoved> usedItems = new ArrayList<>();
        ArrayList<Ingredient> var5 = this.ingredients;
        int var6 = var5.size();

        for (Ingredient in : var5) {
            if (in.getIngredientAmount() > 0) {
                int amountRemaining = in.getIngredientAmount();
                Iterator<InventoryRange> var10 = invList.iterator();

                while (var10.hasNext()) {
                    InventoryRange invRange = var10.next();
                    if (invRange.inventory.canBeUsedForCrafting()) {
                        if (amountRemaining <= 0) {
                            break;
                        }

                        int removed = invRange.inventory.removeItems(level, player, in, amountRemaining, invRange.startSlot, invRange.endSlot, usedItems);
                        amountRemaining -= removed;
                    }
                }
            }
        }

        return usedItems;
    }

    public GameTooltips getTooltip(PlayerMob perspective) {
        return this.getTooltip((int[])null, false, perspective);
    }

    public GameTooltips getTooltip(CanCraftBlueprint canCraft, PlayerMob perspective) {
        if (canCraft == null) {
            canCraft = CanCraftBlueprint.allTrue(this);
        }

        return this.getTooltip(canCraft.haveIngredients, canCraft.countAllIngredients, perspective);
    }

    public GameTooltips getTooltip(int[] haveIngredientsAmount, boolean countAllIngredients, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        for(int i = 0; i < this.ingredients.size(); ++i) {
            Ingredient ingredient = this.ingredients.get(i);
            tooltips.add(ingredient.getTooltips(haveIngredientsAmount == null ? ingredient.getIngredientAmount() : haveIngredientsAmount[i], countAllIngredients));
        }

        return tooltips;
    }

}

package settlementexpansion.map.preset;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.inventory.recipe.Ingredient;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import settlementexpansion.inventory.recipe.BlueprintRecipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlueprintPreset extends Preset {

    public BlueprintRecipe recipe;
    public BlueprintPresetID id;

    public BlueprintPreset(String script) {
        super(script);
        this.id = BlueprintPresetID.getMatchingScript(script);
        calculateIngredients();
    }

    public BlueprintPreset(BlueprintPresetID id) {
        this(id.getScript());
        this.id = id;
    }

    public void calculateIngredients() {
        this.recipe = new BlueprintRecipe();
        for (int id : Arrays.stream(this.tiles).distinct().filter((i) -> i != -1).toArray()) {
            recipe.addIngredient(new Ingredient(TileRegistry.getTile(id).getStringID(),
                    Arrays.stream(this.tiles).filter((i) -> i == id).boxed().toArray().length));
        }
        for (int id : Arrays.stream(this.objects).distinct().filter((i) -> i != 0).filter((id)
                -> ObjectRegistry.getObject(id).isMultiTileMaster()).toArray()) {
            recipe.addIngredient(new Ingredient(ObjectRegistry.getObject(id).getStringID(),
                    Arrays.stream(this.objects).filter((i) -> i == id).boxed().toArray().length));
        }
    }


}

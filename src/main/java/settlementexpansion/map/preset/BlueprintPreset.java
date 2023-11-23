package settlementexpansion.map.preset;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.inventory.recipe.Ingredient;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;
import settlementexpansion.inventory.recipe.BlueprintRecipe;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlueprintPreset extends Preset {

    public BlueprintRecipe recipe;
    public BlueprintPresetID id;

    public BlueprintPreset(int x, int y) {
        super(x, y);
    }

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

    public BlueprintPreset rotate(PresetRotation rotation) throws PresetRotateException {
        Point dim = PresetUtils.getRotatedPoint(this.width, this.height, 0, 0, rotation);
        BlueprintPreset preset = this.newObject(Math.abs(dim.x), Math.abs(dim.y));
        preset.rotateData(this, rotation);
        return preset;
    }

    private void rotateData(Preset from, PresetRotation rotation) throws PresetRotateException {
        boolean[] computed = new boolean[this.width * this.height];

        for(int x = 0; x < from.width; ++x) {
            for(int y = 0; y < from.height; ++y) {
                Point rp = PresetUtils.getRotatedPointInSpace(x, y, from.width, from.height, rotation);
                setInt(this.tiles, this.width, rp.x, rp.y, getInt(from.tiles, from.width, x, y));
                int objectID = getInt(from.objects, from.width, x, y);
                if (objectID != -1) {
                    GameObject obj = ObjectRegistry.getObject(objectID);
                    if (obj != null) {
                        byte objectRotation = getByte(from.objectRotations, from.width, x, y);
                        MultiTile multiTile = obj.getMultiTile(objectRotation);
                        Point posOffset = multiTile.getPresetRotationOffset(rotation);
                        if (posOffset == null) {
                            throw new PresetRotateException(obj.getDisplayName() + " could not be rotated");
                        }

                        setInt(this.objects, this.width, rp.x + posOffset.x, rp.y + posOffset.y, objectID);
                        setByte(this.objectRotations, this.width, rp.x, rp.y, (byte)multiTile.getPresetRotation(rotation));
                        setBoolean(computed, this.width, rp.x, rp.y, true);
                    } else {
                        setInt(this.objects, this.width, rp.x, rp.y, objectID);
                    }
                } else if (!getBoolean(computed, this.width, rp.x, rp.y)) {
                    setInt(this.objects, this.width, rp.x, rp.y, -1);
                }

                setByte(this.wires, this.width, rp.x, rp.y, getByte(from.wires, from.width, x, y));
            }
        }

        from.logicGates.forEach((p, lg) -> {
            this.logicGates.put(PresetUtils.getRotatedPointInSpace(p.x, p.y, from.width, from.height, rotation), lg);
        });
        Iterator var12 = from.applyPredicates.iterator();

        while(var12.hasNext()) {
            ApplyPredicate applyPredicate = (ApplyPredicate)var12.next();
            this.applyPredicates.add(applyPredicate.rotate(rotation, from.width, from.height));
        }

        var12 = from.customPreApplies.iterator();

        CustomApply customApply;
        while(var12.hasNext()) {
            customApply = (CustomApply)var12.next();
            this.customPreApplies.add(customApply.rotate(rotation, from.width, from.height));
        }

        var12 = from.customApplies.iterator();

        while(var12.hasNext()) {
            customApply = (CustomApply)var12.next();
            this.customApplies.add(customApply.rotate(rotation, from.width, from.height));
        }

        this.tileApplyListeners.addAll(from.tileApplyListeners);
        this.objectApplyListeners.addAll(from.objectApplyListeners);
        this.wireApplyListeners.addAll(from.wireApplyListeners);
    }

    protected BlueprintPreset newObject(int width, int height) {
        return new BlueprintPreset(width, height);
    }


}

package settlementexpansion.map.preset;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.inventory.recipe.Ingredient;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.presets.*;
import settlementexpansion.inventory.recipe.BlueprintRecipe;

import java.awt.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class BlueprintPreset extends Preset {
    private BlueprintRecipe recipe;
    public BlueprintPresetID id;
    public boolean canPlaceOnLiquid;
    public boolean canPlaceOnShore;


    public BlueprintPreset(int x, int y, BlueprintPresetID id, BlueprintRecipe recipe, boolean canPlaceOnShore, boolean canPlaceOnLiquid) {
        super(x, y);
        this.id = id;
        this.recipe = recipe;
        this.canPlaceOnShore = canPlaceOnShore;
        this.canPlaceOnLiquid = canPlaceOnLiquid;
    }

    public BlueprintPreset(String script, boolean canPlaceOnShore, boolean canPlaceOnLiquid) {
        super(script);
        this.id = BlueprintPresetID.getMatchingScript(script);
        this.canPlaceOnShore = canPlaceOnShore;
        this.canPlaceOnLiquid = canPlaceOnLiquid;
        calculateIngredients();

    }

    public BlueprintPreset(BlueprintPresetID id, boolean canPlaceOnShore, boolean canPlaceOnLiquid) {
        this(id.getScript(), canPlaceOnShore, canPlaceOnLiquid);
    }

    public BlueprintRecipe getRecipe() {
        if (recipe == null) {
            calculateIngredients();
        }

        return recipe;
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

    public LinkedList<UndoLogic> applyToLevel(Level level, int levelX, int levelY) {
        LinkedList<UndoLogic> undoLogics = new LinkedList<>();

        UndoLogic undoLogic;

        for (CustomApply custom : this.customPreApplies) {
            undoLogic = custom.applyToLevel(level, levelX, levelY);
            if (undoLogic != null) {
                undoLogics.add(undoLogic);
            }
        }

        for(int i = 0; i < this.width; ++i) {
            int tileX = levelX + i;
            if (tileX >= 0 && tileX < level.width) {
                for(int j = 0; j < this.height; ++j) {
                    int tileY = levelY + j;
                    if (tileY >= 0 && tileY < level.height) {
                        int tile = getInt(this.tiles, this.width, i, j);
                        if (tile != -1) {
                            level.setTile(tileX, tileY, tile);
                            level.sendTileUpdatePacket(tileX, tileY);
                            for (TileApplyListener listener : this.tileApplyListeners) {
                                listener.onTileApply(level, tileX, tileY, tile);
                            }
                        }

                        byte wireData = getByte(this.wires, this.width, i, j);

                        int object;
                        for(object = 0; object < 4; ++object) {
                            if (GameMath.getBit(wireData, object * 2)) {
                                boolean isThere = GameMath.getBit(wireData, object * 2 + 1);
                                level.wireManager.setWire(tileX, tileY, object, isThere);
                                level.sendWireUpdatePacket(tileX, tileY);

                                for (WireApplyListener listener : this.wireApplyListeners) {
                                    listener.onWireApply(level, tileX, tileY, tile, isThere);
                                }
                            }
                        }

                        level.logicLayer.clearLogicGate(tileX, tileY);
                        object = getInt(this.objects, this.width, i, j);
                        if (object != -1) {
                            byte objectRotation = getByte(this.objectRotations, this.width, i, j);
                            level.setObject(tileX, tileY, object, objectRotation);
                            level.sendObjectUpdatePacket(tileX, tileY);

                            for (ObjectApplyListener listener : this.objectApplyListeners) {
                                listener.onObjectApply(level, tileX, tileY, object, objectRotation);
                            }


                        }
                    }
                }
            }
        }

        this.logicGates.forEach((tilex, presetLogicGate) -> {
            int tileX = levelX + tilex.x;
            int tileY = levelY + tilex.y;
            if (tileX >= 0 && tileX < level.width && tileY >= 0 && tileY < level.height) {
                presetLogicGate.applyToLevel(level, tileX, tileY);
            }

        });

        for (CustomApply custom : this.customApplies) {
            undoLogic = custom.applyToLevel(level, levelX, levelY);
            if (undoLogic != null) {
                undoLogics.add(undoLogic);
            }
        }

        return undoLogics;
    }

    public boolean canApplyToLevel(Level level, int tileX, int tileY) {
        int endX = tileX + width - 1;
        int endY = tileY + height - 1;

        for(int x = tileX; x <= endX; ++x) {
            for(int y = tileY; y <= endY; ++y) {
                if (level.getObjectID(x, y) != 0 && !level.getObject(x, y).isGrass) {
                    return false;
                } else if (!this.canPlaceOnLiquid && level.isLiquidTile(x, y)) {
                    return false;
                } else if (!this.canPlaceOnShore && level.isShore(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    public BlueprintPreset copy() {
        BlueprintPreset preset = this.newObject(this.width, this.height);
        preset.copyData(this);
        return preset;
    }

    private void copyData(BlueprintPreset from) {
        this.tiles = from.tiles.clone();
        this.objects = from.objects.clone();
        this.objectRotations = from.objectRotations.clone();
        this.wires = from.wires.clone();
        this.logicGates.putAll(from.logicGates);
        this.applyPredicates.addAll(from.applyPredicates);
        this.customPreApplies.addAll(from.customPreApplies);
        this.customApplies.addAll(from.customApplies);
        this.tileApplyListeners.addAll(from.tileApplyListeners);
        this.objectApplyListeners.addAll(from.objectApplyListeners);
        this.wireApplyListeners.addAll(from.wireApplyListeners);
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
        Iterator iterator = from.applyPredicates.iterator();

        while(iterator.hasNext()) {
            ApplyPredicate applyPredicate = (ApplyPredicate)iterator.next();
            this.applyPredicates.add(applyPredicate.rotate(rotation, from.width, from.height));
        }

        iterator = from.customPreApplies.iterator();

        CustomApply customApply;
        while(iterator.hasNext()) {
            customApply = (CustomApply)iterator.next();
            this.customPreApplies.add(customApply.rotate(rotation, from.width, from.height));
        }

        iterator = from.customApplies.iterator();

        while(iterator.hasNext()) {
            customApply = (CustomApply)iterator.next();
            this.customApplies.add(customApply.rotate(rotation, from.width, from.height));
        }

        this.tileApplyListeners.addAll(from.tileApplyListeners);
        this.objectApplyListeners.addAll(from.objectApplyListeners);
        this.wireApplyListeners.addAll(from.wireApplyListeners);
    }

    public BlueprintPreset attemptMirrorX() {
        try {
            return this.mirrorX();
        } catch (PresetMirrorException e) {
            return this.copy();
        }
    }

    public BlueprintPreset mirrorX() throws PresetMirrorException {
        BlueprintPreset preset = this.newObject(this.width, this.height);
        preset.mirrorXData(this);
        return preset;
    }

    private void mirrorXData(Preset from) throws PresetMirrorException {
        boolean[] computed = new boolean[this.width * this.height];

        for(int x = 0; x < this.width; ++x) {
            int mirrorX = this.getMirroredX(x);

            for(int y = 0; y < this.height; ++y) {
                setInt(this.tiles, this.width, mirrorX, y, getInt(from.tiles, this.width, x, y));
                int objectID = getInt(from.objects, this.width, x, y);
                if (objectID != -1) {
                    GameObject obj = ObjectRegistry.getObject(objectID);
                    if (obj != null) {
                        byte objectRotation = getByte(from.objectRotations, this.width, x, y);
                        MultiTile multiTile = obj.getMultiTile(objectRotation);
                        Point posOffset = multiTile.getMirrorXPosOffset();
                        if (posOffset == null) {
                            throw new PresetMirrorException(obj.getDisplayName() + " could not be mirrored");
                        }

                        setInt(this.objects, this.width, mirrorX + posOffset.x, y + posOffset.y, objectID);
                        setByte(this.objectRotations, this.width, mirrorX, y, (byte)multiTile.getXMirrorRotation());
                        setBoolean(computed, this.width, mirrorX, y, true);
                    } else {
                        setInt(this.objects, this.width, mirrorX, y, objectID);
                    }
                } else if (!getBoolean(computed, this.width, mirrorX, y)) {
                    setInt(this.objects, this.width, mirrorX, y, objectID);
                }

                setByte(this.wires, this.width, mirrorX, y, getByte(from.wires, this.width, x, y));
            }
        }

        from.logicGates.forEach((p, lg) -> {
            this.logicGates.put(new Point(this.getMirroredX(p.x), p.y), lg);
        });
        Iterator iterator = from.applyPredicates.iterator();

        while(iterator.hasNext()) {
            ApplyPredicate applyPredicate = (ApplyPredicate)iterator.next();
            this.applyPredicates.add(applyPredicate.mirrorX(this.width));
        }

        iterator = from.customPreApplies.iterator();

        CustomApply customApply;
        while(iterator.hasNext()) {
            customApply = (CustomApply)iterator.next();
            this.customPreApplies.add(customApply.mirrorX(this.width));
        }

        iterator = from.customApplies.iterator();

        while(iterator.hasNext()) {
            customApply = (CustomApply)iterator.next();
            this.customApplies.add(customApply.mirrorX(this.width));
        }

        this.tileApplyListeners.addAll(from.tileApplyListeners);
        this.objectApplyListeners.addAll(from.objectApplyListeners);
        this.wireApplyListeners.addAll(from.wireApplyListeners);
    }

    public final BlueprintPreset attemptMirrorY() {
        try {
            return this.mirrorY();
        } catch (PresetMirrorException e) {
            return this.copy();
        }
    }

    public BlueprintPreset mirrorY() throws PresetMirrorException {
        BlueprintPreset preset = this.newObject(this.width, this.height);
        preset.mirrorYData(this);
        return preset;
    }

    private void mirrorYData(BlueprintPreset from) throws PresetMirrorException {
        boolean[] computed = new boolean[this.width * this.height];

        for(int y = 0; y < this.height; ++y) {
            int mirrorY = this.getMirroredY(y);

            for(int x = 0; x < this.width; ++x) {
                setInt(this.tiles, this.width, x, mirrorY, getInt(from.tiles, this.width, x, y));
                int objectID = getInt(from.objects, this.width, x, y);
                if (objectID != -1) {
                    GameObject obj = ObjectRegistry.getObject(objectID);
                    if (obj != null) {
                        byte objectRotation = getByte(from.objectRotations, this.width, x, y);
                        MultiTile multiTile = obj.getMultiTile(objectRotation);
                        Point posOffset = multiTile.getMirrorYPosOffset();
                        if (posOffset == null) {
                            throw new PresetMirrorException(obj.getDisplayName() + " could not be mirrored");
                        }

                        setInt(this.objects, this.width, x + posOffset.x, mirrorY + posOffset.y, objectID);
                        setByte(this.objectRotations, this.width, x, mirrorY, (byte)multiTile.getYMirrorRotation());
                        setBoolean(computed, this.width, x, mirrorY, true);
                    } else {
                        setInt(this.objects, this.width, x, mirrorY, objectID);
                    }
                } else if (!getBoolean(computed, this.width, x, mirrorY)) {
                    setInt(this.objects, this.width, x, mirrorY, -1);
                }

                setByte(this.wires, this.width, x, mirrorY, getByte(from.wires, this.width, x, y));
            }
        }

        from.logicGates.forEach((p, lg) -> {
            this.logicGates.put(new Point(p.x, this.getMirroredY(p.y)), lg);
        });
        Iterator iterator = from.applyPredicates.iterator();

        while(iterator.hasNext()) {
            ApplyPredicate applyPredicate = (ApplyPredicate)iterator.next();
            this.applyPredicates.add(applyPredicate.mirrorY(this.height));
        }

        iterator = from.customPreApplies.iterator();

        CustomApply customApply;
        while(iterator.hasNext()) {
            customApply = (CustomApply)iterator.next();
            this.customPreApplies.add(customApply.mirrorY(this.height));
        }

        iterator = from.customApplies.iterator();

        while(iterator.hasNext()) {
            customApply = (CustomApply)iterator.next();
            this.customApplies.add(customApply.mirrorY(this.height));
        }

        this.tileApplyListeners.addAll(from.tileApplyListeners);
        this.objectApplyListeners.addAll(from.objectApplyListeners);
        this.wireApplyListeners.addAll(from.wireApplyListeners);
    }

    protected BlueprintPreset newObject(int width, int height) {
        return new BlueprintPreset(width, height, id, recipe, canPlaceOnShore, canPlaceOnLiquid);
    }


}

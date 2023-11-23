package settlementexpansion.object.entity;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;
import settlementexpansion.map.preset.BlueprintPreset;
import settlementexpansion.map.preset.BlueprintPresetID;

public class BlueprintObjectEntity extends ObjectEntity {
    private BlueprintPreset preset;

    public BlueprintObjectEntity(Level level, BlueprintPresetID presetId, int x, int y) {
        super(level, "blueprint", x, y);
        this.preset = new BlueprintPreset(presetId);
    }

    public BlueprintPreset getPreset() {
        return preset;
    }

    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addEnum("blueprintid", preset.id);
    }

    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.preset = new BlueprintPreset(save.getEnum(BlueprintPresetID.class, "blueprintid"));
    }
}

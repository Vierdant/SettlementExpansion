package settlementexpansion.map.settlement;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.LevelData;

public class SettlementModData extends LevelData {

    public boolean isPvpFlagged;

    public SettlementModData() {
        this.isPvpFlagged = false;
    }

    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addBoolean("settlementPvpFlagged", this.isPvpFlagged);
    }

    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.isPvpFlagged = save.getBoolean("settlementPvpFlagged");
    }


    public void togglePvpFlag() {
        this.isPvpFlagged = !this.isPvpFlagged;
    }

    public static void createSettlementModDataCreateIfNonExist(Level level) {
        if (!level.isServerLevel()) {
            throw new IllegalArgumentException("Level must be server level");
        } else {
            SettlementModData settlementData = getSettlementModData(level);
            if (settlementData == null) {
                settlementData = new SettlementModData();
                level.addLevelData("settlementExpanded", settlementData);
            }
        }
    }

    public static SettlementModData getSettlementModDataCreateIfNonExist(Level level) {
        if (!level.isServerLevel()) {
            throw new IllegalArgumentException("Level must be server level");
        } else {
            SettlementModData settlementData = getSettlementModData(level);
            if (settlementData == null) {
                settlementData = new SettlementModData();
                level.addLevelData("settlementExpanded", settlementData);
            }

            return settlementData;
        }
    }

    public static SettlementModData getSettlementModData(Level level) {
        LevelData data = level.getLevelData("settlementExpanded");
        return data instanceof SettlementModData ? (SettlementModData)data : null;
    }



}
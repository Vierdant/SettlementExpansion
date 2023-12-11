package settlementexpansion.map.settlement;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.LevelData;
import settlementexpansion.SettlementExpansion;

public class SettlementModData extends LevelData {

    public boolean isPvpFlagged;
    public SettlementModRoomsManager rooms;

    public SettlementModData() {
        this.isPvpFlagged = false;
        this.rooms = new SettlementModRoomsManager(this);
    }

    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addBoolean("settlementPvpFlagged", this.isPvpFlagged);
    }

    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.isPvpFlagged = save.getBoolean("settlementPvpFlagged");
        if (this.level.getWorldSettings().forcedPvP) {
            this.isPvpFlagged = SettlementExpansion.getSettings().enableSettlementLevelModification;
        }
    }


    public void togglePvpFlag() {
        if (!this.level.getWorldSettings().forcedPvP) {
            this.isPvpFlagged = !this.isPvpFlagged;
        } else {
            this.isPvpFlagged = this.level.getWorldSettings().forcedPvP;
        }
    }

    public void setPvpFlagged(boolean state) {
        if (!this.level.getWorldSettings().forcedPvP) {
            this.isPvpFlagged = state;
        } else {
            this.isPvpFlagged = this.level.getWorldSettings().forcedPvP;
        }
    }

    public static void createSettlementModDataCreateIfNonExist(Level level) {
        if (!level.isServer()) {
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
        if (!level.isServer()) {
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

    public void setLevel(Level level) {
        super.setLevel(level);
    }

}

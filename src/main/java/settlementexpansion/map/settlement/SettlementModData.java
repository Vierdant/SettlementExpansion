package settlementexpansion.map.settlement;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.LevelData;
import settlementexpansion.SettlementExpansion;

public class SettlementModData extends LevelData {

    public SettlementModRoomsManager rooms;

    public SettlementModData() {
        this.rooms = new SettlementModRoomsManager(this);
    }

    public void addSaveData(SaveData save) {
        super.addSaveData(save);
    }

    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
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

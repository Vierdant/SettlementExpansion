package settlementexpansion.entity.mob.friendly;

import necesse.engine.registries.IDData;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.friendly.human.HumanMob;
import settlementexpansion.util.FurnitureType;

import java.util.HashMap;

public class HumanMobData {

    public static HashMap<IDData, HumanMobData> storage = new HashMap<>();
    public FurnitureType preferredFurnitureType;
    public IDData idData;
    public HumanMob humanMob;

    public HumanMobData(HumanMob humanMob) {
        this.humanMob = humanMob;
        this.idData = humanMob.idData;

        setPreferredFurnitureSet();
    }

    public void setPreferredFurnitureSet() {
        this.preferredFurnitureType = FurnitureType.weightedSelection(humanMob.settlerStringID);
    }


    public SaveData getSaveData() {
        SaveData save = new SaveData("EXPANDED");
        save.addEnum("preferredFurnitureSet", this.preferredFurnitureType);
        return save;
    }

    public void applyLoadData(LoadData load) {
        for (LoadData data : load.getLoadDataByName("EXPANDED")) {
            FurnitureType furnitureType = data.getEnum(FurnitureType.class, "preferredFurnitureSet");
            if (furnitureType != null) {
                this.preferredFurnitureType = furnitureType;
            }
        }
    }

}

package settlementexpansion.entity.mob.friendly;

import necesse.engine.registries.IDData;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.friendly.human.HumanMob;
import settlementexpansion.map.settlement.FurnitureWoodType;

import java.util.HashMap;

public class HumanMobData {

    public static HashMap<IDData, HumanMobData> storage = new HashMap<>();
    public FurnitureWoodType preferredFurnitureWoodType;
    public IDData idData;
    public HumanMob humanMob;

    public HumanMobData(HumanMob humanMob) {
        this.humanMob = humanMob;
        this.idData = humanMob.idData;

        setPreferredFurnitureSet();
    }

    public void setPreferredFurnitureSet() {
        this.preferredFurnitureWoodType = FurnitureWoodType.weightedSelection(humanMob.settlerStringID == null ? humanMob.getStringID() : humanMob.settlerStringID);
    }


    public SaveData getSaveData() {
        SaveData save = new SaveData("EXPANDED");
        save.addEnum("preferredFurnitureSet", this.preferredFurnitureWoodType);
        return save;
    }

    public void applyLoadData(LoadData load) {
        for (LoadData data : load.getLoadDataByName("EXPANDED")) {
            FurnitureWoodType furnitureWoodType = data.getEnum(FurnitureWoodType.class, "preferredFurnitureSet");
            if (furnitureWoodType != null) {
                this.preferredFurnitureWoodType = furnitureWoodType;
            }
        }
    }

}

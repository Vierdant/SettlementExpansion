package settlementexpansion.mob.friendly;

import necesse.engine.registries.IDData;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.friendly.human.HumanMob;
import settlementexpansion.util.FurnitureSetEnum;

import java.util.HashMap;

public class HumanMobData {

    public static HashMap<IDData, HumanMobData> storage = new HashMap<>();
    public FurnitureSetEnum preferredFurnitureSet;
    public IDData idData;
    public HumanMob humanMob;

    public HumanMobData(HumanMob humanMob) {
        this.humanMob = humanMob;
        this.idData = humanMob.idData;

        if (!HumanMobData.storage.containsKey(this.idData)) {
            HumanMobData.storage.put(this.idData, this);
        }

        setPreferredFurnitureSet();
    }

    public void setPreferredFurnitureSet() {
        this.preferredFurnitureSet = FurnitureSetEnum.weightedSelection(humanMob.settlerStringID);
    }


    public SaveData getSaveData() {
        SaveData save = new SaveData("EXPANDED");
        save.addEnum("preferredFurnitureSet", this.preferredFurnitureSet);
        return save;
    }

    public static void applyLoadData(HumanMob humanMob, LoadData load) {
        HumanMobData humanMobData = HumanMobData.storage.put(humanMob.idData,
                new HumanMobData(humanMob));
        for (LoadData data : load.getLoadDataByName("EXPANDED")) {
            FurnitureSetEnum furnitureSetEnum = data.getEnum(FurnitureSetEnum.class, "preferredFurnitureSet");

            if (humanMobData != null) {
                if (furnitureSetEnum != null) {
                    humanMobData.preferredFurnitureSet = furnitureSetEnum;
                }
            }
        }
    }

}

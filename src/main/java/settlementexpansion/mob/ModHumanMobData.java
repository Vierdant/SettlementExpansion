package settlementexpansion.mob;

import necesse.engine.registries.IDData;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.friendly.human.HumanMob;
import settlementexpansion.util.FurnitureSetEnum;
import settlementexpansion.util.SharedData;

public class ModHumanMobData {

    public FurnitureSetEnum preferredFurnitureSet;
    public IDData idData;
    public HumanMob humanMob;

    public ModHumanMobData(HumanMob humanMob) {
        this.humanMob = humanMob;
        this.idData = humanMob.idData;

        if (!SharedData.humanMobDataStorage.containsKey(this.idData)) {
            SharedData.humanMobDataStorage.put(this.idData, this);
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
        ModHumanMobData humanMobData = SharedData.humanMobDataStorage.put(humanMob.idData,
                new ModHumanMobData(humanMob));
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

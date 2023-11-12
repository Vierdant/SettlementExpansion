package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.registries.LevelJobRegistry;
import necesse.level.maps.levelData.jobs.*;
import net.bytebuddy.asm.Advice;
import settlementexpansion.registry.JobModRegistry;

@ModMethodPatch(target = LevelJobRegistry.class, name = "registerCore", arguments = {})
public class LevelJobRegistryPatch {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean onEnter() { return true; }

    @Advice.OnMethodExit
    static void onExit() {
        LevelJobRegistry.consumeFoodID = LevelJobRegistry.registerJob("consumefood", ConsumeFoodLevelJob.class, "needs");
        LevelJobRegistry.equipArmorID = LevelJobRegistry.registerJob("manageequipment", ManageEquipmentLevelJob.class, "needs");
        LevelJobRegistry.hasStorageID = LevelJobRegistry.registerJob("hasstorage", HasStorageLevelJob.class, "needs", 100);
        LevelJobRegistry.haulFromID = LevelJobRegistry.registerJob("haulfrom", HaulFromLevelJob.class, "hauling");
        LevelJobRegistry.storePickupItemID = LevelJobRegistry.registerJob("storepickupitem", StorePickupItemLevelJob.class, "hauling", 100);
        LevelJobRegistry.useWorkstationID = LevelJobRegistry.registerJob("useworkstation", UseWorkstationLevelJob.class, "crafting", 0);
        LevelJobRegistry.forestryID = LevelJobRegistry.registerJob("forestry", ForestryLevelJob.class, "forestry");
        LevelJobRegistry.plantSaplingID = LevelJobRegistry.registerJob("plantsapling", PlantSaplingLevelJob.class, "forestry", 100);
        LevelJobRegistry.fertilizeID = LevelJobRegistry.registerJob("fertilize", FertilizeLevelJob.class, "fertilize");
        LevelJobRegistry.slaughterHusbandryMobID = LevelJobRegistry.registerJob("slaughterhusbandry", SlaughterHusbandryMobLevelJob.class, "husbandry");
        LevelJobRegistry.milkHusbandryMobID = LevelJobRegistry.registerJob("milkhusbandry", MilkHusbandryMobLevelJob.class, "husbandry");
        LevelJobRegistry.shearHusbandryMobID = LevelJobRegistry.registerJob("shearhusbandry", ShearHusbandryMobLevelJob.class, "husbandry");
        LevelJobRegistry.harvestApiaryID = LevelJobRegistry.registerJob("harvestapiary", HarvestApiaryLevelJob.class, "husbandry");
        LevelJobRegistry.fishingPositionID = LevelJobRegistry.registerJob("fishingposition", FishingPositionLevelJob.class, "fishing");
        LevelJobRegistry.harvestFruitID = LevelJobRegistry.registerJob("harvestfruit", HarvestFruitLevelJob.class, "farming");
        LevelJobRegistry.harvestCropID = LevelJobRegistry.registerJob("harvestcrop", HarvestCropLevelJob.class, "farming");
        LevelJobRegistry.plantCropID = LevelJobRegistry.registerJob("plantcrop", PlantCropLevelJob.class, "farming", 100);
        LevelJobRegistry.huntMobID = LevelJobRegistry.registerJob("huntmob", HuntMobLevelJob.class, "hunting");
        JobModRegistry.registerLevelJobs();
    }

}

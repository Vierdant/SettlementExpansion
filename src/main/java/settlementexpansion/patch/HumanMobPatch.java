package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.friendly.human.HumanMob;
import net.bytebuddy.asm.Advice;
import settlementexpansion.map.job.StudyBookLevelJob;
import settlementexpansion.entity.mob.friendly.HumanMobData;

public class HumanMobPatch {

    @ModConstructorPatch(target = HumanMob.class, arguments = {int.class, int.class, String.class})
    public static class HumanMobConstructPatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.This HumanMob humanMob) {
            setJobHandlers(humanMob);

            if (!humanMob.getStringID().equalsIgnoreCase("travelingmerchant")) {
                HumanMobData.storage.put(humanMob.idData,
                        new HumanMobData(humanMob));
            }
        }

        public static void setJobHandlers(HumanMob humanMob) {
            humanMob.jobTypeHandler.setJobHandler(StudyBookLevelJob.class, 3000, 10000, 0, 4000, () ->
                            (!humanMob.isSettler() || humanMob.isSettlerOnCurrentLevel()) && !humanMob.getWorkInventory().isFull(),
                    (foundJob) -> StudyBookLevelJob.getJobSequence(humanMob, foundJob));
        }
    }

    @ModMethodPatch(target = HumanMob.class, name = "addSaveData", arguments = {SaveData.class})
    public static class HumanMobSavePatch {
        @Advice.OnMethodEnter
        static void onEnter(@Advice.This HumanMob humanMob, @Advice.Argument(0) SaveData save) {
            HumanMobData humanMobData = HumanMobData.storage.get(humanMob.idData);
            if (humanMobData != null) {
                save.addSaveData(humanMobData.getSaveData());
            }
        }
    }

    @ModMethodPatch(target = HumanMob.class, name = "applyLoadData", arguments = {LoadData.class})
    public static class HumanMobLoadPatch {
        @Advice.OnMethodEnter
        static void onEnter(@Advice.This HumanMob humanMob, @Advice.Argument(0) LoadData save) {
            HumanMobData humanMobData = HumanMobData.storage.get(humanMob.idData);
            if (humanMobData != null) {
                humanMobData.applyLoadData(save);
            }
        }
    }

}

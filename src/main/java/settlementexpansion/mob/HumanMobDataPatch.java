package settlementexpansion.mob;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.friendly.human.HumanMob;
import net.bytebuddy.asm.Advice;
import settlementexpansion.util.InstanceData;

public class HumanMobDataPatch {

    @ModMethodPatch(target = HumanMob.class, name = "HumanMob", arguments = {})
    public static class HumanMobConstructPatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.This HumanMob humanMob) {
            InstanceData.humanMobDataStorage.put(humanMob.idData,
                    new HumanMobData(humanMob));
        }
    }

    @ModMethodPatch(target = HumanMob.class, name = "addSaveData", arguments = {SaveData.class})
    public static class HumanMobSavePatch {
        @Advice.OnMethodEnter
        static void onEnter(@Advice.This HumanMob humanMob, @Advice.Argument(0) SaveData save) {
            HumanMobData humanMobData = InstanceData.humanMobDataStorage.get(humanMob.idData);
            if (humanMobData != null) {
                save.addSaveData(humanMobData.getSaveData());
            }
        }
    }

    @ModMethodPatch(target = HumanMob.class, name = "applyLoadData", arguments = {LoadData.class})
    public static class HumanMobLoadPatch {
        @Advice.OnMethodEnter
        static void onEnter(@Advice.This HumanMob humanMob, @Advice.Argument(0) LoadData save) {
            HumanMobData.applyLoadData(humanMob, save);
        }
    }

}

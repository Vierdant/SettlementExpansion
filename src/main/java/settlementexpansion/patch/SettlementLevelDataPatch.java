package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import net.bytebuddy.asm.Advice;
import settlementexpansion.map.settlement.SettlementModData;

public class SettlementLevelDataPatch {

    @ModMethodPatch(target = SettlementLevelData.class, name = "getSettlementDataCreateIfNonExist", arguments = {Level.class})
    public static class SettlementLevelDataCreatePatch {

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        static boolean onEnter() {
            return true;
        }

        @Advice.OnMethodExit
        static SettlementLevelData onExit(@Advice.Argument(0) Level level, @Advice.Return(readOnly = false) SettlementLevelData out) {
            if (!level.isServerLevel()) {
                throw new IllegalArgumentException("Level must be server level");
            } else {
                SettlementModData.createSettlementModDataCreateIfNonExist(level);
                out = SettlementLevelData.getSettlementData(level);
                if (out == null) {
                    out = new SettlementLevelData();
                    level.addLevelData("settlement", out);
                }

                return out;
            }
        }
    }

//    @ModMethodPatch(target = SettlementLevelData.class, name = "addSaveData", arguments = {SaveData.class})
//    public static class SettlementLevelDataSavePatch {
//
//        @Advice.OnMethodExit
//        static void onExit(@Advice.Argument(0) SaveData save, @Advice.This SettlementLevelData data) {
//            SettlementModData mod = SettlementModData.getSettlementModDataCreateIfNonExist(data.getLevel());
//            mod.addSaveData(save);
//        }
//    }
//
//    @ModMethodPatch(target = SettlementLevelData.class, name = "applyLoadData", arguments = {LoadData.class})
//    public static class SettlementLevelDataLoadPatch {
//
//        @Advice.OnMethodExit
//        static void onExit(@Advice.Argument(0) LoadData save, @Advice.This SettlementLevelData data) {
//            SettlementModData mod = SettlementModData.getSettlementModDataCreateIfNonExist(data.getLevel());
//            mod.applyLoadData(save);
//        }
//    }
}

package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import net.bytebuddy.asm.Advice;
import settlementexpansion.map.settlement.SettlementModData;

public class SettlementLevelDataPatch {

    @ModMethodPatch(target = SettlementLevelData.class, name = "getSettlementDataCreateIfNonExist", arguments = {Level.class})
    public static class SettlementLevelDataCreatePatch {

        @Advice.OnMethodExit
        static void onExit(@Advice.Argument(0) Level level, @Advice.Return(readOnly = false) SettlementLevelData out) {
            if (out != null) {
                SettlementModData.createSettlementModDataCreateIfNonExist(level);
            }
        }
    }
}

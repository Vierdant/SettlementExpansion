package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModMethodPatch;
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
            if (!level.isServer()) {
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
}

package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import net.bytebuddy.asm.Advice;
import settlementexpansion.map.settlement.SettlementModData;

public class GameObjectPatch {
    @ModMethodPatch(target = GameObject.class, name = "doExplosionDamage", arguments = {Level.class,
            int.class, int.class, int.class, int.class, ServerClient.class})
    public static class DoExplosionDamagePatch {
        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        static boolean onExit(@Advice.AllArguments Object[] args) {
            Level level = (Level) args[0];
            int x = (int) args[1];
            int y = (int) args[2];
            SettlementModData data = SettlementModData.getSettlementModData(level);
            SettlementLevelData settlementData = SettlementLevelData.getSettlementData(level);
            return level.settlementLayer.isActive() && !data.doExplosionDamage && settlementData.getDefendZone().containsTile(x, y);
        }
    }
}

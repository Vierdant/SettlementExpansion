package settlementexpansion;

import necesse.engine.modLoader.ModSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class Settings extends ModSettings {

    public boolean enableHappinessModifier = true;
    public boolean enableSettlementLevelModification = true;
    public boolean enableMultiFlagPerSettlement = false;
    public boolean allowOwnedSettlerKillsNoPvP = false;
    public boolean requireSettlerOwnerOnlineToKill = false;
    public boolean enableHumansGetAngryOnBreakOrSteal = true;

    public Settings() {}

    @Override
    public void addSaveData(SaveData save) {
        save.addBoolean("enableHappinessModifier", enableHappinessModifier,
                "Toggle the Happiness Modifier module, which gives Settlers new needs that you need to satisfy | Default: true");
        save.addBoolean("enableSettlementLevelModification", enableSettlementLevelModification,
                "Toggle the Settlement Level module, which modifies how settlements work to be more multiplayer friendly | Default: true");
        save.addBoolean("enableMultiFlagPerSettlement", enableMultiFlagPerSettlement,
                "If true, allows the player to place multiple flags in a SINGLE settlement | Default: false");
        save.addBoolean("allowOwnedSettlerKillsNoPvP", allowOwnedSettlerKillsNoPvP,
                "If true, player will be able to kill owned settlers even while SETTLEMENT pvp is disabled | Default: false");
        save.addBoolean("requireSettlerOwnerOnlineToKill", requireSettlerOwnerOnlineToKill,
                "If true, a player can't kill a settler owned by another player, unless the owner player is online | Default: false");
        save.addBoolean("enableHumansGetAngryOnBreakOrSteal", enableHumansGetAngryOnBreakOrSteal,
                "If true, when a player breaks an object or interacts with a chest, with settlers or villagers around, they will get hostile | Default: true");
    }

    @Override
    public void applyLoadData(LoadData load) {
        enableHappinessModifier = load.getBoolean("enableHappinessModifier", true);
        enableSettlementLevelModification = load.getBoolean("enableSettlementLevelModification", true);
        enableMultiFlagPerSettlement = load.getBoolean("enableMultiFlagPerSettlement", false);
        allowOwnedSettlerKillsNoPvP = load.getBoolean("allowOwnedSettlerKillsNoPvP", false);
        requireSettlerOwnerOnlineToKill = load.getBoolean("requireSettlerOwnerOnlineToKill", false);
        enableHumansGetAngryOnBreakOrSteal = load.getBoolean("enableHumansGetAngryOnBreakOrSteal", true);
    }
}

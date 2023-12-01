package settlementexpansion;

import necesse.engine.modLoader.ModSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class Settings extends ModSettings {

    public boolean enableHappinessModifier = true;
    public boolean enableSettlementLevelModification = true;
    public boolean enableMultiFlagPerSettlement = false;

    public Settings() {}

    @Override
    public void addSaveData(SaveData save) {
        save.addBoolean("enableHappinessModifier", enableHappinessModifier,
                "Toggle the Happiness Modifier module, which gives Settlers new needs that you need to satisfy | Default: true");
        save.addBoolean("enableSettlementLevelModification", enableSettlementLevelModification,
                "Toggle the Settlement Level module, which modifies how settlements work to be more multiplayer friendly | Default: true");
        save.addBoolean("enableMultiFlagPerSettlement", enableMultiFlagPerSettlement,
                "If true, allows the player to place multiple flags in a SINGLE settlement | Default: false");
    }

    @Override
    public void applyLoadData(LoadData load) {
        enableHappinessModifier = load.getBoolean("enableHappinessModifier", true);
        enableMultiFlagPerSettlement = load.getBoolean("enableMultiFlagPerSettlement", false);
    }
}

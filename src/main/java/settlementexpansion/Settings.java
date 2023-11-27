package settlementexpansion;

import necesse.engine.modLoader.ModSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class Settings extends ModSettings {

    public boolean enableHappinessModifier = true;

    public Settings() {}

    @Override
    public void addSaveData(SaveData save) {
        save.addBoolean("enableHappinessModifier", enableHappinessModifier,
                "Toggle the Happiness Modifier module, which gives Settlers new needs that you need to satisfy");
    }

    @Override
    public void applyLoadData(LoadData load) {
        enableHappinessModifier = load.getBoolean("enableHappinessModifier", true);
    }
}

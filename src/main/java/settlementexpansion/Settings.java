package settlementexpansion;

import necesse.engine.modLoader.ModSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class Settings extends ModSettings {

    public boolean enableHappinessModifier;
    public boolean enableHumansGetAngryOnBreakOrSteal;

    public Settings() {
        this.enableHappinessModifier = true;
        this.enableHumansGetAngryOnBreakOrSteal = false;
    }

    @Override
    public void addSaveData(SaveData save) {
        save.addBoolean("enableHappinessModifier", this.enableHappinessModifier,
                "Toggle the Happiness Modifier module, which gives Settlers new needs that you need to satisfy | Default: true");
        save.addBoolean("enableHumansGetAngryOnBreakOrSteal", this.enableHumansGetAngryOnBreakOrSteal,
                "If true, when a player breaks an object or interacts with a chest, with settlers or villagers around, they will get hostile | Default: false");
    }

    @Override
    public void applyLoadData(LoadData load) {
        this.enableHappinessModifier = load.getBoolean("enableHappinessModifier", true);
        this.enableHumansGetAngryOnBreakOrSteal = load.getBoolean("enableHumansGetAngryOnBreakOrSteal", false);
    }
}

package settlementexpansion;

import necesse.engine.modLoader.ModSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

import java.util.ArrayList;
import java.util.List;

public class Settings extends ModSettings {

    public boolean enableHappinessModifier;
    public boolean enableSettlementLevelModification;
    public boolean enableMultiFlagPerSettlement;
    public boolean allowOwnedSettlerKillsNoPvP;
    public boolean requireSettlerOwnerOnlineToKill;
    public boolean enableHumansGetAngryOnBreakOrSteal;
    public boolean enableCustomBlueprints;
    public List<String> customBlueprintIDSettings;
    public List<String> customBlueprintIDPresets;

    public Settings() {
        this.enableHappinessModifier = true;
        this.enableSettlementLevelModification = true;
        this.enableMultiFlagPerSettlement = false;
        this.allowOwnedSettlerKillsNoPvP = false;
        this.requireSettlerOwnerOnlineToKill = false;
        this.enableHumansGetAngryOnBreakOrSteal = true;
        this.enableCustomBlueprints = false;
        this.customBlueprintIDSettings = new ArrayList<>();
        this.customBlueprintIDPresets = new ArrayList<>();
    }

    @Override
    public void addSaveData(SaveData save) {
        save.addBoolean("enableHappinessModifier", this.enableHappinessModifier,
                "Toggle the Happiness Modifier module, which gives Settlers new needs that you need to satisfy | Default: true");
        save.addBoolean("enableSettlementLevelModification", this.enableSettlementLevelModification,
                "Toggle the Settlement Level module, which modifies how settlements work to be more multiplayer friendly | Default: true");
        save.addBoolean("enableMultiFlagPerSettlement", this.enableMultiFlagPerSettlement,
                "If true, allows the player to place multiple flags in a SINGLE settlement | Default: false");
        save.addBoolean("allowOwnedSettlerKillsNoPvP", this.allowOwnedSettlerKillsNoPvP,
                "If true, player will be able to kill owned settlers even while SETTLEMENT pvp is disabled | Default: false");
        save.addBoolean("requireSettlerOwnerOnlineToKill", this.requireSettlerOwnerOnlineToKill,
                "If true, a player can't kill a settler owned by another player, unless the owner player is online | Default: false");
        save.addBoolean("enableHumansGetAngryOnBreakOrSteal", this.enableHumansGetAngryOnBreakOrSteal,
                "If true, when a player breaks an object or interacts with a chest, with settlers or villagers around, they will get hostile | Default: true");
        save.addBoolean("enableCustomBlueprints", enableCustomBlueprints,
                "Enable this to be able to use the custom blueprints related settings. If it's false, the mod will not attempt registering the custom blueprints. | Default: false");
        save.addStringList("customBlueprintIDSettings", customBlueprintIDSettings,
                "The settings of the custom blueprint presets you provided in customBlueprintIDPresets. Add the names in the same order you added the presets, so first name is for first preset, etc.");
        save.addStringList("customBlueprintIDPresets", customBlueprintIDPresets,
                "Custom blueprint presets. Look up how to save presets using dev tools in Necesse, or ask in the discord server for instructions.");
    }

    @Override
    public void applyLoadData(LoadData load) {
        this.enableHappinessModifier = load.getBoolean("enableHappinessModifier", true);
        this.enableSettlementLevelModification = load.getBoolean("enableSettlementLevelModification", true);
        this.enableMultiFlagPerSettlement = load.getBoolean("enableMultiFlagPerSettlement", false);
        this.allowOwnedSettlerKillsNoPvP = load.getBoolean("allowOwnedSettlerKillsNoPvP", false);
        this.requireSettlerOwnerOnlineToKill = load.getBoolean("requireSettlerOwnerOnlineToKill", false);
        this.enableHumansGetAngryOnBreakOrSteal = load.getBoolean("enableHumansGetAngryOnBreakOrSteal", true);
        this.enableCustomBlueprints = load.getBoolean("enableCustomBlueprints", false);
        this.customBlueprintIDSettings = load.getStringList("customBlueprintIDSettings", new ArrayList<>());
        this.customBlueprintIDPresets = load.getStringList("customBlueprintIDPresets", new ArrayList<>());
    }
}

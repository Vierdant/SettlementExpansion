package settlementexpansion.map.preset;

import necesse.engine.GameLog;

import java.util.UUID;

public class CustomBlueprintSettings {

    public String blueprintName;
    public String furnitureType;
    public boolean canChangeWalls;
    public boolean canChangeFloor;
    public boolean canPlaceOnShore;
    public boolean canPlaceOnLiquid;

    public CustomBlueprintSettings(String settings) {
        String[] entries = settings.split(", ");
        boolean canApplyCustom = true;
        if (entries.length != 6) {
            GameLog.warn.print("Error processing a custom blueprint. Expected settings count did not much the settings provided.");
            canApplyCustom = false;
        }

        if (canApplyCustom) {
            this.blueprintName = entries[0];
            this.furnitureType = entries[1].equalsIgnoreCase("null") ? null : entries[1];
            this.canChangeWalls = Boolean.parseBoolean(entries[2]);
            this.canChangeFloor = Boolean.parseBoolean(entries[3]);
            this.canPlaceOnShore = Boolean.parseBoolean(entries[4]);
            this.canPlaceOnLiquid = Boolean.parseBoolean(entries[5]);
        } else {
            this.blueprintName = UUID.randomUUID().toString();
            this.furnitureType = null;
            this.canChangeWalls = false;
            this.canChangeFloor = false;
            this.canPlaceOnShore = false;
            this.canPlaceOnLiquid = false;
        }
    }
}

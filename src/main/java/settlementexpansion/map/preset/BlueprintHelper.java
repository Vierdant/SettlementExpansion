package settlementexpansion.map.preset;

import settlementexpansion.registry.ObjectModRegistry;

public class BlueprintHelper {

    public static String[] wallTypes = new String[]{"woodwall", "pinewall", "palmwall", "stonewall", "sandstonewall", "swampstonewall", "snowstonewall", "icewall", "dungeonwall", "deepstonewall", "obsidianwall", "deepsnowstonewall", "deepswampstonewall", "deepsandstonewall"};

    public static boolean isObjectWoodFurniture(String id) {
        for (String type : ObjectModRegistry.woodFurnitureTypes) {
            if (id.contains(type)) {
                return true;
            }
        }
        return false;
    }

    public static String getTrueFurnitureType(String id) {
        for (String type : ObjectModRegistry.woodFurnitureTypes) {
            if (id.contains(type)) {
                return id.split(type)[1];
            }
        }

        return id;
    }

}

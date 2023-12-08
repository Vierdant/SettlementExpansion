package settlementexpansion.map.settlement;

import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.RoomFurniture;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import necesse.level.maps.regionSystem.SemiRegion;
import settlementexpansion.util.FurnitureWoodType;

import java.awt.*;
import java.util.*;

public class SettlementRoomData {

    public static HashMap<Point, SettlementRoomData> storage = new HashMap<>();
    private boolean calculatedStats;

    public final int tileX;
    public final int tileY;
    private final SettlementRoom room;
    private FurnitureWoodType furnitureMajority;
    private HashMap<String, Integer> objectTypes = new HashMap<>();

    public SettlementRoomData(SettlementRoom room, Point point) {
        this.tileX = point.x;
        this.tileY = point.y;
        this.room = room;

        if (!SettlementRoomData.storage.containsKey(point)) {
            SettlementRoomData.storage.put(point, this);
        }
    }

    public void calculateStats() {

        try {
            this.objectTypes.clear();

            HashSet<SemiRegion> semiRegions = (HashSet<SemiRegion>) room.getSemiRegions();

            for (SemiRegion roomSr : semiRegions) {
                HashMap<String, Integer> majorityMap = new HashMap<>();

                for (Point point : roomSr.getLevelTiles()) {
                    GameObject object = room.getLevel().getObject(point.x, point.y);
                    String id = object.getStringID();
                    this.addObjectType(id, 1);

                    // determine furniture majority type
                    if (object instanceof RoomFurniture) {
                        for (FurnitureWoodType set : FurnitureWoodType.values()) {
                            if (id.contains(set.getString())) {
                                int addition = 0;
                                if (majorityMap.containsKey(set.getString())) {
                                    addition = majorityMap.get(set.getString());
                                }
                                majorityMap.put(set.getString(), addition + 1);
                            }
                        }
                    }
                }

                if (!majorityMap.isEmpty()) {
                    int maxValue = (Collections.max(majorityMap.values()));
                    for (Map.Entry<String, Integer> entry : majorityMap.entrySet()) {
                        if (entry.getValue() == maxValue) {
                            this.furnitureMajority = getFurnitureSet(entry.getKey());
                        }
                    }
                }
            }
        }
        finally {
            this.calculatedStats = true;
        }


    }

    private FurnitureWoodType getFurnitureSet(String id) {
        for (FurnitureWoodType set : FurnitureWoodType.values()) {
            if (set.getString().equalsIgnoreCase(id)) {
                return set;
            }
        }

        return FurnitureWoodType.OAK;
    }

    public FurnitureWoodType getFurnitureMajority() {
        if (!this.calculatedStats) {
            this.calculateStats();
        }

        return this.furnitureMajority;
    }

    public void addObjectType(String type, int amount) {
        this.objectTypes.compute(type, (key, i) -> i == null ? amount : i + amount);
    }

    public int getObjectType(String type) {
        if (!this.calculatedStats) {
            this.calculateStats();
        }

        return this.objectTypes.getOrDefault(type, 0);
    }

    public void recalculateStats() {
        this.calculatedStats = false;
    }

}

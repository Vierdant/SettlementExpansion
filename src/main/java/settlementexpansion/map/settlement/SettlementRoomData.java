package settlementexpansion.map.settlement;

import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.RoomFurniture;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import necesse.level.maps.regionSystem.SemiRegion;
import settlementexpansion.util.FurnitureSetEnum;

import java.awt.*;
import java.util.*;

public class SettlementRoomData {

    public static HashMap<Point, SettlementRoomData> storage = new HashMap<>();
    private boolean calculatedStats;

    public final int tileX;
    public final int tileY;
    public final SettlementRoom room;
    public FurnitureSetEnum furnitureMajority;

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
            HashSet<SemiRegion> semiRegions = (HashSet<SemiRegion>) room.getSemiRegions();

            for (SemiRegion roomSr : semiRegions) {
                HashMap<String, Integer> majorityMap = new HashMap<>();
                for (Point point : roomSr.getLevelTiles()) {
                    GameObject object = room.getLevel().getObject(point.x, point.y);

                    // determine furniture majority type
                    if (object instanceof RoomFurniture) {
                        String id = object.getStringID();
                        for (FurnitureSetEnum set : FurnitureSetEnum.values()) {
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

    private FurnitureSetEnum getFurnitureSet(String id) {
        for (FurnitureSetEnum set : FurnitureSetEnum.values()) {
            if (set.getString().equalsIgnoreCase(id)) {
                return set;
            }
        }

        return FurnitureSetEnum.OAK;
    }

    public FurnitureSetEnum getFurnitureMajority() {
        if (!this.calculatedStats) {
            this.calculateStats();
        }

        return this.furnitureMajority;
    }

    public void recalculateStats() {
        this.calculatedStats = false;
    }

}

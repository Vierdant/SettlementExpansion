package settlementexpansion.map.settlement;

import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.RoomFurniture;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SemiRegion;
import settlementexpansion.util.FurnitureWoodType;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SettlementRoomData {
    public final SettlementModData data;
    private final HashMap<Point, SettlementRoomData> roomsMap;
    private boolean calculatedStats;
    private boolean invalidated;

    public final int tileX;
    public final int tileY;
    private FurnitureWoodType furnitureMajority;
    private final HashMap<String, Integer> objectTypes = new HashMap<>();

    public SettlementRoomData(SettlementModData data, HashMap<Point, SettlementRoomData> roomsMap, Point point) {
        this.tileX = point.x;
        this.tileY = point.y;
        this.data = data;
        this.roomsMap = roomsMap;
    }

    public void calculateStats() {

        try {
            if (this.calculatedStats) {
                return;
            }

            this.objectTypes.clear();

            HashSet<SemiRegion> semiRegions = (HashSet<SemiRegion>) this.getSemiRegions();

            for (SemiRegion roomSr : semiRegions) {
                HashMap<String, Integer> majorityMap = new HashMap<>();

                for (Point point : roomSr.getLevelTiles()) {
                    GameObject object = this.getLevel().getObject(point.x, point.y);
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

                    if (this.roomsMap != null) {
                        this.roomsMap.put(point, this);
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

    public void invalidate() {
        this.invalidated = true;
    }

    public boolean isInvalidated() {
        return this.invalidated;
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

    public Level getLevel() {
        return this.data.getLevel();
    }

    public Collection<SemiRegion> getSemiRegions() {
        if (this.getLevel().isOutside(this.tileX, this.tileY)) {
            return new HashSet<>();
        } else {
            SemiRegion semiRegion = this.data.getLevel().regionManager.getSemiRegion(this.tileX, this.tileY);
            return semiRegion == null ? new HashSet<>() : semiRegion.getAllConnected((sr) ->
                    sr.getType().roomInt == SemiRegion.RegionType.OPEN.roomInt);
        }
    }

    public List<String> getDebugTooltips() {
        if (!this.calculatedStats) {
            this.calculateStats();
        }

        LinkedList<String> list = new LinkedList<>();
        FurnitureWoodType type = this.getFurnitureMajority();
        if (type != null) {
            list.add("Furniture Wood: " + type.string);
        }

        return list;
    }
}

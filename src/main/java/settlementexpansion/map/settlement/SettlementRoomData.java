package settlementexpansion.map.settlement;

import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.RoomFurniture;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SemiRegion;

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
    private final HashMap<String, Integer> furnitureTypes = new HashMap<>();
    private SettlementRoomType type;
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

                    if (object instanceof RoomFurniture) {
                        String type = ((RoomFurniture)object).getFurnitureType();
                        if (type != null) {
                            this.addFurnitureType(type, 1);
                        }

                        // determine furniture majority type
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
            determineRoomType();
        }
    }

    public void determineRoomType() {
        List<SettlementRoomType> matches = new ArrayList<>();

        // determine room type
        for (SettlementRoomType type : SettlementRoomType.values()) {
            boolean match = true;
            for (RoomTypeCondition condition : type.getConditions()) {
                int amount = condition.isFurnitureType() ? getFurnitureTypes(condition.getId()) : getObjectType(condition.getId());
                if (!condition.compare(amount)) {
                    match = false;
                    break;
                }
            }

            if (match) {
                matches.add(type);
            }
        }

        matches.sort(Comparator.comparingInt(SettlementRoomType::getPriority));
        this.type = matches.get(matches.size() - 1);
        data.rooms.roomTypes.put(new Point(tileX, tileY), this.type);
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

    public int getFurnitureTypes(String type) {
        if (!this.calculatedStats) {
            this.calculateStats();
        }

        return this.furnitureTypes.getOrDefault(type, 0);
    }

    public SettlementRoomType getRoomType() {
        if (!this.calculatedStats) {
            this.calculateStats();
        }

        if (this.type != null) {
            this.determineRoomType();
        }

        return type;
    }

    public void addObjectType(String type, int amount) {
        this.objectTypes.compute(type, (key, i) -> i == null ? amount : i + amount);
    }

    private void addFurnitureType(String type, int amount) {
        this.furnitureTypes.compute(type, (key, i) -> {
            return i == null ? amount : i + amount;
        });
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
        FurnitureWoodType woodType = this.getFurnitureMajority();
        SettlementRoomType roomType = this.getRoomType();
        if (woodType != null) {
            list.add("Furniture Wood: " + woodType.string);
        }

        if (roomType != null) {
            list.add("Room Type: " + roomType.name());
        }

        return list;
    }
}

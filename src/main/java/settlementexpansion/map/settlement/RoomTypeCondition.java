package settlementexpansion.map.settlement;

public class RoomTypeCondition {

    private final String id;
    private final int value;
    private final boolean isFurnitureType;

    public RoomTypeCondition(String id, int value, boolean isFurnitureType) {
        this.id = id;
        this.value = value;
        this.isFurnitureType = isFurnitureType;
    }

    public String getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public boolean compare(int value) {
        return this.value <= value;
    }

    public boolean isFurnitureType() {
        return isFurnitureType;
    }
}

package settlementexpansion.map.settlement;

import java.awt.*;
import java.util.HashMap;

public class SettlementRoomTypeManager {

    private final HashMap<Point, SettlementRoomType> types;

    public SettlementRoomTypeManager() {
        this.types = new HashMap<>();
    }

    public boolean has(SettlementRoomType type) {
        return types.containsValue(type);
    }

    public void put(Point point, SettlementRoomType type) {
        types.put(point, type);
    }

    public void remove(Point point) {
        if (!types.containsKey(point)) {
            return;
        }

        types.remove(point);
    }

}

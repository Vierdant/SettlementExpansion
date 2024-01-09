package settlementexpansion.map.settlement;

import java.awt.*;
import java.util.HashMap;

public class SettlementModRoomsManager {

    public final SettlementModData data;
    public HashMap<Point, SettlementRoomData> rooms = new HashMap<>();
    private final HashMap<Point, SettlementRoomType> roomTypes;

    public SettlementModRoomsManager(SettlementModData data) {
        this.data = data;
        this.roomTypes = new HashMap<>();
    }

    public boolean hasRoomType(SettlementRoomType type) {
        return this.roomTypes.containsValue(type);
    }

    public void putRoomType(Point point, SettlementRoomType type) {
        this.roomTypes.put(point, type);
    }

    public void removeRoomType(Point point) {
        if (!this.roomTypes.containsKey(point)) {
            return;
        }

        this.roomTypes.remove(point);
    }

    public void refreshRooms(Iterable<Point> tiles) {
        for (Point tile : tiles) {
            removeRoomType(tile);
            SettlementRoomData room = this.rooms.remove(tile);
            if (room != null) {
                room.invalidate();
            }
        }
    }

    public void recalculateStats(Iterable<Point> tiles) {
        for (Point tile : tiles) {
            SettlementRoomData room = this.rooms.get(tile);
            if (room != null) {
                room.recalculateStats();
            }
        }
    }

    public void findAndCalculateRoom(int tileX, int tileY) {
        SettlementRoomData room = this.getRoom(tileX, tileY);
        if (room != null) {
            room.calculateStats();
        }
    }

    public SettlementRoomData getRoom(int tileX, int tileY) {
        Point point = new Point(tileX, tileY);
        return this.data.getLevel().isOutside(tileX, tileY) ? null : this.rooms.compute(point, (k, v) ->
                v != null && !v.isInvalidated() ? v : new SettlementRoomData(this.data, this.rooms, point));
    }
}

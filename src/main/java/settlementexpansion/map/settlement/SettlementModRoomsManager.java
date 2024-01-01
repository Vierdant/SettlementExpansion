package settlementexpansion.map.settlement;

import java.awt.*;
import java.util.HashMap;

public class SettlementModRoomsManager {

    public final SettlementModData data;
    public HashMap<Point, SettlementRoomData> rooms = new HashMap<>();
    public SettlementRoomTypeManager roomTypes;

    public SettlementModRoomsManager(SettlementModData data) {
        this.data = data;
        this.roomTypes = new SettlementRoomTypeManager();
    }

    public void refreshRooms(Iterable<Point> tiles) {
        for (Point tile : tiles) {
            roomTypes.remove(tile);
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

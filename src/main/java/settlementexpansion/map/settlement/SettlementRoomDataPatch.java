package settlementexpansion.map.settlement;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import necesse.level.maps.levelData.settlementData.SettlementRoomsManager;
import net.bytebuddy.asm.Advice;

import java.awt.*;
import java.util.Iterator;

public class SettlementRoomDataPatch {

    @ModMethodPatch(target = SettlementRoomsManager.class, name = "getRoom", arguments = {int.class, int.class})
    public static class SettlementRoomsManagerGetRoomPatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.Argument(0) int tileX, @Advice.Argument(1) int tileY, @Advice.Return(readOnly = false) SettlementRoom room) {
            if (room != null && !room.isInvalidated()) {
                Point point = new Point(tileX, tileY);
                if (!SettlementRoomData.storage.containsKey(point)) {
                    new SettlementRoomData(room, point);
                }
            }
        }
    }

    @ModMethodPatch(target = SettlementRoomsManager.class, name = "refreshRooms", arguments = {Iterable.class})
    public static class SettlementRoomsManagerRefreshPatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.Argument(0) Iterable<Point> tiles) {
            for (Point tile : tiles) {
                SettlementRoomData.storage.remove(tile);
            }
        }
    }

    @ModMethodPatch(target = SettlementRoomsManager.class, name = "recalculateStats", arguments = {Iterable.class})
    public static class SettlementRoomsManagerRecalculatePatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.Argument(0) Iterable<Point> tiles) {
            for (Point tile : tiles) {
                SettlementRoomData room = SettlementRoomData.storage.get(tile);
                if (room != null) {
                    room.recalculateStats();
                }
            }
        }
    }
    @ModMethodPatch(target = SettlementRoomsManager.class, name = "findAndCalculateRoom", arguments = {int.class, int.class})
    public static class SettlementRoomsManagerFindRecalculatePatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.Argument(0) int tileX, @Advice.Argument(1) int tileY, @Advice.This SettlementRoomsManager settlementRoomsManager) {
            SettlementRoomData room = SettlementRoomData.storage.get(new Point(tileX, tileY));

            if (room != null) {
                room.recalculateStats();
            }
        }
    }
}

package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import necesse.level.maps.levelData.settlementData.SettlementRoomsManager;
import net.bytebuddy.asm.Advice;
import settlementexpansion.map.settlement.SettlementRoomData;

import java.awt.*;

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

    @ModMethodPatch(target = SettlementRoom.class, name = "getRoomSize", arguments = {})
    public static class SettlementRoomRecalculatePatch {
        @Advice.OnMethodExit
        static void onEnter(@Advice.This SettlementRoom room) {
            SettlementRoomData roomEx = SettlementRoomData.storage.get(new Point(room.tileX, room.tileY));

            if (roomEx != null) {
                roomEx.recalculateStats();
            }
        }
    }
}

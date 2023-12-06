package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import necesse.level.maps.levelData.settlementData.SettlementRoomsManager;
import net.bytebuddy.asm.Advice;
import settlementexpansion.map.settlement.SettlementRoomData;
import settlementexpansion.util.FurnitureType;

import java.awt.*;
import java.util.List;

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
        static void onExit(@Advice.This SettlementRoom room) {
            Point point = new Point(room.tileX, room.tileY);
            SettlementRoomData roomEx = SettlementRoomData.storage.get(point);

            if (roomEx != null) {
                roomEx.recalculateStats();
            }
        }
    }

    @ModMethodPatch(target = SettlementRoom.class, name = "getDebugTooltips", arguments = {})
    public static class SettlementRoomDebugToolPatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.This SettlementRoom room, @Advice.Return(readOnly = false)List<String> list) {
            Point point = new Point(room.tileX, room.tileY);
            SettlementRoomData roomEx = SettlementRoomData.storage.get(point);

            if (roomEx != null) {
                FurnitureType type = roomEx.getFurnitureMajority();
                if (type != null) {
                    list.add("Furniture Wood: " + type.string);
                }
            }
        }
    }
}

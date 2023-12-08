package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import necesse.level.maps.levelData.settlementData.SettlementRoomsManager;
import net.bytebuddy.asm.Advice;
import settlementexpansion.map.settlement.SettlementModData;
import settlementexpansion.map.settlement.SettlementRoomData;
import settlementexpansion.util.FurnitureWoodType;

import java.awt.*;
import java.util.List;

public class SettlementRoomDataPatch {

    @ModMethodPatch(target = SettlementRoomsManager.class, name = "getRoom", arguments = {int.class, int.class})
    public static class SettlementRoomsManagerGetRoomPatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.This SettlementRoomsManager manager, @Advice.Argument(0) int tileX, @Advice.Argument(1) int tileY, @Advice.Return(readOnly = false) SettlementRoom room) {
            SettlementModData data = SettlementModData.getSettlementModDataCreateIfNonExist(manager.data.getLevel());
            data.rooms.getRoom(tileX, tileY);
        }
    }

    @ModMethodPatch(target = SettlementRoomsManager.class, name = "refreshRooms", arguments = {Iterable.class})
    public static class SettlementRoomsManagerRefreshPatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.This SettlementRoomsManager manager, @Advice.Argument(0) Iterable<Point> tiles) {
            SettlementModData data = SettlementModData.getSettlementModDataCreateIfNonExist(manager.data.getLevel());
            data.rooms.refreshRooms(tiles);
        }
    }

    @ModMethodPatch(target = SettlementRoomsManager.class, name = "recalculateStats", arguments = {Iterable.class})
    public static class SettlementRoomsManagerRecalculatePatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.This SettlementRoomsManager manager, @Advice.Argument(0) Iterable<Point> tiles) {
            SettlementModData data = SettlementModData.getSettlementModDataCreateIfNonExist(manager.data.getLevel());
            data.rooms.recalculateStats(tiles);
        }
    }

    @ModMethodPatch(target = SettlementRoomsManager.class, name = "findAndCalculateRoom", arguments = {int.class, int.class})
    public static class SettlementRoomsManagerFindAndCalculatePatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.This SettlementRoomsManager manager, @Advice.Argument(0) int tileX, @Advice.Argument(1) int tileY) {
            SettlementModData data = SettlementModData.getSettlementModDataCreateIfNonExist(manager.data.getLevel());
            data.rooms.findAndCalculateRoom(tileX, tileY);
        }
    }

    @ModMethodPatch(target = SettlementRoom.class, name = "getDebugTooltips", arguments = {})
    public static class SettlementRoomDebugToolPatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.This SettlementRoom room, @Advice.Return(readOnly = false)List<String> list) {
            SettlementModData data = new SettlementModData();
            data.setLevel(room.data.getLevel());
            SettlementRoomData roomEx = new SettlementRoomData(data, null, new Point(room.tileX, room.tileY));
            list.addAll(roomEx.getDebugTooltips());
        }
    }
}

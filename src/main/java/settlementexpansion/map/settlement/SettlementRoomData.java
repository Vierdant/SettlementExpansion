package settlementexpansion.map.settlement;

import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.RoomFurniture;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import necesse.level.maps.levelData.settlementData.SettlementRoomsManager;
import necesse.level.maps.regionSystem.SemiRegion;
import settlementexpansion.mob.friendly.HumanMobData;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class SettlementRoomData {

    public static HashMap<Point, SettlementRoomData> storage = new HashMap<>();

    public final int tileX;
    public final int tileY;
    public final SettlementRoom room;

    SettlementRoomData(SettlementRoom room, Point point) {
        this.tileX = point.x;
        this.tileY = point.y;
        this.room = room;

        if (!SettlementRoomData.storage.containsKey(point)) {
            SettlementRoomData.storage.put(point, this);
        }
    }

    public void calculateStats() {
        HashSet<SemiRegion> semiRegions = (HashSet<SemiRegion>) room.getSemiRegions();

        for (SemiRegion roomSr : semiRegions) {
            for (Point point : roomSr.getLevelTiles()) {
                GameObject object = room.getLevel().getObject(point.x, point.y);

                if (object instanceof RoomFurniture) {
                    String id = object.getStringID();

                }
            }
        }
    }


}

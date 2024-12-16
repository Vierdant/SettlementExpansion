package settlementexpansion.object;

import necesse.entity.mobs.PlayerMob;
import necesse.level.gameObject.DoorObject;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SemiRegion;

import java.awt.*;

public class SecureDoorObject extends DoorObject {

    public SecureDoorObject(Rectangle collision, int counterID, boolean isOpen) {
        super(collision, counterID, isOpen);
        this.regionType = SemiRegion.RegionType.WALL;
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return false;
    }


    @Override
    public boolean pathCollidesIfOpen(Level level, int tileX, int tileY, CollisionFilter collisionFilter, Rectangle mobCollision) {
        return true;
    }
}

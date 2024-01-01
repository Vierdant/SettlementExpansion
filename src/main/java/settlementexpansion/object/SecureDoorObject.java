package settlementexpansion.object;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.entity.TileDamageType;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.gameObject.DoorObject;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.FloatTextFade;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
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
    public void onPathOpened(Level level, int tileX, int tileY) {
        level.entityManager.doDamage(tileX, tileY, this.objectHealth, TileDamageType.Object, this.toolTier, null, true, tileX * 32 + 16, tileY * 32 + 16);
    }

    @Override
    public void onPathBreakDown(Level level, int tileX, int tileY, int damage, int hitX, int hitY) {
        level.entityManager.doDamage(tileX, tileY, this.objectHealth, TileDamageType.Object, this.toolTier, null, true, hitX, hitY);
    }

    @Override
    public boolean pathCollidesIfOpen(Level level, int tileX, int tileY, CollisionFilter collisionFilter, Rectangle mobCollision) {
        return true;
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", this.isSwitched ? "closetip" : "opentip");
    }
}

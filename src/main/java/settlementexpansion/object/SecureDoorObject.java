package settlementexpansion.object;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.gameObject.DoorObject;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.FloatTextFade;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

import java.awt.*;

public class SecureDoorObject extends DoorObject {

    public SecureDoorObject(Rectangle collision, int counterID, boolean isOpen) {
        super(collision, counterID, isOpen);
    }

    public boolean hasSettlementAccess(Level level, ServerClient client) {
        SettlementLevelData data = SettlementLevelData.getSettlementData(level);
        if (data != null) {
            return level.settlementLayer.doesClientHaveAccess(client);
        } else {
            return true;
        }
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        System.out.println("attempted");
        if (level.isServer()) {
            ServerClient client = player.getServerClient();
            if (hasSettlementAccess(level, client)) {
                super.interact(level, x, y, player);
                level.sendObjectUpdatePacket(x, y);
            } else {
                String message = new LocalMessage("ui", "Locked").translate();
                Color color = new Color(206, 15, 15, 255);
                level.hudManager.addElement(new FloatTextFade(x, y, message, new FontOptions(12).color(color)));
            }
        }
    }

    @Override
    public void onPathOpened(Level level, int tileX, int tileY) {}

    @Override
    public void onPathBreakDown(Level level, int tileX, int tileY, int damage, int hitX, int hitY) {}

    @Override
    public boolean pathCollidesIfOpen(Level level, int tileX, int tileY, CollisionFilter collisionFilter, Rectangle mobCollision) {
        return true;
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", this.isSwitched ? "closetip" : "opentip");
    }
}

package settlementexpansion.item.placeable;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.objectItem.SettlementFlagObjectItem;
import necesse.level.gameObject.SettlementFlagObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import settlementexpansion.SettlementExpansion;

public class SettlementFlagModObjectItem extends SettlementFlagObjectItem {

    public SettlementFlagModObjectItem(SettlementFlagObject object) {
        super(object);
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader) {
        if (level.isServerLevel() && player != null) {
            SettlementLevelData data = SettlementLevelData.getSettlementData(level);
            ServerClient client = player.getServerClient();
            if (data != null && level.settlementLayer.getOwnerAuth() == client.authentication && data.getObjectEntityPos() != null &&
                    !SettlementExpansion.getSettings().enableMultiFlagPerSettlement) {
                return "maxsettlementflags";
            }
        }

        return super.canPlace(level, x, y, player, item, contentReader);
    }

    @Override
    public InventoryItem onAttemptPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader, String error) {
        if (error.equals("maxsettlementflags")) {
            player.getServerClient().sendChatMessage(new LocalMessage("misc", "maxsettlementsflagreached"));
            return item;
        } else {
            return super.onAttemptPlace(level, x, y, player, item, contentReader, error);
        }
    }
}

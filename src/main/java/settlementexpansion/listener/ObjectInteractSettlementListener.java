package settlementexpansion.listener;

import necesse.engine.GameEventListener;
import necesse.engine.events.players.ObjectInteractEvent;
import necesse.entity.TileDamageType;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanAngerTargetAINode;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.InventoryObject;
import necesse.level.gameObject.furniture.StorageBoxInventoryObject;
import necesse.level.maps.layers.SettlementLevelLayer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import settlementexpansion.SettlementExpansion;
import settlementexpansion.map.settlement.SettlementModData;
import settlementexpansion.object.furniture.LockedInventoryObject;

public class ObjectInteractSettlementListener extends GameEventListener<ObjectInteractEvent> {

    @Override
    public void onEvent(ObjectInteractEvent event) {
        if (event.player != null && event.player.isServerClient() && SettlementExpansion.getSettings().enableSettlementLevelModification) {
            SettlementLevelData data = SettlementLevelData.getSettlementData(event.level);
            if (data != null) {
                SettlementLevelLayer layer = event.level.settlementLayer;
                SettlementModData layerData = SettlementModData.getSettlementModDataCreateIfNonExist(event.level);
                if (!layer.doesClientHaveAccess(event.player.getServerClient()) && !layerData.isPvpFlagged) {
                    event.preventDefault();
                }
            }

            if (SettlementExpansion.getSettings().enableHumansGetAngryOnBreakOrSteal) {
                PlayerMob player = event.player;
                if (!event.isPrevented() && (event.level.biome.hasVillage() || data != null)) {
                    GameObject object = event.level.getObject(event.tileX, event.tileY);
                    if (object instanceof InventoryObject) {
                        player.getLevel().entityManager.mobs.getInRegionByTileRange(player.getX() / 32, player.getY() / 32, 25).stream().filter((m) -> {
                            return m instanceof HumanMob && !m.isSameTeam(player);
                        }).forEach((m) -> {
                            HumanAngerTargetAINode<?> humanAngerHandler = (HumanAngerTargetAINode<?>)m.ai.blackboard.getObject(HumanAngerTargetAINode.class, "humanAngerHandler");
                            if (humanAngerHandler != null) {
                                humanAngerHandler.addEnemy(player, 20F);
                            }
                        });
                    }
                }
            }
        }

    }
}

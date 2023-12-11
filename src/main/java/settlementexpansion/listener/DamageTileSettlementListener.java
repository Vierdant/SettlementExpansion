package settlementexpansion.listener;

import necesse.engine.GameEventListener;
import necesse.engine.events.players.DamageTileEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketMobChat;
import necesse.entity.DamagedObjectEntity;
import necesse.entity.TileDamageType;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanAngerTargetAINode;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.gameObject.*;
import necesse.level.maps.LevelObject;
import necesse.level.maps.layers.SettlementLevelLayer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import settlementexpansion.SettlementExpansion;
import settlementexpansion.map.settlement.SettlementModData;

public class DamageTileSettlementListener extends GameEventListener<DamageTileEvent> {

    @Override
    public void onEvent(DamageTileEvent event) {
        if (event.client != null && event.client.isServer() && SettlementExpansion.getSettings().enableSettlementLevelModification) {
            SettlementLevelData data = SettlementLevelData.getSettlementData(event.level);
            if (data != null) {
                SettlementLevelLayer layer = event.level.settlementLayer;
                SettlementModData layerData = SettlementModData.getSettlementModDataCreateIfNonExist(event.level);
                if (!layer.doesClientHaveAccess(event.client) && !layerData.isPvpFlagged) {
                    event.preventDefault();
                }
            }

            if (SettlementExpansion.getSettings().enableHumansGetAngryOnBreakOrSteal) {
                PlayerMob player = event.client.playerMob;
                if (!event.isPrevented() && player != null && (event.level.biome.hasVillage() || data != null) && event.type == TileDamageType.Object) {
                    if (data != null && event.level.settlementLayer.doesClientHaveAccess(player.getServerClient())) {
                        return;
                    }
                    DamagedObjectEntity objectDamage = event.level.entityManager.getDamagedObjectEntity(event.tileX, event.tileY);
                    GameObject object = event.level.getObject(event.tileX, event.tileY);
                    LevelObject master = object.isMultiTileMaster() ? null : object.getMultiTile(event.level, event.tileX, event.tileY).getMasterLevelObject(event.level, event.tileX, event.tileY).orElse(null);

                    if (objectDamage != null && master == null) {
                        if (!(object instanceof TreeObject) && !(object instanceof SingleRockObject) && !(object instanceof SingleRockSmall) && !(object instanceof GrassObject)) {
                            int damage = objectDamage.objectDamage + event.damage;
                            if (damage >= object.objectHealth) {
                                player.getLevel().entityManager.mobs.getInRegionByTileRange(player.getX() / 32, player.getY() / 32, 25).stream().filter((m) ->
                                        m instanceof HumanMob && !m.isSameTeam(player)).forEach((m) -> {
                                    HumanAngerTargetAINode<?> humanAngerHandler = (HumanAngerTargetAINode<?>)m.ai.blackboard.getObject(HumanAngerTargetAINode.class, "humanAngerHandler");
                                    if (humanAngerHandler != null) {
                                        if (data != null && !event.level.biome.hasVillage()) {
                                            humanAngerHandler.addEnemy(player, 20F);
                                        } else if (event.level.biome.hasVillage()) {
                                            float oldAnger = humanAngerHandler.anger;
                                            humanAngerHandler.anger += 0.4F;
                                            if (humanAngerHandler.anger >= 1.0F) {
                                                humanAngerHandler.anger += 19F;
                                                if (oldAnger < 1.0F && !m.removed()) {
                                                    GameMessage attackMessage = ((HumanMob) m).getRandomAttackMessage();
                                                    if (attackMessage != null) {
                                                        m.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobChat(m.getUniqueID(), attackMessage), m);
                                                    }
                                                }

                                                humanAngerHandler.addEnemy(player, humanAngerHandler.anger);
                                            } else if (!m.removed()) {
                                                GameMessage angryMessage = ((HumanMob) m).getRandomAngryMessage();
                                                if (angryMessage != null) {
                                                    m.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobChat(m.getUniqueID(), angryMessage), m);
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
    }
}

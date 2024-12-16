package settlementexpansion.listener;

import necesse.engine.GameEventListener;
import necesse.engine.events.players.ObjectDamageEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketMobChat;
import necesse.entity.DamagedObjectEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanAngerTargetAINode;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.gameObject.*;
import necesse.level.maps.LevelObject;
import settlementexpansion.SettlementExpansion;

import java.util.Arrays;

public class ObjectDamageSettlementListener extends GameEventListener<ObjectDamageEvent> {

    @Override
    public void onEvent(ObjectDamageEvent event) {
        if (event.client != null && event.client.isServer()) {
            if (SettlementExpansion.getSettings().enableHumansGetAngryOnBreakOrSteal) {
                PlayerMob player = event.client.playerMob;
                if (!event.isPrevented() && player != null && !event.level.isCave && (event.level.biome.hasVillage() || event.level.settlementLayer.isActive())) {
                    if (event.level.settlementLayer.isActive() && event.level.settlementLayer.doesClientHaveAccess(player.getServerClient())) {
                        return;
                    }
                    DamagedObjectEntity objectDamage = event.level.entityManager.getDamagedObjectEntity(event.tileX, event.tileY);
                    GameObject object = event.level.getObject(event.tileX, event.tileY);
                    LevelObject master = object.isMultiTileMaster() ? null : object.getMultiTile(event.level, event.tileX, event.tileY).getMasterLevelObject(event.level, event.tileX, event.tileY).orElse(null);

                    if (objectDamage != null && master == null) {
                        if (!(object instanceof TreeObject) && !(object instanceof SingleRockObject) && !(object instanceof SingleRockSmall) && !(object instanceof GrassObject)) {
                            int damage = Arrays.stream(objectDamage.objectDamage).sum() + event.damage;
                            if (damage >= object.objectHealth) {
                                player.getLevel().entityManager.mobs.getInRegionByTileRange(player.getX() / 32, player.getY() / 32, 25).stream().filter((m) ->
                                        m instanceof HumanMob && !m.isSameTeam(player)).forEach((m) -> {
                                    HumanAngerTargetAINode<?> humanAngerHandler = (HumanAngerTargetAINode<?>)m.ai.blackboard.getObject(HumanAngerTargetAINode.class, "humanAngerHandler");
                                    if (humanAngerHandler != null) {
                                        if (event.level.settlementLayer.isActive() && !event.level.biome.hasVillage()) {
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

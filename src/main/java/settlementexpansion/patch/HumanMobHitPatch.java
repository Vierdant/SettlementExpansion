package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanAngerTargetAINode;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import net.bytebuddy.asm.Advice;
import settlementexpansion.SettlementExpansion;
import settlementexpansion.map.settlement.SettlementModData;

public class HumanMobHitPatch {

    @ModMethodPatch(target = Mob.class, name = "isServerHit", arguments = {GameDamage.class, float.class, float.class, float.class, Attacker.class})
    public static class HumanMobIsServerHitPatch {
        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        static boolean onEnter(@Advice.This Mob mob, @Advice.Argument(4) Attacker attacker) {
            if (mob.removed()) {
                return false;
            } else if (!mob.canTakeDamage()) {
                return false;
            } else {
                Mob attackOwner = attacker.getAttackOwner();
                if (attackOwner != null && attackOwner.isPlayer) {
                    SettlementLevelData data = SettlementLevelData.getSettlementData(mob.getLevel());
                    if (data != null && mob.isHuman) {
                        SettlementModData layerData = SettlementModData.getSettlementModDataCreateIfNonExist(mob.getLevel());
                        ServerClient client = ((PlayerMob)attackOwner).getServerClient();
                        if (!layerData.isPvpFlagged && !mob.getLevel().settlementLayer.doesClientHaveAccess(client) && !SettlementExpansion.getSettings().allowOwnedSettlerKillsNoPvP) {
                            return true;
                        } else {
                            setSettlerTarget(data, attackOwner);
                        }
                    } else if (data != null && mob.isPlayer) {
                        Mob target = getTargetMob(attackOwner, mob);
                        if (target != null) {
                            setSettlerTarget(data, target);
                        }
                    }

                    /*if (mob.isPlayer || mob.isHuman) {
                        setAdventureTarget(attackOwner, mob);
                    }*/
                }
            }
            return false;
        }

        public static void setAdventureTarget(Mob attackOwner, Mob mob) {
            attackOwner.getLevel().entityManager.mobs.getInRegionByTileRange(attackOwner.getX() / 32, attackOwner.getY() / 32, 25).stream().filter((m) -> {
                return m instanceof HumanMob && ((HumanMob) m).adventureParty.isInAdventureParty() && ((HumanMob) m).adventureParty.getServerClient() == ((PlayerMob)attackOwner).getServerClient();
            }).forEach((m) -> {
                HumanAngerTargetAINode<?> humanAngerHandler = (HumanAngerTargetAINode<?>)m.ai.blackboard.getObject(HumanAngerTargetAINode.class, "humanAngerHandler");
                if (humanAngerHandler != null) {
                    humanAngerHandler.addEnemy(mob, 10F);
                }

                if (mob.isPlayer) {
                    mob.getLevel().entityManager.mobs.getInRegionByTileRange(mob.getX() / 32, mob.getY() / 32, 25).stream().filter((h) -> {
                        return h instanceof HumanMob && ((HumanMob) h).adventureParty.isInAdventureParty() && ((HumanMob) h).adventureParty.getServerClient() == ((PlayerMob)mob).getServerClient();
                    }).forEach((h) -> {
                        if (humanAngerHandler != null) {
                            humanAngerHandler.addEnemy(h, 10F);
                        }
                    });
                }
            });
        }

        public static Mob getTargetMob(Mob mob, Mob other) {
            if (!mob.getLevel().settlementLayer.doesClientHaveAccess(((PlayerMob)mob).getServerClient())) {
                return mob;
            }
            if (!other.getLevel().settlementLayer.doesClientHaveAccess(((PlayerMob)other).getServerClient())) {
                return other;
            }
            return null;
        }

        public static void setSettlerTarget(SettlementLevelData data, Mob mob) {
            PlayerTeam team = ((PlayerMob)mob).getServerClient().getPlayerTeam();

            if (team == null) {
                for (LevelSettler settler : data.settlers) {
                    HumanAngerTargetAINode<?> humanAngerHandler = (HumanAngerTargetAINode<?>)settler.getMob().getMob().ai.blackboard.getObject(HumanAngerTargetAINode.class, "humanAngerHandler");
                    if (humanAngerHandler != null) {
                        humanAngerHandler.addEnemy(mob, 10F);
                    }
                }
            } else {
                ((PlayerMob)mob).getServerClient().getPlayerTeam().streamOnlineMembers(mob.getLevel().getServer()).filter((c) -> {
                    return c.getLevelIdentifier().equals(mob.getLevel().getIdentifier());
                }).forEach((c) -> {
                    for (LevelSettler settler : data.settlers) {
                        HumanAngerTargetAINode<?> humanAngerHandler = (HumanAngerTargetAINode<?>)settler.getMob().getMob().ai.blackboard.getObject(HumanAngerTargetAINode.class, "humanAngerHandler");
                        if (humanAngerHandler != null) {
                            humanAngerHandler.addEnemy(c.playerMob, 10F);
                        }
                    }
                });
            }
        }
    }

    @ModMethodPatch(target = HumanMob.class, name = "canBeTargeted", arguments = {Mob.class, NetworkClient.class})
    public static class HumanMobTargetingPatch {
        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        static boolean onEnter() {
            return true;
        }

        @Advice.OnMethodExit
        static boolean onExit(@Advice.This HumanMob humanMob, @Advice.Argument(0) Mob attacker, @Advice.Argument(1) NetworkClient client, @Advice.Return(readOnly = false) boolean out) {
            if (client != null) {
                if (humanMob.isFriendlyClient(client)) {
                    out = false;
                    return out;
                }

                if (!client.pvpEnabled() && humanMob.owner.get() != -1L) {
                    out = false;
                    return out;
                } else if (humanMob.owner.get() != -1L && SettlementExpansion.getSettings().requireSettlerOwnerOnlineToKill && humanMob.getLevel().getClient().getClientByAuth(humanMob.owner.get()) == null) {
                    out = false;
                    return out;
                }

                ClientClient partyOwner = humanMob.adventureParty.getClientClient();
                if (humanMob.adventureParty.isInAdventureParty() && partyOwner != null && !partyOwner.pvpEnabled()) {
                    out = false;
                    return out;
                }
            } else if (attacker instanceof HumanMob) {
                if (humanMob.isTravelingHuman()) {
                    out = false;
                    return out;
                }

                if (((HumanMob)attacker).isTravelingHuman()) {
                    out = false;
                    return out;
                }

                HumanAngerTargetAINode<?> humanAngerHandler = (HumanAngerTargetAINode<?>)attacker.ai.blackboard.getObject(HumanAngerTargetAINode.class, "humanAngerHandler");
                if (humanAngerHandler.enemies.contains(humanMob)) {
                    out = true;
                    return out;
                }
            }

            out = canBeTargeted(humanMob, attacker, client);
            return out;
        }

        public static boolean canBeTargeted(HumanMob mob, Mob attacker, NetworkClient attackerClient) {
            if (mob.buffManager.getModifier(BuffModifiers.UNTARGETABLE)) {
                return false;
            } else if (!mob.canTakeDamage()) {
                return false;
            } else if (mob.getUniqueID() == attacker.getUniqueID()) {
                return false;
            } else if (mob.getUniqueID() == attacker.mount) {
                return false;
            } else if (mob.isSameTeam(attacker)) {
                return false;
            } else if (!mob.isSamePlace(attacker)) {
                return false;
            } else if (attacker.isInAttackOwnerChain(mob)) {
                return false;
            } else if (!attacker.canTarget(mob)) {
                return false;
            } else {
                NetworkClient fClient = null;
                if (mob.getLevel().isServer()) {
                    fClient = mob.getFollowingServerClient();
                } else if (mob.getLevel().isServer()) {
                    fClient = mob.getFollowingClientClient();
                }

                if (fClient != null && attacker == (fClient).playerMob) {
                    return false;
                } else {
                    NetworkClient pvpOwner = mob.getPvPOwner();
                    return pvpOwner == null || attackerClient == null || pvpOwner == attackerClient || pvpOwner.pvpEnabled() && attackerClient.pvpEnabled();
                }
            }
        }
    }

}

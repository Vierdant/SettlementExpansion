package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.levelEvent.toolItemEvent.ToolItemEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.CowMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import net.bytebuddy.asm.Advice;
import settlementexpansion.SettlementExpansion;
import settlementexpansion.map.job.StudyBookLevelJob;
import settlementexpansion.entity.mob.friendly.HumanMobData;
import settlementexpansion.map.settlement.SettlementModData;

public class HumanMobPatch {

    @ModConstructorPatch(target = HumanMob.class, arguments = {int.class, int.class, String.class})
    public static class HumanMobConstructPatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.This HumanMob humanMob) {
            setJobHandlers(humanMob);

            if (!humanMob.getStringID().equalsIgnoreCase("travelingmerchant")) {
                HumanMobData.storage.put(humanMob.idData,
                        new HumanMobData(humanMob));
            }
        }

        public static void setJobHandlers(HumanMob humanMob) {
            humanMob.jobTypeHandler.setJobHandler(StudyBookLevelJob.class, 3000, 10000, 0, 4000, () ->
                            (!humanMob.isSettler() || humanMob.isSettlerOnCurrentLevel()) && !humanMob.getWorkInventory().isFull(),
                    (foundJob) -> StudyBookLevelJob.getJobSequence(humanMob, foundJob));
        }
    }

    @ModMethodPatch(target = HumanMob.class, name = "addSaveData", arguments = {SaveData.class})
    public static class HumanMobSavePatch {
        @Advice.OnMethodEnter
        static void onEnter(@Advice.This HumanMob humanMob, @Advice.Argument(0) SaveData save) {
            HumanMobData humanMobData = HumanMobData.storage.get(humanMob.idData);
            if (humanMobData != null) {
                save.addSaveData(humanMobData.getSaveData());
            }
        }
    }

    @ModMethodPatch(target = HumanMob.class, name = "applyLoadData", arguments = {LoadData.class})
    public static class HumanMobLoadPatch {
        @Advice.OnMethodEnter
        static void onEnter(@Advice.This HumanMob humanMob, @Advice.Argument(0) LoadData save) {
            HumanMobData humanMobData = HumanMobData.storage.get(humanMob.idData);
            if (humanMobData != null) {
                humanMobData.applyLoadData(save);
            }
        }
    }

    @ModMethodPatch(target = Mob.class, name = "isServerHit", arguments = {GameDamage.class, float.class, float.class, float.class, Attacker.class})
    public static class MobIsServerHitPatch {
        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        static boolean onEnter(@Advice.This Mob mob, @Advice.Argument(4) Attacker attacker) {
            if (mob.removed()) {
                return false;
            } else if (!mob.canTakeDmg()) {
                return false;
            } else if (!SettlementExpansion.getSettings().allowOwnedSettlerKillsNoPvP) {
                if (attacker.getAttackOwner().isPlayer && mob.isHuman) {
                    SettlementLevelData data = SettlementLevelData.getSettlementData(mob.getLevel());
                    if (data != null) {
                        SettlementModData layerData = SettlementModData.getSettlementModDataCreateIfNonExist(mob.getLevel());
                        ServerClient client = ((PlayerMob)attacker.getAttackOwner()).getServerClient();
                        return !layerData.isPvpFlagged && !mob.getLevel().settlementLayer.doesClientHaveAccess(client);
                    }
                }
            }
            return false;
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
                } else if (humanMob.owner.get() != -1L && SettlementExpansion.getSettings().requireSettlerOwnerOnlineToKill && humanMob.getLevel().getClient().getClientByAuth(humanMob.owner.get()) != null) {
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
            }

            out = canBeTargeted(humanMob, attacker, client);
            return out;
        }

        public static boolean canBeTargeted(HumanMob mob, Mob attacker, NetworkClient attackerClient) {
            if (mob.buffManager.getModifier(BuffModifiers.UNTARGETABLE)) {
                return false;
            } else if (!mob.canTakeDmg()) {
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
                if (mob.getLevel().isServerLevel()) {
                    fClient = mob.getFollowingServerClient();
                } else if (mob.getLevel().isClientLevel()) {
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

package settlementexpansion.patch;

import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.ProjectileHitboxMob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.entity.mobs.friendly.*;
import necesse.entity.mobs.friendly.critters.*;
import necesse.entity.mobs.friendly.critters.caveling.*;
import necesse.entity.mobs.friendly.human.ElderHumanMob;
import necesse.entity.mobs.friendly.human.GenericHumanMob;
import necesse.entity.mobs.friendly.human.GuardHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.*;
import necesse.entity.mobs.hostile.*;
import necesse.entity.mobs.hostile.bosses.*;
import necesse.entity.mobs.hostile.pirates.PirateCaptainMob;
import necesse.entity.mobs.hostile.pirates.PirateParrotMob;
import necesse.entity.mobs.hostile.pirates.PirateRecruit;
import necesse.entity.mobs.summon.MinecartMob;
import necesse.entity.mobs.summon.SawBladeMob;
import necesse.entity.mobs.summon.WoodBoatMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.*;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.*;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetParrotMob;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetPenguinMob;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetWalkingTorchMob;
import net.bytebuddy.asm.Advice;
import settlementexpansion.mob.friendly.BlacksmithModHumanMob;

@ModMethodPatch(target = MobRegistry.class, name = "registerCore", arguments = {})
public class MobRegistryPatch {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean onEnter() { return true; }

    @Advice.OnMethodExit
    static void onExit() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "mobs"));
        MobRegistry.registerMob("sheep", SheepMob.class, true);
        MobRegistry.registerMob("wildostrich", WildOstrichMob.class, true);
        MobRegistry.registerMob("cow", CowMob.class, true);
        MobRegistry.registerMob("pig", PigMob.class, true);
        MobRegistry.registerMob("honeybee", HoneyBeeMob.class, true);
        MobRegistry.registerMob("penguin", PenguinMob.class, true);
        MobRegistry.registerMob("polarbear", PolarBearMob.class, true);
        MobRegistry.registerMob("rabbit", RabbitMob.class, true);
        MobRegistry.registerMob("squirrel", SquirrelMob.class, true);
        MobRegistry.registerMob("snowhare", SnowHareMob.class, true);
        MobRegistry.registerMob("crab", CrabMob.class, true);
        MobRegistry.registerMob("scorpion", ScorpionMob.class, true);
        MobRegistry.registerMob("turtle", TurtleMob.class, true);
        MobRegistry.registerMob("swampslug", SwampSlugMob.class, true);
        MobRegistry.registerMob("frog", FrogMob.class, true);
        MobRegistry.registerMob("duck", DuckMob.class, true);
        MobRegistry.registerMob("bird", BirdMob.class, true);
        MobRegistry.registerMob("bluebird", BluebirdMob.class, true);
        MobRegistry.registerMob("canarybird", CanaryBirdMob.class, true);
        MobRegistry.registerMob("cardinalbird", CardinalBirdMob.class, true);
        MobRegistry.registerMob("spider", SpiderCritterMob.class, true);
        MobRegistry.registerMob("mouse", MouseMob.class, true);
        MobRegistry.registerMob("stonecaveling", StoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
        MobRegistry.registerMob("snowstonecaveling", SnowStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
        MobRegistry.registerMob("swampstonecaveling", SwampStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
        MobRegistry.registerMob("sandstonecaveling", SandStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
        MobRegistry.registerMob("deepstonecaveling", DeepStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
        MobRegistry.registerMob("deepsnowstonecaveling", DeepSnowStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
        MobRegistry.registerMob("deepswampstonecaveling", DeepSwampStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
        MobRegistry.registerMob("deepsandstonecaveling", DeepSandStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
        MobRegistry.registerMob("human", GenericHumanMob.class, true);
        MobRegistry.registerMob("farmerhuman", FarmerHumanMob.class, true);
        MobRegistry.registerMob("blacksmithhuman", BlacksmithModHumanMob.class, true);
        MobRegistry.registerMob("guardhuman", GuardHumanMob.class, true);
        MobRegistry.registerMob("magehuman", MageHumanMob.class, true);
        MobRegistry.registerMob("gunsmithhuman", GunsmithHumanMob.class, true);
        MobRegistry.registerMob("alchemisthuman", AlchemistHumanMob.class, true);
        MobRegistry.registerMob("hunterhuman", HunterHumanMob.class, true);
        MobRegistry.registerMob("elderhuman", ElderHumanMob.class, true);
        MobRegistry.registerMob("anglerhuman", AnglerHumanMob.class, true);
        MobRegistry.registerMob("pawnbrokerhuman", PawnBrokerHumanMob.class, true);
        MobRegistry.registerMob("animalkeeperhuman", AnimalKeeperHumanMob.class, true);
        MobRegistry.registerMob("stylisthuman", StylistHumanMob.class, true);
        MobRegistry.registerMob("piratehuman", PirateHumanMob.class, true);
        MobRegistry.registerMob("explorerhuman", ExplorerHumanMob.class, true);
        MobRegistry.registerMob("minerhuman", MinerHumanMob.class, true);
        MobRegistry.registerMob("travelingmerchant", TravelingMerchantMob.class, true);
        MobRegistry.registerMob("zombie", ZombieMob.class, true);
        MobRegistry.registerMob("trapperzombie", TrapperZombieMob.class, true);
        MobRegistry.registerMob("goblin", GoblinMob.class, true);
        MobRegistry.registerMob("vampire", VampireMob.class, true);
        MobRegistry.registerMob("zombiearcher", ZombieArcherMob.class, true);
        MobRegistry.registerMob("crawlingzombie", CrawlingZombieMob.class, true);
        MobRegistry.registerMob("giantcavespider", GiantCaveSpiderMob.class, true);
        MobRegistry.registerMob("blackcavespider", BlackCaveSpiderMob.class, true);
        MobRegistry.registerMob("swampcavespider", SwampCaveSpiderMob.class, true);
        MobRegistry.registerMob("cavemole", CaveMoleMob.class, true);
        MobRegistry.registerMob("frozendwarf", FrozenDwarfMob.class, true);
        MobRegistry.registerMob("frostsentry", FrostSentryMob.class, true);
        MobRegistry.registerMob("swampzombie", SwampZombieMob.class, true);
        MobRegistry.registerMob("swampslime", SwampSlimeMob.class, true);
        MobRegistry.registerMob("swampshooter", SwampShooterMob.class, true);
        MobRegistry.registerMob("enchantedzombie", EnchantedZombieMob.class, true);
        MobRegistry.registerMob("enchantedzombiearcher", EnchantedZombieArcherMob.class, true);
        MobRegistry.registerMob("enchantedcrawlingzombie", EnchantedCrawlingZombieMob.class, true);
        MobRegistry.registerMob("voidapprentice", VoidApprentice.class, true);
        MobRegistry.registerMob("mummy", MummyMob.class, true);
        MobRegistry.registerMob("mummymage", MummyMageMob.class, true);
        MobRegistry.registerMob("sandspirit", SandSpiritMob.class, true);
        MobRegistry.registerMob("jackal", JackalMob.class, true);
        MobRegistry.registerMob("skeleton", SkeletonMob.class, true);
        MobRegistry.registerMob("skeletonthrower", SkeletonThrowerMob.class, true);
        MobRegistry.registerMob("deepcavespirit", DeepCaveSpiritMob.class, true);
        MobRegistry.registerMob("skeletonminer", SkeletonMinerMob.class, true);
        MobRegistry.registerMob("ninja", NinjaMob.class, true);
        MobRegistry.registerMob("snowwolf", SnowWolfMob.class, true);
        MobRegistry.registerMob("cryoflake", CryoFlakeMob.class, true);
        MobRegistry.registerMob("giantswampslime", GiantSwampSlimeMob.class, true);
        MobRegistry.registerMob("swampskeleton", SwampSkeletonMob.class, true);
        MobRegistry.registerMob("smallswampcavespider", SmallCaveSpiderMob.class, true);
        MobRegistry.registerMob("swampdweller", SwampDwellerMob.class, true);
        MobRegistry.registerMob("sandworm", SandwormHead.class, true);
        MobRegistry.registerMob("sandwormbody", SandwormBody.class, false);
        MobRegistry.registerMob("sandwormtail", SandwormTail.class, false);
        MobRegistry.registerMob("desertcrawler", DesertCrawlerMob.class, true);
        MobRegistry.registerMob("ancientskeleton", AncientSkeletonMob.class, true);
        MobRegistry.registerMob("ancientskeletonthrower", AncientSkeletonThrowerMob.class, true);
        MobRegistry.registerMob("ancientarmoredskeleton", AncientArmoredSkeletonMob.class, true);
        MobRegistry.registerMob("ancientskeletonmage", AncientSkeletonMageMob.class, true);
        MobRegistry.registerMob("leggedslimethrower", LeggedSlimeThrowerMob.class, true);
        MobRegistry.registerMob("mageslime", MageSlimeMob.class, true);
        MobRegistry.registerMob("ghostslime", GhostSlimeMob.class, true);
        MobRegistry.registerMob("warriorslime", WarriorSlimeMob.class, true);
        MobRegistry.registerMob("slimeworm", SlimeWormHead.class, true);
        MobRegistry.registerMob("slimewormbody", SlimeWormBody.class, false);
        MobRegistry.registerMob("cryptbat", CryptBatMob.class, true);
        MobRegistry.registerMob("phantom", PhantomMob.class, true);
        MobRegistry.registerMob("cryptvampire", CryptVampireMob.class, true);
        MobRegistry.registerMob("humanraider", HumanRaiderMob.class, true);
        MobRegistry.registerMob("tameostrich", TameOstrichMob.class, false);
        MobRegistry.registerMob("petpenguin", PetPenguinMob.class, false);
        MobRegistry.registerMob("petparrot", PetParrotMob.class, false);
        MobRegistry.registerMob("minecartmount", MinecartMountMob.class, false, false, new LocalMessage("mob", "minecart"), (GameMessage)null);
        MobRegistry.registerMob("woodboatmount", WoodBoatMountMob.class, false, false, new LocalMessage("mob", "woodboat"), (GameMessage)null);
        MobRegistry.registerMob("steelboat", SteelBoatMob.class, false);
        MobRegistry.registerMob("petwalkingtorch", PetWalkingTorchMob.class, false);
        MobRegistry.registerMob("jumpingball", JumpingBallMob.class, false);
        MobRegistry.registerMob("hoverboard", HoverboardMob.class, false);
        MobRegistry.registerMob("babyzombie", BabyZombieMob.class, false);
        MobRegistry.registerMob("babyzombiearcher", BabyZombieArcherMob.class, false);
        MobRegistry.registerMob("babyspider", BabySpiderMob.class, false);
        MobRegistry.registerMob("frostpiercer", FrostPiercerFollowingMob.class, false);
        MobRegistry.registerMob("babysnowman", BabySnowmanMob.class, false);
        MobRegistry.registerMob("playerpoisonslime", PoisonSlimeFollowingMob.class, false);
        MobRegistry.registerMob("playervulturehatchling", VultureHatchlingFollowingMob.class, false);
        MobRegistry.registerMob("playerreaperspirit", ReaperSpiritFollowingMob.class, false);
        MobRegistry.registerMob("playercryoflake", CryoFlakeFollowingMob.class, false);
        MobRegistry.registerMob("playerpouncingslime", PouncingSlimeFollowingMob.class, false);
        MobRegistry.registerMob("slimegreatswordslime", SlimeGreatswordFollowingMob.class, false);
        MobRegistry.registerMob("babyskeleton", BabySkeletonMob.class, false);
        MobRegistry.registerMob("babyskeletonmage", BabySkeletonMageMob.class, false);
        MobRegistry.registerMob("playerchargingphantom", ChargingPhantomFollowingMob.class, false);
        MobRegistry.registerMob("evilsprotector", EvilsProtectorMob.class, true, true, new LocalMessage("quests", "evilsprotectortip"));
        MobRegistry.registerMob("evilsportal", EvilsPortalMob.class, true);
        MobRegistry.registerMob("portalminion", PortalMinion.class, true);
        MobRegistry.registerMob("queenspider", QueenSpiderMob.class, true, true, new LocalMessage("quests", "queenspidertip"));
        MobRegistry.registerMob("spiderhatchling", SpiderHatchlingMob.class, true);
        MobRegistry.registerMob("voidwizard", VoidWizard.class, true, true, new LocalMessage("quests", "voidwizardtip"));
        MobRegistry.registerMob("voidwizardclone", VoidWizardClone.class, false, false, new LocalMessage("mob", "voidwizard"), (GameMessage)null);
        MobRegistry.registerMob("swampguardian", SwampGuardianHead.class, true, true, new LocalMessage("quests", "swampguardiantip"));
        MobRegistry.registerMob("swampguardianbody", SwampGuardianBody.class, false, true);
        MobRegistry.registerMob("swampguardiantail", SwampGuardianTail.class, false, true);
        MobRegistry.registerMob("ancientvulture", AncientVultureMob.class, true, true, new LocalMessage("quests", "ancientvulturetip"));
        MobRegistry.registerMob("ancientvultureegg", AncientVultureEggMob.class, true);
        MobRegistry.registerMob("vulturehatchling", VultureHatchling.class, true);
        MobRegistry.registerMob("piratecaptain", PirateCaptainMob.class, true, true, new LocalMessage("quests", "piratecaptaintip"));
        MobRegistry.registerMob("piraterecruit", PirateRecruit.class, true);
        MobRegistry.registerMob("pirateparrot", PirateParrotMob.class, true);
        MobRegistry.registerMob("reaper", ReaperMob.class, true, true, new LocalMessage("quests", "reapertip"));
        MobRegistry.registerMob("reaperspiritportal", ReaperSpiritPortalMob.class, true);
        MobRegistry.registerMob("reaperspirit", ReaperSpiritMob.class, true);
        MobRegistry.registerMob("cryoqueen", CryoQueenMob.class, true, true, new LocalMessage("quests", "cryoqueentip"));
        MobRegistry.registerMob("pestwarden", PestWardenHead.class, true, true, new LocalMessage("quests", "pestwardentip"));
        MobRegistry.registerMob("pestwardenbody", PestWardenBody.class, true, true);
        MobRegistry.registerMob("grit", GritHead.class, true, true);
        MobRegistry.registerMob("sage", SageHead.class, true, true);
        MobRegistry.registerMob("flyingspiritsbody", FlyingSpiritsBody.class, false, true);
        MobRegistry.registerMob("sageandgrit", SageAndGritStartMob.class, true, false, new LocalMessage("quests", "sageandgrittip"));
        MobRegistry.registerMob("fallenwizard", FallenWizardMob.class, true, true, new LocalMessage("quests", "fallenwizardtip"));
        MobRegistry.registerMob("fallenwizardghost", FallenWizardGhostMob.class, false, false, new LocalMessage("mob", "fallenwizard"), (GameMessage)null);
        MobRegistry.registerMob("fallendragon", FallenDragonHead.class, true);
        MobRegistry.registerMob("fallendragonbody", FallenDragonBody.class, false);
        MobRegistry.registerMob("motherslime", MotherSlimeMob.class, true, true);
        MobRegistry.registerMob("nightswarm", NightSwarmStartMob.class, true, true);
        MobRegistry.registerMob("nightswarmbat", NightSwarmBatMob.class, true);
        MobRegistry.registerMob("woodboat", WoodBoatMob.class, false);
        MobRegistry.registerMob("minecart", MinecartMob.class, false);
        MobRegistry.registerMob("sawblade", SawBladeMob.class, false);
        MobRegistry.registerMob("trainingdummy", TrainingDummyMob.class, false);
        MobRegistry.registerMob("projectilehitbox", ProjectileHitboxMob.class, false, false, new StaticMessage("PROJECTILE_HITBOX"), (GameMessage)null);
        MobRegistry.registerMob("homeportal", HomePortalMob.class, false);
        MobRegistry.registerMob("returnportal", ReturnPortalMob.class, false);
    }

}
package settlementexpansion.entity.mob.friendly;

import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.humanShop.TravelingMerchantMob;
import necesse.engine.sound.GameMusic;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import settlementexpansion.SettlementExpansion;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TravelingMerchantModMob extends TravelingMerchantMob {

    public TravelingMerchantModMob() {
        super();
    }

    public ArrayList<ShopItem> getShopItems(VillageShopsData data, ServerClient client) {
        ArrayList<ShopItem> out = new ArrayList<>();
        GameRandom random = new GameRandom(this.getShopSeed() + 5L);
        SettlementLevelData settlement = SettlementLevelData.getSettlementData(this.getLevel());
        conditionSection(random, GameSeasons.isChristmas(), (r2) -> {
            out.add(ShopItem.item("christmastree", r2.getIntBetween(600, 800)));
            out.add(ShopItem.item("greenwrappingpaper", r2.getIntBetween(20, 40)));
            out.add(ShopItem.item("bluewrappingpaper", r2.getIntBetween(20, 40)));
            out.add(ShopItem.item("redwrappingpaper", r2.getIntBetween(20, 40)));
            out.add(ShopItem.item("yellowwrappingpaper", r2.getIntBetween(20, 40)));
        });
        out.add(ShopItem.item("rope", random.getIntBetween(150, 200)));
        out.add(ShopItem.item("piratemap", random.getIntBetween(180, 220)));
        out.add(ShopItem.item("brainonastick", random.getIntBetween(800, 1200)));
        conditionSection(random, random.getChance(4), (r) ->
                out.add(ShopItem.item("binoculars", r.getIntBetween(200, 300))));
        conditionSection(random, random.getChance(4), (r) ->
                out.add(ShopItem.item("recipebook", r.getIntBetween(500, 800))));
        boolean modActive = SettlementExpansion.getSettings().enableSettlementLevelModification;
        conditionSection(random,modActive && random.getChance(2), (r) ->
                out.add(ShopItem.item("claimingscroll", r.getIntBetween(3000, 5000))));
        out.add(ShopItem.item("potionpouch", random.getIntBetween(1000, 1200)));
        conditionSection(random, client.characterStats().mob_kills.getKills("piratecaptain") > 0, (r) ->
                out.add(ShopItem.item("ninjasmark", r.getIntBetween(800, 1200))));
        conditionSection(random, settlement != null, (r) -> {
            if (settlement == null) return;
            conditionSection(r, settlement.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("evilsprotector"), (r2) ->
                    out.add(ShopItem.item("ammopouch", r2.getIntBetween(400, 500))));
            conditionSection(r, settlement.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("queenspider"), (r2) ->
                    out.add(ShopItem.item("lunchbox", r2.getIntBetween(500, 600))));
            conditionSection(r, settlement.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("voidwizard"), (r2) ->
                    out.add(ShopItem.item("voidpouch", r2.getIntBetween(600, 800))));
            conditionSection(r, settlement.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("ancientvulture"), (r2) ->
                    out.add(ShopItem.item("recallflask", r2.getIntBetween(1000, 1200))));
            conditionSection(r, settlement.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("piratecaptain"), (r2) ->
                    out.add(ShopItem.item("coinpouch", r2.getIntBetween(1200, 1500))));
            conditionSection(r, settlement.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("reaper"), (r2) ->
                    out.add(ShopItem.item("hoverboard", r2.getIntBetween(1500, 1800))));
            conditionSection(r, settlement.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("cryoqueen"), (r2) ->
                    out.add(ShopItem.item("bannerstand", r2.getIntBetween(250, 350))));
            conditionSection(r, settlement.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("pestwarden"), (r2) ->
                    out.add(ShopItem.item("portalflask", r2.getIntBetween(1600, 2400))));
            conditionSection(r, settlement.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("sageandgrit"), (r2) ->
                    out.add(ShopItem.item("blinkscepter", r2.getIntBetween(1700, 2400))));
            conditionSection(r, settlement.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex("fallenwizard"), (r2) ->
                    out.add(ShopItem.item("voidbag", r2.getIntBetween(1900, 2600))));
        });
        conditionSection(random, random.getChance(4), (r) -> {
            out.add(ShopItem.item("importedcow", r.getIntBetween(200, 300)));
            out.add(ShopItem.item("importedsheep", r.getIntBetween(200, 300)));
            conditionSection(random, client.characterStats().mob_kills.getKills("piratecaptain") > 0, (r2) ->
                    out.add(ShopItem.item("importedpig", r2.getIntBetween(500, 600))));
        });
        TicketSystemList<Consumer<GameRandom>> cosmetics = getCosmeticShopItems(out);
        GameRandom sRandom = random.nextSeeded();
        (cosmetics.getRandomObject(sRandom)).accept(sRandom);
        conditionSection(random, random.getChance(4), (r) -> {
            out.add(ShopItem.item("musicplayer", r.getIntBetween(800, 1200)));
            out.add(ShopItem.item("portablemusicplayer", r.getIntBetween(800, 1200)));
            out.add(ShopItem.item("adventurebeginsvinyl", r.getIntBetween(75, 125)));
            out.add(ShopItem.item("homevinyl", r.getIntBetween(75, 125)));
            Iterable<GameMusic> currentMusic = this.getLevel().biome.getLevelMusic(this.getLevel(), client.playerMob).getMusicInList();

            for (GameMusic gameMusic : currentMusic) {
                String itemStringID = gameMusic.getStringID() + "vinyl";
                if (ItemRegistry.itemExists(itemStringID)) {
                    out.add(ShopItem.item(itemStringID, r.getIntBetween(75, 125)));
                }
            }

        });
        out.add(ShopItem.item("mapfragment", random.getIntBetween(-40, -80)));
        return out;

    }

    private TicketSystemList<Consumer<GameRandom>> getCosmeticShopItems(ArrayList<ShopItem> out) {
        TicketSystemList<Consumer<GameRandom>> cosmetics = new TicketSystemList<>();
        cosmetics.addObject(100, (r) ->
                out.add(ShopItem.item("jumpingball", r.getIntBetween(600, 800))));
        cosmetics.addObject(50, (r) ->
                out.add(ShopItem.item("weticicle", r.getIntBetween(350, 650))));
        cosmetics.addObject(100, (r) ->
                out.add(ShopItem.item("sunglasses", r.getIntBetween(300, 600))));
        cosmetics.addObject(100, (r) -> {
            out.add(ShopItem.item("hulahat", r.getIntBetween(200, 400)));
            out.add(ShopItem.item("hulaskirtwithtop", r.getIntBetween(200, 400)));
            out.add(ShopItem.item("hulaskirt", r.getIntBetween(200, 400)));
        });
        cosmetics.addObject(100, (r) -> {
            out.add(ShopItem.item("swimsuit", r.getIntBetween(200, 400)));
            out.add(ShopItem.item("swimtrunks", r.getIntBetween(200, 400)));
        });
        cosmetics.addObject(100, (r) -> {
            out.add(ShopItem.item("snowhood", r.getIntBetween(200, 400)));
            out.add(ShopItem.item("snowcloak", r.getIntBetween(200, 400)));
            out.add(ShopItem.item("snowboots", r.getIntBetween(200, 400)));
        });
        cosmetics.addObject(100, (r) -> {
            out.add(ShopItem.item("sailorhat", r.getIntBetween(200, 400)));
            out.add(ShopItem.item("sailorshirt", r.getIntBetween(200, 400)));
            out.add(ShopItem.item("sailorshoes", r.getIntBetween(200, 400)));
        });
        cosmetics.addObject(50, (r) -> {
            out.add(ShopItem.item("jesterhat", r.getIntBetween(200, 400)));
            out.add(ShopItem.item("jestershirt", r.getIntBetween(200, 400)));
            out.add(ShopItem.item("jesterboots", r.getIntBetween(200, 400)));
        });
        cosmetics.addObject(50, (r) -> {
            out.add(ShopItem.item("spacehelmet", r.getIntBetween(200, 400)));
            out.add(ShopItem.item("spacesuit", r.getIntBetween(200, 400)));
            out.add(ShopItem.item("spaceboots", r.getIntBetween(200, 400)));
        });
        return cosmetics;
    }
}

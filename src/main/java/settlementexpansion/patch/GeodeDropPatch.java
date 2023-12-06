package settlementexpansion.patch;

import necesse.engine.expeditions.MiningTripExpedition;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.RockObject;
import necesse.level.gameObject.SingleRockObject;
import necesse.level.gameObject.SingleRockSmall;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.forest.ForestBiome;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.biomes.swamp.SwampBiome;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import net.bytebuddy.asm.Advice;

import java.util.List;

public class GeodeDropPatch {

    @ModMethodPatch(target = RockObject.class, name = "getLootTable", arguments = {Level.class, int.class, int.class})
    public static class RockObjectDropPatch {

        @Advice.OnMethodExit
        static void onExit(@Advice.Return(readOnly = false) LootTable out) {
            out.items.add(new ChanceLootItem(0.05f, "geode", 1));
        }
    }

    @ModMethodPatch(target = SingleRockObject.class, name = "getLootTable", arguments = {Level.class, int.class, int.class})
    public static class SingleRockObjectDropPatch {

        @Advice.OnMethodExit
        static void onExit(@Advice.Return(readOnly = false) LootTable out) {
            out.items.add(new ChanceLootItem(0.05f, "geode", 1));
        }
    }

    @ModMethodPatch(target = SingleRockSmall.class, name = "getLootTable", arguments = {Level.class, int.class, int.class})
    public static class SingleRockSmallDropPatch {

        @Advice.OnMethodExit
        static void onExit(@Advice.Return(readOnly = false) LootTable out) {
            out.items.add(new ChanceLootItem(0.025f, "geode", 1));
        }
    }

    @ModMethodPatch(target = MiningTripExpedition.class, name = "getRewardItems", arguments = {SettlementLevelData.class, HumanMob.class})
    public static class MiningTripExpeditionDropPatch {

        @Advice.OnMethodExit
        static void onExit(@Advice.Return(readOnly = false) List<InventoryItem> out) {
            LootTable table = new LootTable();
            table.items.add(new ChanceLootItem(0.25f, "geode", GameRandom.globalRandom.getIntBetween(1, 5)));
            table.addItems(out, GameRandom.globalRandom, 1f);
        }
    }

    @ModMethodPatch(target = MiningTripExpedition.class, name = "getRewardItems", arguments = {SettlementLevelData.class, HumanMob.class})
    public static class TypesFishingTripExpeditionDropPatch {

        @Advice.OnMethodExit
        static void onExit(@Advice.Return(readOnly = false) List<InventoryItem> out) {
            LootTable table = new LootTable();
            table.items.add(new ChanceLootItem(0.25f, "geode", GameRandom.globalRandom.getIntBetween(1, 2)));
            table.addItems(out, GameRandom.globalRandom, 1f);
        }
    }

    @ModMethodPatch(target = Biome.class, name = "getFishingLootTable", arguments = {FishingSpot.class})
    public static class FishingDefaultLootTablePatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.Return(readOnly = false) FishingLootTable out) {
            out.addWater(25, "geode");
        }
    }

    @ModMethodPatch(target = ForestBiome.class, name = "getFishingLootTable", arguments = {FishingSpot.class})
    public static class FishingForestLootTablePatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.Return(readOnly = false) FishingLootTable out) {
            out.addWater(25, "geode");
        }
    }

    @ModMethodPatch(target = SnowBiome.class, name = "getFishingLootTable", arguments = {FishingSpot.class})
    public static class FishingSnowLootTablePatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.Return(readOnly = false) FishingLootTable out) {
            out.addWater(25, "geode");
        }
    }

    @ModMethodPatch(target = SwampBiome.class, name = "getFishingLootTable", arguments = {FishingSpot.class})
    public static class FishingSwampLootTablePatch {
        @Advice.OnMethodExit
        static void onExit(@Advice.Return(readOnly = false) FishingLootTable out) {
            out.addWater(25, "geode");
        }
    }
}

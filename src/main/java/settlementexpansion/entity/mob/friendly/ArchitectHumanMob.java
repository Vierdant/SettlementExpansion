package settlementexpansion.entity.mob.friendly;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.QuestMarkerOptions;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanLook;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.HumanGender;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import necesse.level.maps.light.GameLight;
import org.jetbrains.annotations.NotNull;
import settlementexpansion.ModResources;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class ArchitectHumanMob extends HumanShop {

    public ArchitectHumanMob() {
        super(500, 200, "architect");
        this.look = new HumanLook();
        this.attackCooldown = 500;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0F);
        this.equipmentInventory.setItem(6, new InventoryItem("handgun"));
    }

    public LootTable getLootTable() {
        return super.getLootTable();
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        GameSkin gameSkin = this.look.getGameSkin(true);

        for(int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), gameSkin.getBodyTexture(), GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(new InventoryItem("architecthat"));
        drawOptions.chestplate(new InventoryItem("businesssuit"));
        drawOptions.boots(new InventoryItem("businesssuitshoes"));
    }

    public QuestMarkerOptions getMarkerOptions(PlayerMob perspective) {
        return this.isVisitor() ? new QuestMarkerOptions('?', QuestMarkerOptions.orangeColor) : super.getMarkerOptions(perspective);
    }

    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        ArrayList<GameMessage> out = this.getLocalMessages("architecttalk", 4);
        HumanMob hb = this.getRandomHuman("stylist");
        if (hb != null) {
            out.add(new LocalMessage("mobmsg", "architectspecial1", "stylist", hb.getSettlerName()));
        }

        HumanMob hh = this.getRandomHuman("elder");
        if (hh != null) {
            out.add(new LocalMessage("mobmsg", "architectspecial2", "elder", hh.getSettlerName()));
        }

        return out;
    }

    public LevelIdentifier getRecruitedToLevel(ServerClient client) {
        return this.isVisitor() && SettlementLevelData.getSettlementData(this.getLevel()) != null ? this.getLevel().getIdentifier() : null;
    }

    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isSettler()) {
            return null;
        } else {
            GameRandom random = new GameRandom((this.getSettlerSeed() * 29L));
            if (this.isVisitor()) {
                return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(300, 500)));
            } else {
                LootTable secondItems = new LootTable(new CountOfTicketLootItems(random.getIntBetween(1, 2), 100, new LootItem("blueprintempty", Integer.MAX_VALUE), 100, new LootItem("goldbar", Integer.MAX_VALUE), 100, new LootItem("apple", Integer.MAX_VALUE)));
                ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, random.getIntBetween(300, 500), 0.20000000298023224, new LootItem("coin", Integer.MAX_VALUE));
                out.addAll(GameLootUtils.getItemsValuedAt(random, random.getIntBetween(75, 150), 0.20000000298023224, secondItems));
                out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
                return out;
            }
        }
    }

    public ArrayList<ShopItem> getShopItems(VillageShopsData data, ServerClient client) {
        if (this.isVisitor()) {
            return null;
        } else {
            ArrayList<ShopItem> out = new ArrayList<>();
            GameRandom random = new GameRandom(this.getShopSeed() + 5L);
            out.add(ShopItem.item("blueprintempty", this.getRandomHappinessPrice(random, 15, 25, 2)));
            out.add(ShopItem.item("trainingarea", this.getRandomHappinessPrice(random, 150, 350, 5)));
            out.add(ShopItem.item("storageroombig", this.getRandomHappinessPrice(random, 350, 580, 5)));
            return out;
        }
    }
}

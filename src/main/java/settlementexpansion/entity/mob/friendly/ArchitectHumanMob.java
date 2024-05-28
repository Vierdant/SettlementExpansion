package settlementexpansion.entity.mob.friendly;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
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
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import necesse.level.maps.light.GameLight;
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
        this.attackCooldown = 500;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0F);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.equipmentInventory.setItem(6, new InventoryItem("handgun"));
    }

    public LootTable getLootTable() {
        return super.getLootTable();
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for(int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), ModResources.architect.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    public DrawOptions getUserDrawOptions(Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective, Consumer<HumanDrawOptions> humanDrawOptionsModifier) {
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        Point sprite = this.getAnimSprite(x, y, this.dir);
        HumanDrawOptions humanOptions = (new HumanDrawOptions(level, ModResources.architect)).helmet(this.getDisplayArmor(0, (String)null)).chestplate(this.getDisplayArmor(1, "businesssuit")).boots(this.getDisplayArmor(2, "businesssuitshoes")).invis(this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(sprite).dir(this.dir).light(light);
        humanDrawOptionsModifier.accept(humanOptions);
        DrawOptions drawOptions = humanOptions.pos(drawX, drawY);
        DrawOptions markerOptions = this.getMarkerDrawOptions(x, y, light, camera, 0, -45, perspective);
        return () -> {
            drawOptions.draw();
            markerOptions.draw();
        };
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (this.objectUser == null || this.objectUser.object.drawsUsers()) {
            if (this.isVisible()) {
                GameLight light = level.getLightLevel(x / 32, y / 32);
                int drawX = camera.getDrawX(x) - 22 - 10;
                int drawY = camera.getDrawY(y) - 44 - 7;
                Point sprite = this.getAnimSprite(x, y, this.dir);
                drawY += this.getBobbing(x, y);
                drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
                HumanDrawOptions humanDrawOptions = (new HumanDrawOptions(level, ModResources.architect)).helmet(this.getDisplayArmor(0, (String)null)).chestplate(this.getDisplayArmor(1, "businesssuit")).boots(this.getDisplayArmor(2, "businesssuitshoes")).invis(this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(sprite).dir(this.dir).light(light);
                if (this.inLiquid(x, y)) {
                    drawY -= 10;
                    humanDrawOptions.armSprite(2);
                    humanDrawOptions.mask(MobRegistry.Textures.boat_mask[sprite.y % 4], 0, -7);
                }

                humanDrawOptions = this.setCustomItemAttackOptions(humanDrawOptions);
                final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
                final DrawOptions boat = this.inLiquid(x, y) ? MobRegistry.Textures.woodBoat.initDraw().sprite(0, this.dir % 4, 64).light(light).pos(drawX, drawY + 7) : null;
                final DrawOptions markerOptions = this.getMarkerDrawOptions(x, y, light, camera, 0, -45, perspective);
                list.add(new MobDrawable() {
                    public void draw(TickManager tickManager) {
                        if (boat != null) {
                            boat.draw();
                        }

                        drawOptions.draw();
                        markerOptions.draw();
                    }
                });
                this.addShadowDrawables(tileList, x, y, light, camera);
            }
        }
    }

    public QuestMarkerOptions getMarkerOptions(PlayerMob perspective) {
        return this.isTravelingHuman() ? new QuestMarkerOptions('?', QuestMarkerOptions.orangeColor) : super.getMarkerOptions(perspective);
    }

    public HumanMob.HumanGender getHumanGender() {
        return HumanGender.MALE;
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
        return this.isTravelingHuman() && SettlementLevelData.getSettlementData(this.getLevel()) != null ? this.getLevel().getIdentifier() : null;
    }

    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isSettler()) {
            return null;
        } else {
            GameRandom random = new GameRandom((this.getSettlerSeed() * 29L));
            if (this.isTravelingHuman()) {
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
        if (this.isTravelingHuman()) {
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

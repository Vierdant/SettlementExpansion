package settlementexpansion.tile;

import necesse.engine.GameEvents;
import necesse.engine.events.loot.TileLootTableDropsEvent;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.LevelTile;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import settlementexpansion.item.tool.CustomBrushToolItem;

import java.awt.*;
import java.util.ArrayList;

public class MudFossilTile extends TerrainSplatterTile {
    private final GameRandom drawRandom;

    public MudFossilTile() {
        super(false, "mudfossil");
        this.mapColor = new Color(66, 152, 66);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
    }

    @Override
    public Point getTerrainSprite(GameTextureSection gameTextureSection, Level level, int tileX, int tileY) {
        int tile;
        synchronized(this.drawRandom) {
            tile = this.drawRandom.seeded(this.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }

        return new Point(0, tile);
    }

    @Override
    public int getTerrainPriority() {
        return 100;
    }

    @Override
    public LootTable getLootTable(Level level, int tileX, int tileY) {
        return new LootTable(new LootItem("brokenfossil").preventLootMultiplier());
    }

    public LootTable getFossilLootTable(Level level, int tileX, int tileY) {
        return new LootTable(new LootItem("fossil").preventLootMultiplier());
    }

    public ArrayList<InventoryItem> getDroppedItems(Level level, int x, int y, boolean isBrush) {
        LootTable table = isBrush ? this.getFossilLootTable(level, x, y) : this.getLootTable(level, x, y);
        return table.getNewList(GameRandom.globalRandom, level.buffManager.getModifier(LevelModifiers.LOOT));
    }

    @Override
    public void onDestroyed(Level level, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        boolean isBrush = false;

        if (client != null) {
            client.newStats.tiles_mined.increment(1);
            isBrush = client.playerMob.attackingItem.item instanceof CustomBrushToolItem;
        }

        if (itemsDropped != null) {
            TileLootTableDropsEvent dropsEvent;
            GameEvents.triggerEvent(dropsEvent = new TileLootTableDropsEvent(new LevelTile(level, x, y), new Point(x * 32 + 16, y * 32 + 16), this.getDroppedItems(level, x, y, isBrush)));
            if (dropsEvent.dropPos != null && dropsEvent.drops != null) {
                for (InventoryItem item : dropsEvent.drops) {
                    ItemPickupEntity itemDropped = item.getPickupEntity(level, (float) dropsEvent.dropPos.x, (float) dropsEvent.dropPos.y);
                    level.entityManager.pickups.add(itemDropped);
                    itemsDropped.add(itemDropped);
                }
            }
        }

        if (!level.isServer()) {
            this.spawnDestroyedParticles(level, x, y);
        }

        level.setTile(x, y, this.getDestroyedTile());
    }

    @Override
    public ModifierValue<Float> getSlowModifier(Mob mob) {
        return mob.isFlying() ? super.getSpeedModifier(mob) : new ModifierValue<>(BuffModifiers.SLOW, 0.25F);
    }
}

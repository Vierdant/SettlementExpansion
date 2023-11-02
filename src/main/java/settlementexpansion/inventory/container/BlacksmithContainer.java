package settlementexpansion.inventory.container;

import necesse.engine.Screen;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.level.maps.hudManager.floatText.ItemPickupText;
import settlementexpansion.inventory.slots.GeodeSlot;
import settlementexpansion.item.geode.GeodeItem;
import settlementexpansion.mob.friendly.BlacksmithModHumanMob;

import java.util.List;

public class BlacksmithContainer extends ShopContainer {
    public final EmptyCustomAction breakGeodeButton;
    public final ContentCustomAction breakGeodeButtonResponse;
    public final BooleanCustomAction setIsBreakingGeode;
    public BlacksmithModHumanMob blacksmithMob;
    public final int RESULT_SLOT;
    public final int GEODE_SLOT;
    public boolean isBreakingGeode;
    public final long breakCostSeed;
    public final PlayerTempInventory geodeBreakInv;

    public BlacksmithContainer(final NetworkClient client, int uniqueSeed, BlacksmithModHumanMob mob, PacketReader contentReader) {
        super(client, uniqueSeed, mob, contentReader.getNextContentPacket());
        this.geodeBreakInv = client.playerMob.getInv().applyTempInventoryPacket(contentReader.getNextContentPacket(),
                (m) -> this.isClosed());

        this.GEODE_SLOT = this.addSlot(new GeodeSlot(this.geodeBreakInv, 0));
        this.RESULT_SLOT = this.addSlot(new ContainerSlot(this.geodeBreakInv, 1));
        this.addInventoryQuickTransfer((s) -> this.isBreakingGeode, this.GEODE_SLOT, this.GEODE_SLOT);
        this.addInventoryQuickTransfer((s) -> this.isBreakingGeode, this.RESULT_SLOT, this.RESULT_SLOT);

        this.blacksmithMob = mob;
        this.isBreakingGeode = false;
        this.breakCostSeed = this.priceSeed * (long) GameRandom.prime(28);

        this.breakGeodeButton = this.registerAction(new EmptyCustomAction() {
            public void run() {
                if (client.isServerClient()) {
                    if (canBreak()) {
                        int breakCost = getBreakCost();
                        InventoryItem item = getSlot(GEODE_SLOT).getItem();
                        InventoryItem result = getFocusedBreakResult(item);

                        geodeBreakInv.removeItems(client.playerMob.getLevel(), client.playerMob, item.item, 1, "break");
                        //System.out.println(geodeBreakInv.addItem(client.playerMob.getLevel(), client.playerMob, result, RESULT_SLOT, RESULT_SLOT, "break", null));

                        getSlot(RESULT_SLOT).setItem(result);

                        client.playerMob.getInv().main.removeItems(client.playerMob.getLevel(), client.playerMob, ItemRegistry.getItem("coin"), breakCost, "break");
                        Packet itemContent = InventoryItem.getContentPacket(result);
                        breakGeodeButtonResponse.runAndSend(itemContent);
                    }

                    getSlot(GEODE_SLOT).markDirty();
                    getSlot(RESULT_SLOT).markDirty();
                }
            }
        });

        this.breakGeodeButtonResponse = this.registerAction(new ContentCustomAction() {
            public void run(Packet content) {
                if (client.isClientClient()) {
                    InventoryItem resultItem = InventoryItem.fromContentPacket(content);
                    client.playerMob.getLevel().hudManager.addElement(new ItemPickupText(client.playerMob, resultItem));
                    Screen.playSound(GameResources.pop, SoundEffect.effect(client.playerMob));
                }
            }
        });

        this.setIsBreakingGeode = this.registerAction(new BooleanCustomAction() {
            public void run(boolean value) {
                BlacksmithContainer.this.isBreakingGeode = value;
            }
        });
    }

    public int getBreakCost() {
        if (this.getSlot(this.GEODE_SLOT).isClear()) {
            return 0;
        } else {
            InventoryItem item = this.getSlot(this.GEODE_SLOT).getItem();
            GeodeItem geode = (GeodeItem) item.item;
            GameRandom random = new GameRandom(this.breakCostSeed + ((long) item.item.getID() * GameRandom.prime(54)));
            return Math.abs(geode.getRandomBreakCost(random, this.settlerHappiness));
        }
    }

    public InventoryItem getFocusedBreakResult(InventoryItem item) {
        GeodeItem geode = (GeodeItem) item.item;
        List<Item> possibleItems = geode.getLootTable();
        float happinessMultiplier = 0.05F;
        float lotteryFocus = 0.0F;
        int settlerHappiness = GameMath.limit(this.settlerHappiness, 0, 100);
        TicketSystemList<Item> lottery = new TicketSystemList<>();
        if (settlerHappiness > 50) {
            lotteryFocus = (float)(settlerHappiness - 50) * happinessMultiplier;
        } else if (settlerHappiness < 50) {
            lotteryFocus = (float)(-settlerHappiness + 50) * happinessMultiplier;
        }

        for (Item entryItem : possibleItems) {
            float costModifier = (geode.getRarityModifier(entryItem.getRarity()) - 1F) * 100.0F;
            if (settlerHappiness > 50) {
                lottery.addObject(100 + (int) (lotteryFocus * costModifier), entryItem);
            } else {
                lottery.addObject(100 - (int) (lotteryFocus * costModifier), entryItem);
            }
        }

        Item reward = lottery.getRandomObject(GameRandom.globalRandom);
        return new InventoryItem(reward);
    }

    public boolean canBreak() {
        boolean check = !this.getSlot(this.GEODE_SLOT).isClear() && this.getSlot(this.RESULT_SLOT).isClear();
        if (check) {
            int amount = this.client.playerMob.getInv().main.getAmount(this.client.playerMob.getLevel(), this.client.playerMob, ItemRegistry.getItem("coin"), "buy");
            if (amount < this.getBreakCost()) {
                check = false;
            }
        }
        return check;
    }

    public static Packet getBlacksmithContainerContent(BlacksmithModHumanMob mob, ServerClient client) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextContentPacket(mob.getShopItemsContentPacket(client));
        writer.putNextContentPacket(client.playerMob.getInv().getTempInventoryPacket(1));
        return packet;
    }
}
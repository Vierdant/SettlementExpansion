package settlementexpansion.registry;

import necesse.engine.network.PacketReader;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.forms.presets.containerComponent.mob.MageContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.DresserContainerForm;
import necesse.inventory.container.mob.MageContainer;
import necesse.inventory.container.object.OEInventoryContainer;
import settlementexpansion.inventory.container.BlacksmithContainer;
import settlementexpansion.inventory.container.OELockedInventoryContainer;
import settlementexpansion.inventory.form.BlacksmithContainerForm;
import settlementexpansion.inventory.form.OELockedInventoryContainerForm;
import settlementexpansion.inventory.form.StudyTableContainerForm;
import settlementexpansion.mob.friendly.BlacksmithModHumanMob;
import settlementexpansion.object.entity.LockedInventoryObjectEntity;

public class ContainerModRegistry {

    public static int OE_LOCKED_INVENTORY_CONTAINER;
    public static int BLACKSMITH_CONTAINER;
    public static int STUDYTABLE_CONTAINER;

    public static void registerContainers() {
        OE_LOCKED_INVENTORY_CONTAINER =
                ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) ->
                                new OELockedInventoryContainerForm<>(client, new OELockedInventoryContainer(client.getClient(), uniqueSeed, (LockedInventoryObjectEntity) oe,
                                        new PacketReader(content))), (client, uniqueSeed, oe, content, serverObject) ->
                new OELockedInventoryContainer(client, uniqueSeed, (LockedInventoryObjectEntity) oe, new PacketReader(content)));

        BLACKSMITH_CONTAINER = ContainerRegistry.registerMobContainer((client, uniqueSeed, mob, content) ->
                new BlacksmithContainerForm<>(client, new BlacksmithContainer(client.getClient(), uniqueSeed, (BlacksmithModHumanMob)mob,
                        new PacketReader(content))), (client, uniqueSeed, mob, content, serverObject) ->
                new BlacksmithContainer(client, uniqueSeed, (BlacksmithModHumanMob) mob, new PacketReader(content)));

        STUDYTABLE_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) ->
                new StudyTableContainerForm(client, new OEInventoryContainer(client.getClient(), uniqueSeed, (OEInventory)oe,
                        new PacketReader(content))), (client, uniqueSeed, oe, content, serverObject) ->
                new OEInventoryContainer(client, uniqueSeed, (OEInventory)oe, new PacketReader(content)));
    }
}

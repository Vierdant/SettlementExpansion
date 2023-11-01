package settlementexpansion.registry;

import necesse.engine.network.PacketReader;
import necesse.engine.registries.ContainerRegistry;
import settlementexpansion.gfx.container.OELockedInventoryContainer;
import settlementexpansion.gfx.form.OELockedInventoryContainerForm;
import settlementexpansion.object.entity.LockedInventoryObjectEntity;

public class ContainerModRegistry {

    public static int OE_LOCKED_INVENTORY_CONTAINER;

    public static void registerContainers() {
        OE_LOCKED_INVENTORY_CONTAINER =
                ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) -> new OELockedInventoryContainerForm<>(client,
                        new OELockedInventoryContainer(client.getClient(), uniqueSeed, (LockedInventoryObjectEntity) oe, new PacketReader(content))),
                (client, uniqueSeed, oe, content, serverObject) ->
                new OELockedInventoryContainer(client, uniqueSeed, (LockedInventoryObjectEntity) oe, new PacketReader(content)));
    }
}

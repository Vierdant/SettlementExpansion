package settlementexpansion.inventory.slots;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.ContainerSlot;
import settlementexpansion.item.geode.GeodeItem;

public class GeodeSlot extends ContainerSlot {
    public GeodeSlot(Inventory inventory, int inventorySlot) {
        super(inventory, inventorySlot);
    }

    @Override
    public boolean isItemValid(InventoryItem item) {
        return item.item instanceof GeodeItem;
    }
}

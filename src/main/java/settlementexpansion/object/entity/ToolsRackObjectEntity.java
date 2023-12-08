package settlementexpansion.object.entity;

import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class ToolsRackObjectEntity extends InventoryObjectEntity {

    public ToolsRackObjectEntity(Level level, int x, int y) {
        super(level, x, y, 10);
    }

    @Override
    public boolean isItemValid(int slot, InventoryItem item) {
        if (item != null) {
            return item.item.isToolItem();
        }

        return true;
    }

    public boolean isInventoryFull() {
        for (int i = 0; i < this.slots; i++) {
            if (getInventory().isSlotClear(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isSettlementStorageItemDisabled(Item item) {
        return !item.isToolItem();
    }

    @Override
    public boolean canQuickStackInventory() {
        return false;
    }

    @Override
    public boolean canRestockInventory() {
        return false;
    }

    @Override
    public boolean canSortInventory() {
        return false;
    }

    @Override
    public boolean canUseForNearbyCrafting() {
        return false;
    }
}
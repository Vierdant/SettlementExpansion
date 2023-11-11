package settlementexpansion.object.entity;

import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class StudyTableObjectEntity extends InventoryObjectEntity {

    public StudyTableObjectEntity(Level level, int x, int y) {
        super(level, x, y, 1);
    }

    public boolean isItemValid(int slot, InventoryItem item) {
        if (item != null) {
            return isItemStudyMaterial(item.item);
        }

        return true;
    }

    public boolean isSettlementStorageItemDisabled(Item item) {
        return !isItemStudyMaterial(item);
    }

    public boolean canQuickStackInventory() {
        return false;
    }

    public boolean canRestockInventory() {
        return false;
    }

    public boolean canSortInventory() {
        return false;
    }

    public boolean canUseForNearbyCrafting() {
        return false;
    }

    private boolean isItemStudyMaterial(Item item) {
        return item.getStringID().equalsIgnoreCase("book");
    }
}

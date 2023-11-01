package settlementexpansion.object.entity;

import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.level.maps.Level;

public class FishDisplayObjectEntity extends InventoryObjectEntity {
    public FishDisplayObjectEntity(Level level, int x, int y) {
        super(level, x, y, 1);
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
}

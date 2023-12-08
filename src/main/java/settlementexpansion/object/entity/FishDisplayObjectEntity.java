package settlementexpansion.object.entity;

import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.level.maps.Level;

public class FishDisplayObjectEntity extends InventoryObjectEntity {
    public FishDisplayObjectEntity(Level level, int x, int y) {
        super(level, x, y, 1);
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

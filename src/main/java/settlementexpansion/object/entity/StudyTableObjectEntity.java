package settlementexpansion.object.entity;

import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.JobsLevelData;
import settlementexpansion.map.job.StudyBookLevelJob;

public class StudyTableObjectEntity extends InventoryObjectEntity {

    public StudyTableObjectEntity(Level level, int x, int y) {
        super(level, x, y, 1);
    }

    @Override
    public boolean isItemValid(int slot, InventoryItem item) {
        if (item != null) {
            return isItemStudyMaterial(item.item);
        }

        return true;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.getMaterialCount() > 0) {
            JobsLevelData.addJob(this.getLevel(), new StudyBookLevelJob(this.getX(), this.getY()));
            this.markDirty();
        }
    }

    public int getMaterialCount() {
        InventoryItem item = getInventory().getItem(0);
        return item != null ? item.getAmount() : 0;
    }

    @Override
    public boolean isSettlementStorageItemDisabled(Item item) {
        return !isItemStudyMaterial(item);
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

    private boolean isItemStudyMaterial(Item item) {
        return item.getStringID().equalsIgnoreCase("book");
    }
}

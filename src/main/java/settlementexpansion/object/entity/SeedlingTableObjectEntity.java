package settlementexpansion.object.entity;

import necesse.entity.objectEntity.ProcessingTechInventoryObjectEntity;
import necesse.level.maps.Level;
import settlementexpansion.registry.RecipeTechModRegistry;

public class SeedlingTableObjectEntity extends ProcessingTechInventoryObjectEntity {


    public SeedlingTableObjectEntity(Level level, int x, int y) {
        super(level, "seedlingtable", x, y, 1, 2, RecipeTechModRegistry.SEEDLINGTABLE);
    }

    @Override
    public int getProcessTime() {
        return 54000;
    }
}

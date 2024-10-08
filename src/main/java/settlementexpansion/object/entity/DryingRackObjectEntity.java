package settlementexpansion.object.entity;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ProcessingTechInventoryObjectEntity;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.level.maps.Level;
import settlementexpansion.registry.RecipeTechModRegistry;

public class DryingRackObjectEntity extends ProcessingTechInventoryObjectEntity {

    public DryingRackObjectEntity(Level level, int x, int y) {
        super(level, "dryingrack", x, y, 2, 2, RecipeTechModRegistry.DRYINGRACK);
    }

    @Override
    public int getProcessTime() {
        return 8000;
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        StringTooltips tooltips = new StringTooltips(this.getObject().getDisplayName());
        if (this.isProcessing()) {
            tooltips.add(Localization.translate("ui", "drying"));
        } else {
            tooltips.add(Localization.translate("ui", "needdrymaterial"));
        }

        GameTooltipManager.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
    }
}

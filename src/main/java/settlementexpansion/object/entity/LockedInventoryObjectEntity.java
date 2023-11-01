package settlementexpansion.object.entity;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;

public class LockedInventoryObjectEntity extends InventoryObjectEntity implements OEInventory {

    private long ownerAuth;
    private boolean locked;

    public LockedInventoryObjectEntity(Level level, int x, int y, int slots) {
        super(level, x, y, slots);
        this.ownerAuth = 0;
        this.locked = false;
    }

    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        StringTooltips tooltips = new StringTooltips(this.getObject().getDisplayName());
        String lockedMessage = this.locked ? "Locked" : "Unlocked";
        tooltips.add(Localization.translate("ui", "lockedinventory").replace("<state>", lockedMessage));
        Screen.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
    }

    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addLong("owner", this.ownerAuth);
        save.addBoolean("lockedstate", this.locked);
    }

    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.setCurrentOwner(save.getLong("owner", 0));
        this.locked = save.getBoolean("lockedstate", false);
    }

    public long getCurrentOwner() {
        return this.ownerAuth;
    }

    public void setCurrentOwner(long auth) {
        this.ownerAuth = auth;
    }

    public boolean getLockState() {
        return this.locked;
    }

    public void switchLockState() {
        this.locked = !this.locked;
    }
}

package settlementexpansion.object.entity;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;
import settlementexpansion.packet.PacketLockedInventory;

public class LockedInventoryObjectEntity extends InventoryObjectEntity implements OEInventory {

    private long ownerAuth;
    private boolean locked;
    private boolean loadDirty;

    public LockedInventoryObjectEntity(Level level, int x, int y, int slots) {
        super(level, x, y, slots);
        this.ownerAuth = 0;
        this.locked = false;
        this.loadDirty = false;
    }

    public void serverTick() {
        super.serverTick();
        serverTickTradesSync(getLevel().getServer());
    }

    private void serverTickTradesSync(Server server) {
        if (server == null) {
            return;
        }

        if (this.loadDirty) {
            System.out.println("Sent");
            getLevel().getServer().network.sendToClientsAt(new PacketLockedInventory(this), getLevel());
            markLoadClean();
        }
    }

    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        StringTooltips tooltips = new StringTooltips(this.getObject().getDisplayName());
        String lockedMessage = this.locked ? "Locked" : "Unlocked";
        tooltips.add(Localization.translate("ui", "lockedinventory").replace("<state>", lockedMessage));
        Screen.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
    }

    public void markLoadDirty() {
        this.loadDirty = true;
        System.out.println("Marking");
    }

    public void markLoadClean() {
        this.loadDirty = false;
    }

    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        SaveData state = new SaveData("LOCKSTATE");
        state.addLong("owner", this.ownerAuth);
        state.addBoolean("lockedstate", this.locked);
        save.addSaveData(state);
    }

    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        LoadData state = save.getFirstLoadDataByName("LOCKSTATE");
        this.ownerAuth = state.getLong("owner", 0);
        this.locked = state.getBoolean("lockedstate", false);
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
        markLoadDirty();
    }

    public Packet getContentPacket() {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextBoolean(this.locked);
        writer.putNextLong(this.ownerAuth);
        return packet;
    }

    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextBoolean(this.locked);
        writer.putNextLong(this.ownerAuth);
    }

    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.locked = reader.getNextBoolean();
        this.ownerAuth = reader.getNextLong();
    }

    public void applyContentPacket(Packet packet) {
        PacketReader reader = new PacketReader(packet);
        this.locked = reader.getNextBoolean();
        this.ownerAuth = reader.getNextLong();
    }
}

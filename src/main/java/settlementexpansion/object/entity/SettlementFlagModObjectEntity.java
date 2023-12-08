package settlementexpansion.object.entity;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.objectEntity.SettlementFlagObjectEntity;
import necesse.level.maps.Level;

public class SettlementFlagModObjectEntity extends SettlementFlagObjectEntity {
    public long cooldown;
    private long startCooldownTime;

    public SettlementFlagModObjectEntity(Level level, int x, int y) {
        super(level, x, y);
        this.cooldown = 60000;
    }

    public boolean onCooldown() {
        return this.startCooldownTime + this.cooldown > this.getWorldEntity().getTime();
    }

    public void startCooldown() {
        this.startCooldownTime = this.getWorldEntity().getTime();
        this.markDirty();
    }

    public long getCooldownTimeLeft() {
        return this.startCooldownTime + this.cooldown - this.getWorldEntity().getTime();
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextLong(this.startCooldownTime);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.startCooldownTime = reader.getNextLong();
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addLong("startCoolDownTime", this.startCooldownTime);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.startCooldownTime = save.getLong("startCoolDownTime");
    }
}

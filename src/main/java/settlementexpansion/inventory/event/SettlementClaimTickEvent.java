package settlementexpansion.inventory.event;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import settlementexpansion.inventory.container.SettlementClaimContainer;

public class SettlementClaimTickEvent extends ContainerEvent {

    public boolean canClaim;
    public boolean shouldTimerTick;

    public SettlementClaimTickEvent(SettlementClaimContainer container) {
        this.canClaim = container.canClaim;
        this.shouldTimerTick = false;
        if (container.client.isServerClient()){
            this.shouldTimerTick = container.checkDeadSettlers() && container.hasRequiredItem() && !container.timerStarted && !container.timerTicking() && !container.canClaim;
        }
    }

    public SettlementClaimTickEvent(PacketReader reader) {
        this.canClaim = reader.getNextBoolean();
        this.shouldTimerTick = reader.getNextBoolean();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextBoolean(this.canClaim);
        writer.putNextBoolean(this.shouldTimerTick);
    }
}

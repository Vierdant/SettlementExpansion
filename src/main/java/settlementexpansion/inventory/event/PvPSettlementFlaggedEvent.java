package settlementexpansion.inventory.event;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class PvPSettlementFlaggedEvent extends ContainerEvent {

    public boolean isFlagged;

    public PvPSettlementFlaggedEvent(boolean flagged) {
        this.isFlagged = flagged;
    }

    public PvPSettlementFlaggedEvent(PacketReader reader) {
        super(reader);
        this.isFlagged = reader.getNextBoolean();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextBoolean(this.isFlagged);
    }
}

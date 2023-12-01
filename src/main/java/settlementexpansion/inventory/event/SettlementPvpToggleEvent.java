package settlementexpansion.inventory.event;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.events.ContainerEvent;
import settlementexpansion.map.settlement.SettlementModData;

public class SettlementPvpToggleEvent extends ContainerEvent {

    public final boolean isServerClient;
    public final boolean isPvpFlagged;

    public SettlementPvpToggleEvent(SettlementModData data, ServerClient client) {
        this.isServerClient = client.isServerClient();
        this.isPvpFlagged = data.isPvpFlagged;
    }

    public SettlementPvpToggleEvent(PacketReader reader) {
        this.isServerClient = reader.getNextBoolean();
        this.isPvpFlagged = reader.getNextBoolean();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextBoolean(this.isServerClient);
        writer.putNextBoolean(this.isPvpFlagged);
    }
}

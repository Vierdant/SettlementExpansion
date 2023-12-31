package settlementexpansion.registry;

import necesse.inventory.container.events.ContainerEventRegistry;
import settlementexpansion.inventory.event.PvPSettlementFlaggedEvent;
import settlementexpansion.inventory.event.SettlementClaimTickEvent;
import settlementexpansion.inventory.event.SettlementModDataUpdateEvent;

public class ContainerEventModRegistry {

    public static void registerContainerEvents() {
        ContainerEventRegistry.registerUpdate(SettlementModDataUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementClaimTickEvent.class);
        ContainerEventRegistry.registerUpdate(PvPSettlementFlaggedEvent.class);
    }
}

package settlementexpansion.registry;

import necesse.inventory.container.events.ContainerEventRegistry;
import settlementexpansion.inventory.event.SettlementPvpToggleEvent;

public class ContainerEventModRegistry {

    public static void registerContainerEvents() {
        ContainerEventRegistry.registerUpdate(SettlementPvpToggleEvent.class);
    }
}

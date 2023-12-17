package settlementexpansion.inventory.form;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormHorizontalToggle;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementDefendZoneForm;
import necesse.gfx.gameFont.FontOptions;
import settlementexpansion.inventory.container.SettlementModContainer;
import settlementexpansion.inventory.event.SettlementModDataUpdateEvent;
import settlementexpansion.registry.ModBridgeRegistry;

public class SettlementModDefendZoneForm<T extends SettlementModContainer> extends SettlementDefendZoneForm<T> {
    private FormHorizontalToggle explosionDamageToggle;

    public SettlementModDefendZoneForm(Client client, T container, SettlementContainerForm<T> containerForm) {
        super(client, container, containerForm);
        FormFlow flow = new FormFlow(getHeight());
        int toggleX = 0;
        int labelX = 0;
        for (FormComponent comp : getComponents()) {
            if (comp instanceof FormHorizontalToggle) {
                toggleX = ((FormHorizontalToggle) comp).getX();
            } else if (comp instanceof FormLocalLabel) {
                labelX = ((FormLocalLabel) comp).getX();
            }
        }

        if (ModBridgeRegistry.hasBridge("snoobinoob.hardenedsettlements")) {
            FormLocalLabel label = addComponent(flow.nextY(new FormLocalLabel("ui", "settlementexplosiondamage", new FontOptions(16), FormLabel.ALIGN_LEFT, labelX, flow.next(), getWidth() - 60), 10));
            explosionDamageToggle = addComponent(new FormHorizontalToggle(toggleX, label.getY() + label.getHeight() / 2 - 8));
            explosionDamageToggle.onToggled(e -> container.setExplosionDamage.runAndSend(e.from.isToggled()));
        }

        setHeight(flow.next());
    }

    @Override
    protected void init() {
        super.init();
        if (ModBridgeRegistry.hasBridge("snoobinoob.hardenedsettlements")) {
            container.onEvent(SettlementModDataUpdateEvent.class, e -> explosionDamageToggle.setToggled(e.doExplosionDamage));
        }
    }
}

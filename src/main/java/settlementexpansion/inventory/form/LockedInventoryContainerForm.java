package settlementexpansion.inventory.form;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import settlementexpansion.inventory.container.LockedInventoryContainer;
import settlementexpansion.object.entity.LockedInventoryObjectEntity;
import settlementexpansion.ModResources;

public class LockedInventoryContainerForm<T extends LockedInventoryContainer> extends OEInventoryContainerForm<T> {

    private final FormContentIconButton lockButton;
    private final LockedInventoryObjectEntity objectEntity;

    public LockedInventoryContainerForm(Client client, T container) {
        super(client, container);
        this.objectEntity = container.lockedObjectEntity;
        FormFlow iconFlow = new FormFlow(this.inventoryForm.getWidth() - 4);

        boolean lockState = container.lockedObjectEntity.getLockState();

        this.lockButton = this.inventoryForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, getIcon(lockState), getTooltip(lockState)));
        this.lockButton.onClicked((e) -> {
            container.lockButton.runAndSend();
            update();
        });
        this.lockButton.setCooldown(500);
    }

    private ButtonIcon getIcon(boolean state) {
        return state ? ModResources.inventory_locked : ModResources.inventory_unlocked;
    }

    private GameMessage getTooltip(boolean state) {
        return new GameMessageBuilder().append("ui", state ? "unlockbutton" : "lockbutton");
    }

    private void update() {
        boolean state = objectEntity.getLockState();
        this.lockButton.setIcon(getIcon(state));
        this.lockButton.setTooltips(getTooltip(state));
    }
}

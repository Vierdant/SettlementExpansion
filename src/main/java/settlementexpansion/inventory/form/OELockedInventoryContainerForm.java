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
import settlementexpansion.inventory.container.OELockedInventoryContainer;
import settlementexpansion.object.entity.LockedInventoryObjectEntity;
import settlementexpansion.ModResources;

public class OELockedInventoryContainerForm<T extends OELockedInventoryContainer> extends OEInventoryContainerForm<T> {

    private final FormContentIconButton lockButton;
    private final LockedInventoryObjectEntity objectEntity;

    public OELockedInventoryContainerForm(Client client, T container, int height) {
        super(client, container, height);
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

    public OELockedInventoryContainerForm(Client client, T container) {
        this(client, container, getContainerHeight(container.getOEInventory().getInventory().getSize(), 10));
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

package settlementexpansion.inventory.slots;

import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;

public class FormContainerToolSlot extends FormContainerSlot {

    public FormContainerToolSlot(Client client, int containerSlotIndex, int x, int y) {
        super(client, containerSlotIndex, x, y);
        this.setDecal(Settings.UI.inventoryslot_icon_weapon);
    }
}

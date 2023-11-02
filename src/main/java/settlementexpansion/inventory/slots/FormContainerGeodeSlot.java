package settlementexpansion.inventory.slots;

import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;

public class FormContainerGeodeSlot extends FormContainerSlot {

    public FormContainerGeodeSlot(Client client, int containerSlotIndex, int x, int y) {
        super(client, containerSlotIndex, x, y);
        this.setDecal(Settings.UI.inventoryslot_icon_trinket);
    }

    public GameTooltips getClearTooltips() {
        return new StringTooltips(Localization.translate("itemtooltip", "geodeslot"));
    }
}

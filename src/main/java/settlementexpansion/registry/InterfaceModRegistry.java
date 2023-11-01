package settlementexpansion.registry;

import necesse.engine.Settings;
import necesse.gfx.ui.ButtonIcon;

public class InterfaceModRegistry {

    public static ButtonIcon inventory_locked;
    public static ButtonIcon inventory_unlocked;

    public static void loadTextures() {
        inventory_locked = new ButtonIcon(Settings.UI, "inventory_locked");
        inventory_unlocked = new ButtonIcon(Settings.UI, "inventory_unlocked");
    }

}

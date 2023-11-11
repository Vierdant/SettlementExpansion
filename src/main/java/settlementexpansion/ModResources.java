package settlementexpansion;

import necesse.engine.Settings;
import necesse.gfx.gameSound.GameSound;
import necesse.gfx.ui.ButtonIcon;

public class ModResources {

    public static GameSound safe_open;
    public static GameSound safe_close;
    public static ButtonIcon inventory_locked;
    public static ButtonIcon inventory_unlocked;

    public static void loadTextures() {
        inventory_locked = new ButtonIcon(Settings.UI, "inventory_locked");
        inventory_unlocked = new ButtonIcon(Settings.UI, "inventory_unlocked");
    }

    public static void loadSounds() {
        safe_open = GameSound.fromFile("safe_open");
        safe_close = GameSound.fromFile("safe_close");
    }

}

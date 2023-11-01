package settlementexpansion.registry;

import necesse.gfx.gameSound.GameSound;

public class ModResources {

    public static GameSound safe_open;
    public static GameSound safe_close;

    public static void loadSounds() {
        safe_open = GameSound.fromFile("safe_open");
        safe_close = GameSound.fromFile("safe_close");
    }

}

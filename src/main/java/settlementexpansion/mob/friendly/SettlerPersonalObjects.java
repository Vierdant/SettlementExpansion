package settlementexpansion.mob.friendly;

import necesse.engine.localization.message.GameMessageBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum SettlerPersonalObjects {
    HUNTER(Arrays.asList("dryingrack")),
    ANGLER(Arrays.asList("fishwalldisplay")),
    GENERIC(Collections.emptyList())
    ;

    final List<String> furniture;

    SettlerPersonalObjects(List<String> furniture) {
        this.furniture = furniture;
    }

    public List<String> getFurniture() {
        return furniture;
    }

    public static SettlerPersonalObjects getSettler(String id) {
        switch (id) {
            case "hunter": return HUNTER;
            case "angler": return ANGLER;
            default: return GENERIC;
        }
    }

}

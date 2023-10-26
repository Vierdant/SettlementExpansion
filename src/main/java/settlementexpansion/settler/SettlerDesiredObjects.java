package settlementexpansion.settler;

import necesse.engine.localization.message.GameMessageBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum SettlerDesiredObjects {
    HUNTER(Arrays.asList("dryingrack", "chair"),
            new GameMessageBuilder().append("remarks", "hunterpersonalizedfurnituremissing").translate()),
    GENERIC(Collections.emptyList(), "")
    ;

    final List<String> furniture;
    final String remark;

    SettlerDesiredObjects(List<String> furniture, String negative) {
        this.furniture = furniture;
        this.remark = negative;
    }

    public List<String> getDesiredFurniture() {
        return furniture;
    }

    public String getRemark() {
        return remark;
    }

    public static SettlerDesiredObjects getSettler(String id) {
        switch (id) {
            case "hunter": return HUNTER;
            default: return GENERIC;
        }
    }

}

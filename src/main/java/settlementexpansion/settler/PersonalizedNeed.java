package settlementexpansion.settler;

import necesse.engine.localization.message.GameMessageBuilder;
import necesse.entity.mobs.friendly.human.HappinessModifier;

public enum PersonalizedNeed {
    HUNTER("dryingrack", " Drying Rack...")
    ;


    final String furniture;
    final String negative;

    PersonalizedNeed(String furniture, String negative) {
        this.furniture = furniture;
        this.negative = negative;
    }

    public String getRequiredFurniture() {
        return furniture;
    }

    public String getNegativeRemark() {
        return negative;
    }

    public static PersonalizedNeed getSettlerPersonalizedData(String id) {
        switch (id) {
            case "hunter": return HUNTER;
            default: return null;
        }
    }

}

package settlementexpansion.object.furniture;

import necesse.level.gameObject.furniture.ChairObject;

import java.awt.*;

public class LeatherChairObject extends ChairObject {

    public LeatherChairObject(String textureName, Color mapColor) {
        super(textureName, mapColor);
        this.furnitureType = "leatherchair";
    }
}

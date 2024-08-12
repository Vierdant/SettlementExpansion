package settlementexpansion.item.armor.cosmetic;

import necesse.inventory.item.armorItem.HelmetArmorItem;

public class ArchitectHatArmorItem extends HelmetArmorItem {
    public ArchitectHatArmorItem() {
        super(0, null, 0, Rarity.COMMON, "architecthat");
        this.facialFeatureDrawOptions = FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.hairDrawOptions = HairDrawMode.OVER_HAIR;
        this.hairMaskTextureName = "safarihat_hardhat_minerhat_hairmask";
    }
}

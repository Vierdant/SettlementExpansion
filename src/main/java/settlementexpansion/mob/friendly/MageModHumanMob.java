package settlementexpansion.mob.friendly;

import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;

public class MageModHumanMob extends MageHumanMob {

    public MageModHumanMob() {
        super();
        this.jobTypeHandler.getPriority("studying").disabledBySettler = false;
    }
}

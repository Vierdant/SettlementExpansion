package settlementexpansion.map.settlement.settler;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.Settler;

import java.util.function.Supplier;

public class ArchitectSettler extends Settler {

    public ArchitectSettler() {
        super("architecthuman");
    }

    public boolean isAvailableForClient(SettlementLevelData settlement, PlayerStats stats) {
        return super.isAvailableForClient(settlement, stats) && (stats.items_obtained.isItemObtained("blueprintempty"));
    }

    public GameMessage getAcquireTip() {
        return new LocalMessage("settlement", "foundinvillagetip");
    }

    public void addNewRecruitSettler(SettlementLevelData data, boolean isRandomEvent, TicketSystemList<Supplier<HumanMob>> ticketSystem) {
        if (isRandomEvent || !this.doesSettlementHaveThisSettler(data)) {
            ticketSystem.addObject(75, this.getNewRecruitMob(data));
        }
    }
}

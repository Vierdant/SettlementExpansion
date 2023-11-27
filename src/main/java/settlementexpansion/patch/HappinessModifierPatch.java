package settlementexpansion.patch;

import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import net.bytebuddy.asm.Advice;
import settlementexpansion.SettlementExpansion;
import settlementexpansion.map.settlement.SettlementRoomData;
import settlementexpansion.entity.mob.friendly.HumanMobData;
import settlementexpansion.entity.mob.friendly.SettlerPersonalObjects;
import settlementexpansion.util.StringUtil;

import java.awt.*;
import java.util.List;
import java.util.StringJoiner;

@ModMethodPatch(target = HumanMob.class, name = "getHappinessModifiers", arguments = {})
public class HappinessModifierPatch {

    @Advice.OnMethodExit
    static void onExit(@Advice.This HumanMob mob, @Advice.Return(readOnly = false)List<HappinessModifier> modifiers) {
        if (SettlementExpansion.getSettings().enableHappinessModifier) {
            if (mob.levelSettler != null) {
                if (mob.levelSettler.getBed() != null) {
                    SettlementRoom room = mob.levelSettler.getBed().getRoom();
                    if (room != null) {
                        SettlementRoomData roomData = SettlementRoomData
                                .storage.get(new Point(room.tileX, room.tileY));
                        List<String> objectsList = SettlerPersonalObjects.getSettler(mob.settlerStringID).getObjects();
                        if (!objectsList.isEmpty()) {
                            StringJoiner joiner = new StringJoiner(", ");



                            int objectScore = 0;
                            for (String entry : objectsList) {
                                if (roomData.getObjectType(entry) > 0) {
                                    objectScore++;
                                    continue;
                                }

                                if (room.getFurnitureTypes(entry) > 0) {
                                    objectScore++;
                                    continue;
                                }

                                // adds missing objects to the joiner
                                joiner.add(new LocalMessage("object", entry).translate());
                            }
                            // in percentage how much of the desired furniture found from total, applied to 10
                            int specialObjectModifier = !objectsList.isEmpty() ?
                                    (objectScore * 100 / objectsList.size()) * 10 / 100 : 0;

                            if (specialObjectModifier <= 0) specialObjectModifier = -10;

                            GameMessageBuilder builder = new GameMessageBuilder();
                            modifiers.add(new HappinessModifier(specialObjectModifier,
                                    specialObjectModifier != 10 ?
                                            builder.append(new LocalMessage("ui", "nopersonalizedroom", "list", joiner.toString())) :
                                            builder.append("ui", "personalizedroom")));
                        }

                        HumanMobData humanMobData = HumanMobData.storage.get(mob.idData);
                        if (humanMobData != null && roomData != null) {
                            if (roomData.getFurnitureMajority() != null &&
                                    humanMobData.preferredFurnitureType == roomData.getFurnitureMajority()) {
                                modifiers.add(new HappinessModifier(+10, new GameMessageBuilder()
                                        .append("ui", "preferredfurniturewoodtype")));
                            } else {
                                String choice = humanMobData.preferredFurnitureType.getString();
                                modifiers.add(new HappinessModifier(-10, new GameMessageBuilder().append(
                                        new LocalMessage("ui", "notfurniturewoodtype", "wood", StringUtil.capitalize(choice)))));
                            }
                        }
                    }
                }
            }
        }
    }
}

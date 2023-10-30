package settlementexpansion.patch;

import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import net.bytebuddy.asm.Advice;
import settlementexpansion.map.settlement.SettlementRoomData;
import settlementexpansion.mob.friendly.HumanMobData;
import settlementexpansion.mob.friendly.SettlerPersonalObjects;
import settlementexpansion.util.StringUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@ModMethodPatch(target = HumanMob.class, name = "getHappinessModifiers", arguments = {})
public class HappinessModifierPatch {

    @Advice.OnMethodExit
    static void onExit(@Advice.This HumanMob mob, @Advice.Return(readOnly = false)List<HappinessModifier> modifiers) {
        if (mob.levelSettler != null) {
            if (mob.levelSettler.getBed() != null) {
                SettlementRoom room = mob.levelSettler.getBed().getRoom();
                if (room != null) {
                    List<String> objectsList = SettlerPersonalObjects.getSettler(mob.settlerStringID).getFurniture();

                    if (!objectsList.isEmpty()) {
                        GameMessageBuilder builder = new GameMessageBuilder().append(" ");
                        StringJoiner joiner = new StringJoiner(", ");

                        SettlementRoomData roomData = SettlementRoomData
                                .storage.get(new Point(room.tileX, room.tileY));

                        int objectScore = 0;
                        for (String entry : objectsList) {
                            if (roomData.getObjectType(entry) > 0) {
                                objectScore++;
                                continue;
                            }
                            // adds missing objects to the joiner
                            joiner.add(builder.append("object", entry).translate());
                            builder.clear();
                        }
                        // in percentage how much of the desired furniture found from total, applied to 10
                        int specialObjectModifier = !objectsList.isEmpty() ?
                                (objectScore * 100 / objectsList.size()) * 10 / 100 : 0;

                        if (specialObjectModifier <= 0) specialObjectModifier = -10;

                        modifiers.add(new HappinessModifier(specialObjectModifier,
                                specialObjectModifier != 10 ?
                                        builder.append("settlement", "nopersonalizedobject")
                                                .append(joiner.toString()) :
                                        builder.append("settlement", "personalizedobject")));
                    }

                    HumanMobData humanMobData = HumanMobData.storage.get(mob.idData);
                    SettlementRoomData roomData = SettlementRoomData.storage.get(new Point(room.tileX, room.tileY));
                    if (humanMobData != null && roomData != null) {
                        if (roomData.getFurnitureMajority() != null &&
                                humanMobData.preferredFurnitureType == roomData.getFurnitureMajority()) {
                            modifiers.add(new HappinessModifier(+10, new GameMessageBuilder()
                                    .append("Furniture Set of Choice")));
                        } else {
                            String choice = humanMobData.preferredFurnitureType.getString();
                            modifiers.add(new HappinessModifier(0, new GameMessageBuilder().append(
                                    "I like more " + StringUtil.capitalize(choice) + " Furniture in my room")));
                        }
                    }
                }
            }
        }
    }

}

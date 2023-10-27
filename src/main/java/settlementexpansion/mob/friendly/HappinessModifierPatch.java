package settlementexpansion.mob.friendly;

import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import net.bytebuddy.asm.Advice;
import settlementexpansion.map.settlement.SettlementRoomData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@ModMethodPatch(target = HumanMob.class, name = "getHappinessModifiers", arguments = {})
public class HappinessModifierPatch {

    public static List<HappinessModifier> instanceModifiers;

    @Advice.OnMethodEnter()
    static void onEnter(@Advice.This HumanMob humanMob, @Advice.Local("modifiers") ArrayList<HappinessModifier> modifiers) {
        modifiers = new ArrayList<>();

        if (humanMob.levelSettler != null) {
            if (humanMob.levelSettler.getBed() != null) {
                SettlementRoom room = humanMob.levelSettler.getBed().getRoom();
                if (room != null) {
                    SettlerPersonalObjects settler = SettlerPersonalObjects.getSettler(humanMob.settlerStringID);
                    List<String> personalFurniture = settler.getFurniture();
                    if (!personalFurniture.isEmpty()) {
                        GameMessageBuilder remarkBuilder = new GameMessageBuilder().append(" ");
                        StringJoiner remarkJoiner = new StringJoiner(", ");

                        int specialFurnitureScore = 0;
                        for (String entry : personalFurniture) {
                            if (room.getFurnitureTypes(entry) > 0) {
                                specialFurnitureScore++;
                                continue;
                            }
                            // adds missing furniture to the remark joiner
                            remarkJoiner.add(remarkBuilder.append("object", entry).translate());
                            remarkBuilder.clear();
                        }
                        // in percentage how much of the desired furniture found from total, applied to 10
                        int specialNeedsHappiness = !personalFurniture.isEmpty() ? (specialFurnitureScore * 100 / personalFurniture.size()) * 10 / 100 : 0;

                        if (specialNeedsHappiness <= 0) {
                            modifiers.add(new HappinessModifier(-10,
                                    (new GameMessageBuilder()).append("settlement", "personalizedfurnituremissing")
                                            .append(remarkJoiner.toString()).append(settler.getRemark())));
                        } else {
                            if (specialNeedsHappiness != 10) {
                                remarkBuilder.append("settlement", "personalizedfurniturepartialmissing")
                                        .append(remarkJoiner.toString()).append(settler.getRemark());
                            } else {
                                remarkBuilder.append("settlement", "personalizedfurniture");
                            }

                            modifiers.add(new HappinessModifier(specialNeedsHappiness, remarkBuilder));
                        }
                    }

                    HumanMobData humanMobData = HumanMobData.storage.get(humanMob.idData);
                    SettlementRoomData roomData = SettlementRoomData.storage.get(new Point(room.tileX, room.tileY));
                    if (humanMobData != null && roomData != null) {
                        if (roomData.getFurnitureMajority() != null) {
                            if (humanMobData.preferredFurnitureSet.getSet() == roomData.getFurnitureMajority().getSet()) {
                                modifiers.add(new HappinessModifier(+10, new GameMessageBuilder().append("Furniture Set of Choice")));
                            } else {
                                String choice = humanMobData.preferredFurnitureSet.getString();
                                modifiers.add(new HappinessModifier(0, new GameMessageBuilder().append(
                                        "I like more " + choice.substring(0,1).toUpperCase() + choice.substring(1) + " Furniture in my room")));
                            }
                        } else {
                            String choice = humanMobData.preferredFurnitureSet.getString();
                            modifiers.add(new HappinessModifier(0, new GameMessageBuilder().append(
                                    "I like more " + choice.substring(0,1).toUpperCase() + choice.substring(1) + " Furniture in my room")));
                        }
                    }
                }
            }
        }
        instanceModifiers = modifiers;

    }

    @Advice.OnMethodExit
    static void onExit(@Advice.Return(readOnly = false)List<HappinessModifier> modifiers) {
        modifiers.addAll(instanceModifiers);
    }

}

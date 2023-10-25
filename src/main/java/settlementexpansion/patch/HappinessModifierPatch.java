package settlementexpansion.patch;

import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import net.bytebuddy.asm.Advice;
import settlementexpansion.settler.PersonalizedNeed;

import java.util.ArrayList;
import java.util.List;

@ModMethodPatch(target = HumanMob.class, name = "getHappinessModifiers", arguments = {})
public class HappinessModifierPatch {

    public static List<HappinessModifier> instanceModifiers;

    @Advice.OnMethodEnter()
    static void onEnter(@Advice.This HumanMob humanMob, @Advice.Local("modifiers") ArrayList<HappinessModifier> modifiers) {
        System.out.println("Entered HumanMob.getHappinessModifiers(): " + humanMob.settlerStringID);
        modifiers = new ArrayList<>();
        if (humanMob.levelSettler != null) {
            if (humanMob.levelSettler.getBed() != null) {
                SettlementRoom room = humanMob.levelSettler.getBed().getRoom();
                if (room != null) {
                    System.out.println("| Found room for settler: " + humanMob.settlerStringID);
                    PersonalizedNeed personalizedId = PersonalizedNeed.getSettlerPersonalizedData(humanMob.settlerStringID);
                    if (personalizedId != null) {
                        System.out.println("| Found personalization for settler: " + humanMob.settlerStringID);
                        int qualityFurniture = room.getFurnitureTypes(personalizedId.getRequiredFurniture());
                        System.out.println("| Found Passed check for settler: " + humanMob.settlerStringID);
                        if (qualityFurniture <= 0) {
                            modifiers.add(new HappinessModifier(-10, (new GameMessageBuilder()).append("settlement", "personalizedfurnituremissing")
                                    .append(personalizedId.getNegativeRemark())));
                            System.out.println("| No quality piece for settler: " + humanMob.settlerStringID);
                        } else {
                            modifiers.add(new HappinessModifier(10, (new GameMessageBuilder()).append("settlement", "personalizedfurniture")));
                            System.out.println("| Found quality piece for settler: " + humanMob.settlerStringID);
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

package settlementexpansion.util;

import necesse.level.maps.presets.set.FurnitureSet;
import settlementexpansion.mob.SettlerPersonalObjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum FurnitureSetEnum {
     OAK(FurnitureSet.oak),
    SPRUCE(FurnitureSet.spruce),
    PINE(FurnitureSet.pine),
    PALM(FurnitureSet.palm),
    DUNGEON(FurnitureSet.dungeon),
    DEADWOOD(FurnitureSet.deadwood);

    public final FurnitureSet set;


    FurnitureSetEnum(FurnitureSet set) {
        this.set = set;
    }

    public FurnitureSet getSet() {
        return set;
    }

    public static FurnitureSetEnum weightedSelection(String id) {
        FurnitureSetEnum[] sets = values();
        double[] weights = FurnitureSetChances.getSettler(id).getWeights();

        double totalWeight = 0.0;
        for (double i : weights) {
            totalWeight += i;
        }

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < weights.length - 1; ++idx) {
            r -= weights[idx];
            if (r <= 0.0) break;
        }
        return sets[idx];
    }



    public enum FurnitureSetChances {
        // oak, spruce, pine, palm, dungeon, deadwood
        HUNTER(new double[]{0.25, 0.75, 0.0, 0.0, 0.0, 0.0}),
        GENERIC(new double[]{0.25, 0.25, 0.25, 0.25, 0.0, 0.0});

        public final double[] weights;


        FurnitureSetChances(double[] weights) {
            this.weights = weights;
        }

        public double[] getWeights() {
            return weights;
        }

        public static FurnitureSetChances getSettler(String id) {
            switch (id) {
                case "hunter": return HUNTER;
                default: return GENERIC;
            }
        }
    }
}

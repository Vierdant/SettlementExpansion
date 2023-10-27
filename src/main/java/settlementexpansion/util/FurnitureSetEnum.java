package settlementexpansion.util;

import necesse.level.maps.presets.set.FurnitureSet;

public enum FurnitureSetEnum {
     OAK("oak", FurnitureSet.oak),
    SPRUCE("spruce", FurnitureSet.spruce),
    PINE("pine", FurnitureSet.pine),
    PALM("palm", FurnitureSet.palm),
    DUNGEON("dungeon", FurnitureSet.dungeon),
    DEADWOOD("deadwood", FurnitureSet.deadwood);

     public final String string;
    public final FurnitureSet set;


    FurnitureSetEnum(String string, FurnitureSet set) {
        this.string = string;
        this.set = set;
    }

    public String getString() {
        return string;
    }

    public FurnitureSet getSet() {
        return set;
    }

    public static FurnitureSetEnum weightedSelection(String id) {
        FurnitureSetEnum[] sets = values();
        double[] weights = FurnitureSetChances.getSettler(id).getWeights();

        double totalWeight = 0.0;
        for (double entry : weights) {
            totalWeight += entry;
        }

        int bingo = 0;
        for (double result = Math.random() * totalWeight; bingo < weights.length - 1; ++bingo) {
            result -= weights[bingo];
            if (result <= 0.0) break;
        }
        return sets[bingo];
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

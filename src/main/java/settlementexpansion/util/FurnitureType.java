package settlementexpansion.util;

import necesse.level.maps.presets.set.FurnitureSet;

public enum FurnitureType {
     OAK("oak"),
    SPRUCE("spruce"),
    PINE("pine"),
    PALM("palm"),
    DUNGEON("dungeon"),
    DEADWOOD("deadwood");

     public final String string;


    FurnitureType(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public static FurnitureType weightedSelection(String id) {
        FurnitureType[] sets = values();
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

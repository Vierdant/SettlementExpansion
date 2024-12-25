package settlementexpansion.map.settlement;

public enum FurnitureWoodType {
     OAK("oak"),
    SPRUCE("spruce"),
    PINE("pine"),
    PALM("palm"),
    DUNGEON("dungeon"),
    DEADWOOD("deadwood");

     public final String string;


    FurnitureWoodType(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public static FurnitureWoodType weightedSelection(String id) {
        FurnitureWoodType[] sets = values();
        double[] weights = SetChances.getSettler(id).getWeights();

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



    public enum SetChances {
        // oak, spruce, pine, palm, dungeon, deadwood
        HUNTER(new double[]{0.25, 0.75, 0.0, 0.0, 0.0, 0.0}),
        ANGLER(new double[]{0.75, 0.25, 0.25, 0.0, 0.0, 0.0}),
        PAWNBROKER(new double[]{0.0, 0.25, 0.0, 0.0, 0.0, 0.75}),
        EXPLORER(new double[]{0.25, 0.25, 0.25, 0.25, 0.0, 0.0}),
        GUARD(new double[]{0.25, 0.25, 0.25, 0.25, 0.0, 0.0}),
        ALCHEMIST(new double[]{0.25, 0.25, 0.50, 0.0, 0.0, 0.0}),
        BLACKSMITH(new double[]{0.25, 0.75, 0.0, 0.0, 0.0, 0.0}),
        FARMER(new double[]{0.25, 0.25, 0.50, 0.75, 0.0, 0.0}),
        MAGE(new double[]{0.50, 0.50, 0.25, 0.25, 0.25, 0.25}),
        ANIMALKEEPER(new double[]{0.75, 0.25, 0.25, 0.25, 0.0, 0.0}),
        MINER(new double[]{0.25, 0.25, 0.25, 0.25, 0.0, 0.0}),
        GUNSMITH(new double[]{0.25, 0.75, 0.0, 0.0, 0.0, 0.0}),
        WITCH(new double[]{0.25, 0.0, 0.0, 0.0, 0.0, 0.75}),
        STYLIST(new double[]{0.0, 0.0, 0.10, 1.0, 0.0, 0.0}),
        ARCHITECT(new double[]{0.25, 0.25, 0.25, 0.25, 0.0, 0.10}),
        GENERIC(new double[]{0.25, 0.25, 0.25, 0.25, 0.0, 0.0});

        public final double[] weights;


        SetChances(double[] weights) {
            this.weights = weights;
        }

        public double[] getWeights() {
            return weights;
        }

        public static SetChances getSettler(String id) {
            switch (id) {
                case "hunter": return HUNTER;
                case "angler": return ANGLER;
                case "pawnbroker": return PAWNBROKER;
                case "explorer": return EXPLORER;
                case "guard": return GUARD;
                case "alchemist": return ALCHEMIST;
                case "blacksmith": return BLACKSMITH;
                case "farmer": return FARMER;
                case "mage": return MAGE;
                case "animalkeeper": return ANIMALKEEPER;
                case "miner": return MINER;
                case "stylist": return STYLIST;
                case "gunsmith": return GUNSMITH;
                case "friendlywitchhuman": return WITCH;
                case "architect": return ARCHITECT;
                default: return GENERIC;
            }
        }
    }
}

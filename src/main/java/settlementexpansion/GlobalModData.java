package settlementexpansion;

import necesse.engine.GlobalData;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;

import java.util.HashMap;

public class GlobalModData {

    public static HashMap<MainGame, ExpandedGame> expandedGameInstances = new HashMap<>();

    public static void addExpandedGame(MainGame mainGame, Client client) {
        if (!expandedGameInstances.containsKey(mainGame)) {
            expandedGameInstances.put(mainGame, new ExpandedGame(mainGame, client));
        }
    }

    public static ExpandedGame getExpandedGame(MainGame mainGame) {
        return expandedGameInstances.getOrDefault(mainGame, null);
    }

    public static MainGame getMainGame() {
        if (GlobalData.getCurrentState() instanceof MainGame) {
            return ((MainGame)GlobalData.getCurrentState());
        }
        return null;
    }
}

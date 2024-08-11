package settlementexpansion;

import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.window.GameWindow;
import settlementexpansion.manager.ExpandedGameFormManager;

public class ExpandedGame {

    private final Client client;
    public ExpandedGameFormManager formManager;
    public MainGame mainGame;
    public boolean running;

    public ExpandedGame(MainGame mainGame, Client client) {
        this.mainGame = mainGame;
        this.client = client;
        init();
    }

    public void init() {
        this.setupFormManager();
    }

    public void frameTick(TickManager tickManager) {
    }

    public void setupFormManager() {
        this.formManager = new ExpandedGameFormManager(this, this.client);
        this.formManager.setup();
    }

    public void onWindowResized(GameWindow window) {
        if (this.formManager != null) {
            this.formManager.onWindowResized(window);
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void dispose() {
        this.formManager.dispose();
    }
}

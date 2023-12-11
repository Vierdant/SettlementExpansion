package settlementexpansion;

import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
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

    public void onWindowResized() {
        if (this.formManager != null) {
            this.formManager.onWindowResized();
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

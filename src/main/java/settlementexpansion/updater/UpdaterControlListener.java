package settlementexpansion.updater;

import necesse.engine.GameEventListener;
import necesse.engine.events.ServerClientConnectedEvent;
import necesse.engine.localization.message.LocalMessage;

import java.util.concurrent.CompletableFuture;

public class UpdaterControlListener extends GameEventListener<ServerClientConnectedEvent> {

    private final UpdaterService updaterService;

    public UpdaterControlListener() {
        this.updaterService = new UpdaterService();
    }

    @Override
    public void onEvent(ServerClientConnectedEvent event) {
        CompletableFuture<Boolean> upToDate = this.updaterService.isUpToDate();

        upToDate.whenComplete((isUpToDate, throwable) -> {
            if (throwable != null) {
                return;
            }

            if (!isUpToDate) {
                String version = this.updaterService.gitCheckResult.get().getLatestRelease().getName();
                event.client.sendChatMessage(new LocalMessage("ui", "updateavailable", "version", version));
            }
        });
    }
}

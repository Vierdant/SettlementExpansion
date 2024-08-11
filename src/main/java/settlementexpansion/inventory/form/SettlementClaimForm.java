package settlementexpansion.inventory.form;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import settlementexpansion.inventory.container.SettlementClaimContainer;
import settlementexpansion.inventory.event.SettlementClaimTickEvent;

public class SettlementClaimForm extends ContainerForm<SettlementClaimContainer> {

    public SettlementClaimForm(Client client, SettlementClaimContainer container) {
        super(client, 400, 160, container);
        update(this.container.claimTick);
    }

    @Override
    protected void init() {
        super.init();
        this.container.onEvent(SettlementClaimTickEvent.class, this::update);
    }

    public void update(SettlementClaimTickEvent event) {
        this.clearComponents();
        FormFlow flow = new FormFlow(5);
        this.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementtakeover"), new FontOptions(20), 0, this.getWidth() / 2, 0, this.getWidth() - 20), 10));
        this.addComponent(flow.nextY(new FormLocalLabel("ui", "settlementtakeoverexplain", new FontOptions(16), 0, this.getWidth() / 2, 0, this.getWidth() - 20), 20));

        FormLocalTextButton confirm = this.addComponent(new FormLocalTextButton(new LocalMessage("ui", "takeoverbutton", "extra", this.container.timerTicking() ? " (" + (int)(this.container.getTimerTimeLeft() / 1000) + ")" : ""), 4, 120, this.getWidth() / 2 - 6));
        confirm.setActive(event.canClaim);
        confirm.onClicked((e) -> {
            this.playTickSound();
            this.container.forceClaim.runAndSend();
            this.client.closeContainer(true);
        });

        this.addComponent(new FormLocalTextButton("controls", "closetip", this.getWidth() / 2 + 2, 120, this.getWidth() / 2 - 6)).onClicked((e) ->
                this.client.closeContainer(true));

        GameWindow window = WindowManager.getWindow();
        this.onWindowResized(window);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosInventory(this);
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public boolean shouldOpenInventory() {
        return false;
    }
}

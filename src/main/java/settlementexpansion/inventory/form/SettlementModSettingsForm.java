package settlementexpansion.inventory.form;

import necesse.engine.GameAuth;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSubForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import settlementexpansion.inventory.container.SettlementModContainer;
import settlementexpansion.map.settlement.SettlementModData;

public class SettlementModSettingsForm<T extends SettlementModContainer> extends FormSwitcher implements SettlementSubForm {
    public final Client client;
    public final T container;
    public final SettlementModContainerForm<T> containerForm;
    protected Form name;
    protected Form settings;
    protected FormTextInput nameInput;

    public SettlementModSettingsForm(Client client, T container, SettlementModContainerForm<T> containerForm) {
        this.client = client;
        this.container = container;
        this.containerForm = containerForm;
        this.settings = this.addComponent(new Form("settings", 400, 50));
        this.name = this.addComponent(new Form("name", 400, 80));
        this.nameInput = this.name.addComponent(new FormTextInput(4, 0, FormInputSize.SIZE_32_TO_40, this.name.getWidth() - 8, 40));
        this.nameInput.placeHolder = new LocalMessage("settlement", "defname", "biome", client.getLevel().biome.getLocalization());
        String preName = container.basics.settlementName.translate();
        if (!this.nameInput.placeHolder.translate().equals(preName)) {
            this.nameInput.setText(preName);
        }

        this.nameInput.onSubmit((e) -> {
            this.playTickSound();
            this.submitName();
            this.makeCurrent(this.settings);
        });
        this.name.addComponent(new FormLocalTextButton("ui", "confirmbutton", 4, 40, this.name.getWidth() / 2 - 6)).onClicked((e) -> {
            this.playTickSound();
            this.submitName();
            this.makeCurrent(this.settings);
        });
        this.name.addComponent(new FormLocalTextButton("ui", "backbutton", this.name.getWidth() / 2 + 2, 40, this.name.getWidth() / 2 - 6)).onClicked((e) -> {
            this.makeCurrent(this.settings);
        });
        this.update(container.basics);
        this.makeCurrent(this.settings);
    }

    protected void init() {
        super.init();
        this.container.onEvent(SettlementBasicsEvent.class, this::update);
    }

    protected void update(SettlementBasicsEvent event) {
        this.settings.clearComponents();
        FormFlow flow = new FormFlow(5);
        this.settings.addComponent(flow.next(new FormLocalLabel(this.container.basics.settlementName, new FontOptions(20), 0, this.settings.getWidth() / 2, 0, this.settings.getWidth() - 20), 10));
        FormLocalTextButton changeName = this.settings.addComponent(new FormLocalTextButton("ui", "settmentchangename", 40, flow.next(40), this.settings.getWidth() - 80));
        changeName.onClicked((e) -> {
            this.makeCurrent(this.name);
        });
        changeName.setActive(event.isOwner(this.client));
        if (!changeName.isActive()) {
            changeName.setLocalTooltip(new LocalMessage("ui", event.hasOwner() ? "settlementowneronly" : "settlementclaimfirst"));
        }

        System.out.println(this.container.isPvpFlagged);

        FormLocalTextButton togglePvp = this.settings.addComponent(new FormLocalTextButton("ui", this.container.isPvpFlagged ? "settlementdisablepvp" : "settlementenablepvp", 40, flow.next(40), this.settings.getWidth() - 80));
        togglePvp.setActive(event.isOwner(this.client));
        if (!togglePvp.isActive()) {
            togglePvp.setLocalTooltip(new LocalMessage("ui", event.hasOwner() ? "settlementowneronly" : "settlementclaimfirst"));
        }
        togglePvp.onClicked((e) -> {
            this.container.togglePvpFlag.runAndSend();
        });

        FormLocalTextButton changePrivacy = this.settings.addComponent(new FormLocalTextButton("ui", event.isPrivate ? "settlementmakepub" : "settlementmakepriv", 40, flow.next(40), this.settings.getWidth() - 80));
        changePrivacy.onClicked((e) -> {
            this.container.changePrivacy.runAndSend(!this.container.basics.isPrivate);
        });
        changePrivacy.setActive(event.isOwner(this.client));
        if (!changePrivacy.isActive()) {
            changePrivacy.setLocalTooltip(new LocalMessage("ui", event.hasOwner() ? "settlementowneronly" : "settlementclaimfirst"));
        }

        FormLocalTextButton changeClaim = this.settings.addComponent(new FormLocalTextButton("ui", event.isOwner(this.client) ? "settlementunclaim" : "settlementclaim", 40, flow.next(40), this.settings.getWidth() - 80));
        changeClaim.onClicked((e) -> {
            this.container.changeClaim.runAndSend(this.container.basics.ownerAuth != GameAuth.getAuthentication());
        });
        changeClaim.setActive(!event.hasOwner() || event.isOwner(this.client));
        if (!changeClaim.isActive()) {
            changeClaim.setLocalTooltip(new LocalMessage("ui", "settlementowneronly"));
        }

        flow.next(10);
        this.settings.addComponent(new FormLocalLabel(new LocalMessage("ui", "settlementowner", "owner", this.container.basics.ownerName), new FontOptions(16), -1, 5, flow.next(20)));
        this.settings.setHeight(flow.next());
        this.onWindowResized();
        Screen.submitNextMoveEvent();
    }

    protected void submitName() {
        this.container.changeName.runAndSend(this.getCurrentNameInput());
    }

    protected GameMessage getCurrentNameInput() {
        String text = this.nameInput.getText();
        return (GameMessage)(text.isEmpty() ? this.nameInput.placeHolder : new StaticMessage(text));
    }

    public void onSetCurrent(boolean current) {
        if (current) {
            this.makeCurrent(this.settings);
        }

    }

    public void onWindowResized() {
        super.onWindowResized();
        ContainerComponent.setPosInventory(this.settings);
        this.name.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
    }

    public GameMessage getMenuButtonName() {
        return new LocalMessage("ui", "settlementsettings");
    }

    public String getTypeString() {
        return "settings";
    }
}

package settlementexpansion.inventory.form;

import necesse.engine.GameAuth;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
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

import java.awt.*;

public class SettlementModSettingsForm<T extends SettlementModContainer> extends FormSwitcher implements SettlementSubForm {
    public final Client client;
    public final T container;
    public final SettlementModContainerForm<T> containerForm;
    protected Form name;
    protected Form unclaim;
    protected Form destroy;
    protected Form pvp;
    protected Form settings;
    protected FormTextInput nameInput;

    public SettlementModSettingsForm(Client client, T container, SettlementModContainerForm<T> containerForm) {
        this.client = client;
        this.container = container;
        this.containerForm = containerForm;
        this.settings = this.addComponent(new Form("settings", 400, 60));
        this.name = this.addComponent(new Form("name", 400, 80));
        this.unclaim = this.addComponent(new Form("unclaim", 400, 140));
        this.destroy = this.addComponent(new Form("destroy", 400, 140));
        this.pvp = this.addComponent(new Form("pvp", 400, 140));
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
        this.name.addComponent(new FormLocalTextButton("ui", "backbutton", this.name.getWidth() / 2 + 2, 40, this.name.getWidth() / 2 - 6)).onClicked((e) ->
                this.makeCurrent(this.settings));


        FormFlow flow = new FormFlow(5);
        this.unclaim.addComponent(flow.nextY(new FormLocalLabel("ui", "settlementconfirmnotice", new FontOptions(20), 0, this.unclaim.getWidth() / 2, 0, this.unclaim.getWidth() - 20), 10));
        this.unclaim.addComponent(flow.nextY(new FormLocalLabel("ui", "settlementunclaimexplain", new FontOptions(16), 0, this.unclaim.getWidth() / 2, 0, this.unclaim.getWidth() - 20), 20));

        this.unclaim.addComponent(new FormLocalTextButton("ui", "confirmbutton", 4, 100, this.unclaim.getWidth() / 2 - 6)).onClicked((e) -> {
            this.playTickSound();
            this.container.changeClaim.runAndSend(this.container.basics.ownerAuth != GameAuth.getAuthentication());
            this.makeCurrent(this.settings);
        });
        this.unclaim.addComponent(new FormLocalTextButton("ui", "backbutton", this.unclaim.getWidth() / 2 + 2, 100, this.unclaim.getWidth() / 2 - 6)).onClicked((e) ->
                this.makeCurrent(this.settings));

        flow = new FormFlow(5);
        this.destroy.addComponent(flow.nextY(new FormLocalLabel("ui", "settlementconfirmnotice", new FontOptions(20), 0, this.destroy.getWidth() / 2, 0, this.destroy.getWidth() - 20), 10));
        this.destroy.addComponent(flow.nextY(new FormLocalLabel("ui", "settlementdestroyexplain", new FontOptions(16), 0, this.destroy.getWidth() / 2, 0, this.destroy.getWidth() - 20), 20));

        this.destroy.addComponent(new FormLocalTextButton("ui", "settlementdestroybutton", 4, 100, this.destroy.getWidth() / 2 - 6)).onClicked((e) -> {
            this.playTickSound();
            this.container.destroyFlag.runAndSend();
            this.makeCurrent(this.settings);
        });
        this.destroy.addComponent(new FormLocalTextButton("ui", "backbutton", this.destroy.getWidth() / 2 + 2, 100, this.destroy.getWidth() / 2 - 6)).onClicked((e) ->
                this.makeCurrent(this.settings));

        flow = new FormFlow(5);
        this.pvp.addComponent(flow.nextY(new FormLocalLabel("ui", "settlementconfirmnotice", new FontOptions(20), 0, this.pvp.getWidth() / 2, 0, this.pvp.getWidth() - 20), 10));
        this.pvp.addComponent(flow.nextY(new FormLocalLabel("ui", "settlementpvpexplain", new FontOptions(16), 0, this.pvp.getWidth() / 2, 0, this.pvp.getWidth() - 20), 20));

        this.pvp.addComponent(new FormLocalTextButton("ui", "confirmbutton", 4, 100, this.pvp.getWidth() / 2 - 6)).onClicked((e) -> {
            this.playTickSound();
            this.container.togglePvpFlag.runAndSend();
            this.makeCurrent(this.settings);
        });
        this.pvp.addComponent(new FormLocalTextButton("ui", "backbutton", this.pvp.getWidth() / 2 + 2, 100, this.pvp.getWidth() / 2 - 6)).onClicked((e) ->
                this.makeCurrent(this.settings));
        this.update(container.basics);
        this.makeCurrent(this.settings);
    }

    protected void init() {
        super.init();
        this.container.onEvent(SettlementBasicsEvent.class, this::update);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        super.draw(tickManager, perspective, renderBox);
        if (this.container.flagObjectEntity.onCooldown()) {
            update(this.container.basics);
        }
    }

    protected void update(SettlementBasicsEvent event) {
        this.settings.clearComponents();
        FormFlow flow = new FormFlow(5);
        this.settings.addComponent(flow.nextY(new FormLocalLabel(this.container.basics.settlementName, new FontOptions(20), 0, this.settings.getWidth() / 2, 0, this.settings.getWidth() - 20), 10));
        FormLocalTextButton changeName = this.settings.addComponent(new FormLocalTextButton("ui", "settmentchangename", 40, flow.next(40), this.settings.getWidth() - 80));
        changeName.onClicked((e) -> this.makeCurrent(this.name));
        changeName.setActive(event.isOwner(this.client));
        if (!changeName.isActive()) {
            changeName.setLocalTooltip(new LocalMessage("ui", event.hasOwner() ? "settlementowneronly" : "settlementclaimfirst"));
        }

        FormLocalTextButton togglePvp = this.settings.addComponent(new FormLocalTextButton("ui", this.container.isPvpFlagged ? "settlementdisablepvp" : "settlementenablepvp", 40, flow.next(40), this.settings.getWidth() - 80));
        togglePvp.setActive(event.isOwner(this.client) && !this.container.flagObjectEntity.onCooldown() && determinePvpSafetyCondition());
        if (!togglePvp.isActive()) {
            if (!this.container.settlementSafe) {
                togglePvp.setLocalTooltip(new LocalMessage("ui", "settlementunsafe"));
            } else if (this.container.flagObjectEntity.onCooldown() && event.hasOwner()) {
                togglePvp.setLocalTooltip(new LocalMessage("ui", "settlementactioncooldown", "time", this.container.flagObjectEntity.getCooldownTimeLeft() / 1000L));
            } else {
                togglePvp.setLocalTooltip(new LocalMessage("ui", event.hasOwner() ? "settlementowneronly" : "settlementclaimfirst"));
            }
        }
        togglePvp.onClicked((e) -> {
            if (!this.container.isPvpFlagged) {
                this.makeCurrent(this.pvp);
            } else {
                this.container.togglePvpFlag.runAndSend();
            }
        });

        FormLocalTextButton changePrivacy = this.settings.addComponent(new FormLocalTextButton("ui", event.isPrivate ? "settlementmakepub" : "settlementmakepriv", 40, flow.next(40), this.settings.getWidth() - 80));
        changePrivacy.onClicked((e) ->
                this.container.changePrivacy.runAndSend(!this.container.basics.isPrivate));
        changePrivacy.setActive(event.isOwner(this.client) && this.container.settlementSafe);
        if (!changePrivacy.isActive()) {
            if (!this.container.settlementSafe) {
                changePrivacy.setLocalTooltip(new LocalMessage("ui", "settlementunsafe"));
            } else {
                changePrivacy.setLocalTooltip(new LocalMessage("ui", event.hasOwner() ? "settlementowneronly" : "settlementclaimfirst"));
            }
        }

        FormLocalTextButton changeClaim = this.settings.addComponent(new FormLocalTextButton("ui", event.isOwner(this.client) ? "settlementunclaim" : "settlementclaim", 40, flow.next(40), this.settings.getWidth() - 80));
        changeClaim.onClicked((e) -> {
            if (event.hasOwner()) {
                this.makeCurrent(this.unclaim);
            } else {
                this.container.changeClaim.runAndSend(this.container.basics.ownerAuth != GameAuth.getAuthentication());
            }

        });
        changeClaim.setActive((!event.hasOwner() || event.isOwner(this.client)) && this.container.settlementSafe);
        if (!changeClaim.isActive()) {
             if (!this.container.settlementSafe) {
                 changeClaim.setLocalTooltip(new LocalMessage("ui", "settlementunsafe"));
            } else {
                 changeClaim.setLocalTooltip(new LocalMessage("ui", "settlementowneronly"));
            }
        }

        FormLocalTextButton destroyFlag = this.settings.addComponent(new FormLocalTextButton("ui", "settlementdestroyflag", 40, flow.next(40), this.settings.getWidth() - 80));
        destroyFlag.onClicked((e) -> {
            if (event.hasOwner()) {
                this.makeCurrent(this.destroy);
            } else {
                this.container.destroyFlag.runAndSend();
            }

        });
        destroyFlag.setActive((!event.hasOwner() || event.isOwner(this.client)) && this.container.settlementSafe);
        if (!destroyFlag.isActive()) {
            if (!this.container.settlementSafe) {
                destroyFlag.setLocalTooltip(new LocalMessage("ui", "settlementunsafe"));
            } else {
                destroyFlag.setLocalTooltip(new LocalMessage("ui", "settlementowneronly"));
            }

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
        return (text.isEmpty() ? this.nameInput.placeHolder : new StaticMessage(text));
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
        this.unclaim.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
        this.destroy.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
        this.pvp.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
    }

    public boolean determinePvpSafetyCondition() {
        if (this.container.isPvpFlagged && this.container.settlementSafe) {
            return true;
        } else return !this.container.isPvpFlagged;
    }

    public GameMessage getMenuButtonName() {
        return new LocalMessage("ui", "settlementsettings");
    }

    public String getTypeString() {
        return "settings";
    }
}

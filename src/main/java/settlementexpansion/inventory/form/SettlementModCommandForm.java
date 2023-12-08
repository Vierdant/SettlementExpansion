package settlementexpansion.inventory.form;

import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSubForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.data.SettlementSettlerBasicData;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;
import settlementexpansion.inventory.container.SettlementModContainer;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class SettlementModCommandForm<T extends SettlementModContainer> extends FormSwitcher implements SettlementSubForm {
    public final Client client;
    public final T container;
    public final SettlementModContainerForm<T> containerForm;
    protected Form noneSelectedForm;
    protected Form selectedForm;

    public SettlementModCommandForm(Client client, T container, SettlementModContainerForm<T> containerForm) {
        this.client = client;
        this.container = container;
        this.containerForm = containerForm;
        this.noneSelectedForm = this.addComponent(new Form(300, 200));
        FormFlow noneFlow = new FormFlow(4);
        this.noneSelectedForm.addComponent(noneFlow.next(new FormLocalLabel("ui", "settlementcommand", new FontOptions(20), 0, this.noneSelectedForm.getWidth() / 2, 0), 4));
        this.noneSelectedForm.addComponent(noneFlow.next(new FormLocalLabel("ui", "settlementcommandtip", new FontOptions(16), 0, this.noneSelectedForm.getWidth() / 2, this.noneSelectedForm.getWidth() / 2, this.noneSelectedForm.getWidth() - 20), 8));
        (this.noneSelectedForm.addComponent(noneFlow.next(new FormLocalTextButton("ui", "settlementcommandall", 4, 0, this.noneSelectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4))).onClicked((e) -> {
            synchronized(containerForm.selectedSettlers) {

                for (SettlementSettlerBasicData settler : containerForm.settlers) {
                    containerForm.selectedSettlers.add(settler.mobUniqueID);
                }
            }

            this.updateSelectedForm();
        });
        (this.noneSelectedForm.addComponent(noneFlow.next(new FormLocalTextButton("ui", "settlementcommandclearall", 4, 0, this.noneSelectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4))).onClicked((e) -> {
            HashSet<Integer> mobUniqueIDs = new HashSet<>();

            for (SettlementSettlerBasicData settler : containerForm.settlers) {
                mobUniqueIDs.add(settler.mobUniqueID);
            }

            container.commandSettlersClearOrders.runAndSend(mobUniqueIDs);
        });
        this.noneSelectedForm.setHeight(noneFlow.next());
        this.selectedForm = this.addComponent(new Form(300, 200));
    }

    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleInputEvent(event, tickManager, perspective);
        if (!this.containerForm.selectedSettlers.isEmpty() && event.getID() == 256) {
            if (!event.state) {
                synchronized(this.containerForm.selectedSettlers) {
                    this.containerForm.selectedSettlers.clear();
                    this.updateCurrentForm();
                }
            }

            event.use();
        }

    }

    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleControllerEvent(event, tickManager, perspective);
        if (!this.containerForm.selectedSettlers.isEmpty() && event.getState() == ControllerInput.MENU_BACK) {
            if (!event.buttonState) {
                synchronized(this.containerForm.selectedSettlers) {
                    this.containerForm.selectedSettlers.clear();
                    this.updateCurrentForm();
                }
            }

            event.use();
        }

    }

    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updateCurrentForm();
        super.draw(tickManager, perspective, renderBox);
    }

    public void onSetCurrent(boolean current) {
        if (current) {
            this.updateCurrentForm();
        }

    }

    public void updateCurrentForm() {
        if (this.containerForm.selectedSettlers.isEmpty()) {
            if (!this.isCurrent(this.noneSelectedForm)) {
                this.makeCurrent(this.noneSelectedForm);
            }
        } else if (!this.isCurrent(this.selectedForm)) {
            this.updateSelectedForm();
            this.makeCurrent(this.selectedForm);
        }

    }

    public void updateSelectedForm() {
        this.selectedForm.clearComponents();
        FormFlow flow = new FormFlow(4);
        this.selectedForm.addComponent(flow.next(new FormLocalLabel("ui", "settlementcommand", new FontOptions(20), 0, this.selectedForm.getWidth() / 2, 0), 4));
        ArrayList<CommandMob> mobs = new ArrayList<>(this.containerForm.selectedSettlers.size());
        synchronized(this.containerForm.selectedSettlers) {

            for (int uniqueID : this.containerForm.selectedSettlers) {

                Mob mob = this.client.getLevel().entityManager.mobs.get(uniqueID, false);
                if (mob instanceof CommandMob) {
                    mobs.add((CommandMob) mob);
                }
            }
        }

        GameMessage subtitle;
        if (mobs.size() == 1) {
            Mob mob = (Mob)mobs.get(0);
            subtitle = mob.getLocalization();
        } else {
            subtitle = new LocalMessage("ui", "settlementcommandselected", "count", mobs.size());
        }

        this.selectedForm.addComponent(flow.next(new FormLocalLabel(subtitle, new FontOptions(16), 0, this.selectedForm.getWidth() / 2, this.selectedForm.getWidth() / 2, this.selectedForm.getWidth() - 20), 8));
        boolean allHideInside = mobs.stream().allMatch(CommandMob::getHideOnLowHealth);
        this.selectedForm.addComponent(flow.next(new FormLocalCheckBox("ui", "settlementcommandhidelowhealth", 4, 0, allHideInside, this.selectedForm.getWidth() - 8), 4)).onClicked((e) -> {
            mobs.forEach((m) -> m.setHideOnLowHealth(e.from.checked));
            synchronized(this.containerForm.selectedSettlers) {
                this.container.commandSettlersSetHideOnLowHealth.runAndSend(this.containerForm.selectedSettlers, e.from.checked);
            }
        });
        this.selectedForm.addComponent(flow.next(new FormLocalTextButton("ui", "settlementcommandfollow", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4)).onClicked((e) -> {
            synchronized(this.containerForm.selectedSettlers) {
                this.container.commandSettlersFollow.runAndSend(this.containerForm.selectedSettlers);
            }
        });
        this.selectedForm.addComponent(flow.next(new FormLocalTextButton("ui", "settlementcommandclear", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4)).onClicked((e) -> {
            synchronized(this.containerForm.selectedSettlers) {
                this.container.commandSettlersClearOrders.runAndSend(this.containerForm.selectedSettlers);
                this.containerForm.selectedSettlers.clear();
                this.updateCurrentForm();
            }
        });
        this.selectedForm.addComponent(flow.next(new FormLocalTextButton("ui", "cancelbutton", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4)).onClicked((e) -> {
            synchronized(this.containerForm.selectedSettlers) {
                this.containerForm.selectedSettlers.clear();
                this.updateCurrentForm();
            }
        });
        this.selectedForm.setHeight(flow.next());
        ContainerComponent.setPosInventory(this.selectedForm);
    }

    public void onMenuButtonClicked(FormSwitcher switcher) {
        SettlementSubForm.super.onMenuButtonClicked(switcher);
        this.updateCurrentForm();
    }

    public void onWindowResized() {
        super.onWindowResized();
        ContainerComponent.setPosInventory(this.noneSelectedForm);
        ContainerComponent.setPosInventory(this.selectedForm);
    }

    public GameMessage getMenuButtonName() {
        return new LocalMessage("ui", "settlementcommand");
    }

    public String getTypeString() {
        return "command";
    }
}

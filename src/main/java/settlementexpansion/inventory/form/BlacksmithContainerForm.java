package settlementexpansion.inventory.form;

import necesse.engine.Settings;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.*;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import settlementexpansion.inventory.container.BlacksmithContainer;
import settlementexpansion.inventory.slots.FormContainerGeodeSlot;
import settlementexpansion.entity.mob.friendly.BlacksmithModHumanMob;

import java.awt.*;

public class BlacksmithContainerForm<T extends BlacksmithContainer> extends ShopContainerForm<T> {
    public Form breakForm;
    public FormLabel costText;
    public FormLocalTextButton breakButton;
    public FormLocalLabel costLabel;
    public FormItemPreview preview;

    public BlacksmithContainerForm(Client client, T container, int width, int height, int maxExpeditionsHeight) {
        super(client, container, width, height, maxExpeditionsHeight);
        FormFlow heightFlow = new FormFlow(5);

        this.breakForm = this.addComponent(new Form("breakgeode", width, height), (form, active) -> {
            container.setIsBreakingGeode.runAndSend(active);
        });

        this.breakForm.addComponent(new FormLocalTextButton("ui", "backbutton", this.breakForm.getWidth() - 104, 4, 100, FormInputSize.SIZE_20, ButtonColor.BASE)).onClicked((e) -> {
            this.makeCurrent(this.dialogueForm);
        });
        this.breakForm.addComponent(new FormLocalLabel("ui", "blacksmithbreak", new FontOptions(20), -1, 4, heightFlow.next(40)));
        int geodeSlotY = heightFlow.next(50);
        this.breakForm.addComponent(new FormContainerGeodeSlot(client, container.GEODE_SLOT, 40, geodeSlotY));
        this.breakForm.addComponent(new FormContainerSlot(client, container.RESULT_SLOT, 250, geodeSlotY));
        this.breakButton = this.breakForm.addComponent(new FormLocalTextButton("ui", "blacksmithbreakconfirm", 90, geodeSlotY + 10, 150, FormInputSize.SIZE_24, ButtonColor.BASE));
        this.breakButton.onClicked((e) -> {
            container.breakGeodeButton.runAndSend(this.container.quickTransferToggled);
        });
        this.costLabel = this.breakForm.addComponent(new FormLocalLabel("ui", "blacksmithcost", new FontOptions(16), -1, 310, geodeSlotY - 4));
        this.preview = this.breakForm.addComponent(new FormItemPreview(300, geodeSlotY + 10, "coin"));
        this.costText = this.breakForm.addComponent(new FormLabel("x " + container.getBreakCost(), new FontOptions(16), -1, this.preview.getX() + 30, geodeSlotY + 20));
        this.breakForm.addComponent(heightFlow.next((new FormFairTypeLabel(new LocalMessage("ui", "blacksmithbreaktip"), this.breakForm.getWidth() / 2, 0)).setFontOptions(new FontOptions(16)).setTextAlign(FairType.TextAlign.CENTER).setMaxWidth(this.breakForm.getWidth() - 20), 10));
        if (container.blacksmithMob.isSettler()) {
            GameMessageBuilder happinessDescription = (new GameMessageBuilder()).append(Settler.getMood(container.settlerHappiness).getDescription()).append(" (").append(container.settlerHappiness >= 0 ? "+" : "").append(Integer.toString(container.settlerHappiness)).append(")");
            this.breakForm.addComponent(heightFlow.next((new FormFairTypeLabel(happinessDescription, this.breakForm.getWidth() / 2, 0)).setFontOptions(new FontOptions(16)).setTextAlign(FairType.TextAlign.CENTER).setMaxWidth(this.breakForm.getWidth() - 20), 5));
            FormContentIconButton helpIcon = this.breakForm.addComponent(new FormContentIconButton(this.breakForm.getWidth() / 2 - 10, heightFlow.next(30), FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_help_20, new LocalMessage("ui", "blacksmithbreakfocustip")));
            helpIcon.handleClicksIfNoEventHandlers = true;
        }

        FormCheckBox checkTransferToggle = this.breakForm.addComponent(new FormLocalCheckBox("ui", "blacksmithquicktransfer", 5, heightFlow.next(20)));
        checkTransferToggle.checked = this.container.quickTransferToggled;
        checkTransferToggle.onClicked((e) -> {
            this.container.setQuickTransferToggle(e.from.checked);
        });

        this.breakForm.setHeight(heightFlow.next());
        this.updateBreakActive();
    }

    public BlacksmithContainerForm(Client client, T container) {
        this(client, container, 408, 170, 240);
    }

    public void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener() {
            public void onChange(Language language) {
                preview.setX(costLabel.getX() + costLabel.getBoundingBox().width);
                costText.setX(preview.getX() + 30);
            }

            public boolean isDisposed() {
                return BlacksmithContainerForm.this.isDisposed();
            }
        });
    }

    public void setupExtraDialogueOptions() {
        super.setupExtraDialogueOptions();
        if (this.container.humanShop instanceof BlacksmithModHumanMob && this.container.items != null) {
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "blacksmithwantbreakgeode"), () -> {
                this.makeCurrent(this.breakForm);
            });
        }
    }

    private void updateBreakActive() {
        this.costText.setText("x " + this.container.getBreakCost());
        this.breakButton.setActive(this.container.canBreak());
    }

    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.isCurrent(this.breakForm)) {
            this.updateBreakActive();
        }

        super.draw(tickManager, perspective, renderBox);
    }

    public void onWindowResized() {
        super.onWindowResized();
        ContainerComponent.setPosFocus(this.breakForm);
    }
}

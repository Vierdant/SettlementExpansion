package settlementexpansion.inventory.form;

import necesse.engine.Screen;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementObjectStatusFormManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.*;
import necesse.gfx.ui.ButtonColor;
import settlementexpansion.inventory.container.BlueprintContainer;

import java.awt.*;

public class BlueprintContainerForm<T extends BlueprintContainer> extends ContainerFormSwitcher<T> {
    public Form buildForm;
    public SettlementObjectStatusFormManager settlementObjectFormManager;
    public FormLocalTextButton buildButton;

    public BlueprintContainerForm(Client client, T container) {
        super(client, container);
        this.buildForm = this.addComponent(new Form(400, 60));
        FormFlow heightFlow = new FormFlow(5);
        this.buildForm.addComponent(heightFlow.next(new FormLocalLabel(container.objectEntity.getObject().getLocalization(), new FontOptions(16), 0, this.buildForm.getWidth() / 2, 5), 5));
        this.buildButton = this.buildForm.addComponent(heightFlow.next(new FormLocalTextButton("ui", "blueprintbuildconfirm", this.buildForm.getWidth() / 4, this.buildForm.getHeight() / 2, 200, FormInputSize.SIZE_24, ButtonColor.BASE), 50));
        this.buildButton.onClicked((e) -> {
            container.buildAction.runAndSend();
        });

        this.settlementObjectFormManager = container.settlementObjectManager.getFormManager(this, this.buildForm, client);
        this.makeCurrent(this.buildForm);
        this.updateBuildActive();
    }

    public boolean shouldOpenInventory() {
        return true;
    }

    public void onWindowResized() {
        super.onWindowResized();
        ContainerComponent.setPosFocus(this.buildForm);
        this.settlementObjectFormManager.onWindowResized();
    }

    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.settlementObjectFormManager.updateButtons();
        updateBuildActive();
        super.draw(tickManager, perspective, renderBox);
        if (this.buildButton.isHovering()) {
            ListGameTooltips tooltips = new ListGameTooltips();
            tooltips.add(this.container.objectEntity.getPreset().getRecipe().getTooltip(this.client.getPlayer()));
            Screen.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
        }
    }

    private void updateBuildActive() {
        this.buildButton.setActive(this.container.objectEntity.getPreset().getRecipe().canBuild(this.client.getPlayer()));
    }
}

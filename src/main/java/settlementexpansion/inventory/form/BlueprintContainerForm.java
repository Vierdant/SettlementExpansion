package settlementexpansion.inventory.form;

import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.lists.FormContainerCraftingList;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.object.CraftingStationContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementObjectStatusFormManager;
import necesse.gfx.gameTooltips.*;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.level.maps.hudManager.HudDrawElement;
import settlementexpansion.inventory.container.BlueprintContainer;

import java.awt.*;
import java.util.List;

public class BlueprintContainerForm<T extends BlueprintContainer> extends ContainerFormSwitcher<T> {
    public Form buildForm;
    public SettlementObjectStatusFormManager settlementObjectFormManager;
    public FormLocalTextButton buildButton;

    public BlueprintContainerForm(Client client, T container) {
        super(client, container);
        this.buildForm = this.addComponent(new Form(400, 80));
        this.buildButton = this.buildForm.addComponent(new FormLocalTextButton("ui", "blueprintbuildconfirm", this.buildForm.getWidth() / 4, this.buildForm.getHeight() / 2, 200, FormInputSize.SIZE_24, ButtonColor.BASE));
        this.buildButton.onClicked((e) -> {
            container.buildAction.runAndSend();
        });

        this.settlementObjectFormManager = container.settlementObjectManager.getFormManager(this, this.buildForm, client);
        this.makeCurrent(this.buildForm);
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
            tooltips.add(this.container.objectEntity.getPreset().recipe.getTooltip(this.client.getPlayer()));
            Screen.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
        }
    }

    private void updateBuildActive() {
        this.buildButton.setActive(this.container.objectEntity.getPreset().recipe.canCraft(this.client.getPlayer()));
    }
}

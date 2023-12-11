package settlementexpansion.inventory.form;

import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.lists.FormStringSelectList;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementObjectStatusFormManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.*;
import necesse.gfx.ui.ButtonColor;
import settlementexpansion.GlobalModData;
import settlementexpansion.inventory.container.BlueprintContainer;
import settlementexpansion.map.preset.BlueprintHelper;
import settlementexpansion.registry.ObjectModRegistry;

import java.awt.*;

public class BlueprintContainerForm<T extends BlueprintContainer> extends ContainerFormSwitcher<T> {
    public Form buildForm;
    public SettlementObjectStatusFormManager settlementObjectFormManager;
    public FormStringSelectList woodTypes;
    public FormStringSelectList wallTypes;
    public FormStringSelectList floorTypes;
    public FormLocalTextButton buildButton;

    public BlueprintContainerForm(Client client, T container) {
        super(client, container);
        int height = 60;
        if (container.objectEntity.getPreset().canChangeWalls) height += 60;
        if (container.objectEntity.getPreset().canChangeFloor) height += 60;
        if (container.objectEntity.getPreset().isFurnished()) height += 60;
        this.buildForm = this.addComponent(new Form(400, height));
        FormFlow heightFlow = new FormFlow(7);
        this.buildForm.addComponent(heightFlow.nextY(new FormLocalLabel(container.objectEntity.getObject().getLocalization(), new FontOptions(16), 0, this.buildForm.getWidth() / 2, 5), 5));
        this.buildForm.addComponent(GlobalModData.getExpandedGame(GlobalModData.getMainGame()).formManager.getBlueprintButton(this.container.objectEntity, this.buildForm.getWidth() - 40, heightFlow.next() - 20));

        if (container.objectEntity.getPreset().canChangeWalls) {
            height = heightFlow.next(60);
            this.buildForm.addComponent(new FormLocalLabel(new LocalMessage("ui", "blueprintwalls"), new FontOptions(16), 0, this.buildForm.getWidth() / 4, height + 18));
            this.wallTypes = this.buildForm.addComponent(new FormStringSelectList(this.buildForm.getWidth() / 4, height, 200, 50, BlueprintHelper.wallTypes));
            this.wallTypes.setSelected(0);
            this.wallTypes.onSelect((e) ->
                    container.setWallType.runAndSend(e.str));
        }

        if (container.objectEntity.getPreset().canChangeWalls) {
            height = heightFlow.next(60);
            this.buildForm.addComponent(new FormLocalLabel(new LocalMessage("ui", "blueprintfloor"), new FontOptions(16), 0, this.buildForm.getWidth() / 4, height + 18));
            this.floorTypes = this.buildForm.addComponent(new FormStringSelectList(this.buildForm.getWidth() / 4, height, 200, 50, BlueprintHelper.floorTypes));
            this.floorTypes.setSelected(0);
            this.floorTypes.onSelect((e) ->
                    container.setFloorType.runAndSend(e.str));
        }

        if (container.objectEntity.getPreset().isFurnished()) {
            height = heightFlow.next(60);
            this.buildForm.addComponent(new FormLocalLabel(new LocalMessage("ui", "blueprintfurniture"), new FontOptions(16), 0, this.buildForm.getWidth() / 4, height + 18));
            this.woodTypes = this.buildForm.addComponent(new FormStringSelectList(this.buildForm.getWidth() / 4, height, 200, 50, ObjectModRegistry.woodFurnitureTypes));
            this.woodTypes.setSelected(1);
            this.woodTypes.onSelect((e) ->
                    container.setWoodType.runAndSend(e.str));
        }

        int buttonY = heightFlow.next(30);
        this.buildButton = this.buildForm.addComponent(new FormLocalTextButton("ui", "blueprintbuildconfirm", this.buildForm.getWidth() / 4, buttonY, 200, FormInputSize.SIZE_24, ButtonColor.BASE), 10);
        this.buildButton.onClicked((e) ->
                container.buildAction.runAndSend());

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
            tooltips.add(new LocalMessage("ui", "blueprintrecipeheader"));
            tooltips.add(this.container.objectEntity.getPreset().getRecipe().getTooltip(this.client.getPlayer()));
            Screen.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
        }
    }

    private void updateBuildActive() {
        this.buildButton.setActive(this.container.objectEntity.getPreset().getRecipe().canBuild(this.client.getPlayer()));
    }
}

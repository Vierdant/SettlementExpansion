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
    protected FormLocalCheckBox useNearby;
    protected HudDrawElement rangeElement;

    public BlueprintContainerForm(Client client, T container) {
        super(client, container);
        this.buildForm = this.addComponent(new Form(400, 80));
        this.buildButton = this.buildForm.addComponent(new FormLocalTextButton("ui", "blueprintbuildconfirm", this.buildForm.getWidth() / 2, this.buildForm.getHeight() / 2, 200, FormInputSize.SIZE_24, ButtonColor.BASE));
        this.buildButton.onClicked((e) -> {
            container.buildAction.runAndSend();
        });

        this.useNearby = this.buildForm.addComponent(new FormLocalCheckBox("ui", "usenearbyinv", 5, this.buildForm.getHeight() - 16 - 4, Settings.craftingUseNearby.get()) {
            public GameTooltips getTooltip() {
                return (new StringTooltips()).add(Localization.translate("ui", "usenearbyinvtip"), 400);
            }
        }, 100);
        Settings.craftingUseNearby.addChangeListener((v) -> {
            this.useNearby.checked = v;
            GlobalData.updateCraftable();
        }, this::isDisposed);
        this.useNearby.onClicked((e) -> {
            Settings.craftingUseNearby.set((e.from).checked);
        });

        this.settlementObjectFormManager = container.settlementObjectManager.getFormManager(this, this.buildForm, client);
        updateBuildActive();
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

    protected void init() {
        super.init();
        if (this.rangeElement != null) {
            this.rangeElement.remove();
        }

        this.rangeElement = new HudDrawElement() {
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                if (BlueprintContainerForm.this.useNearby.isHovering()) {
                    final SharedTextureDrawOptions options = (BlueprintContainerForm.this.container).range.getDrawOptions(new Color(255, 255, 255, 200), new Color(255, 255, 255, 75), (BlueprintContainerForm.this.container).objectEntity.getTileX(), (BlueprintContainerForm.this.container).objectEntity.getTileY(), camera);
                    if (options != null) {
                        list.add(new SortedDrawable() {
                            public int getPriority() {
                                return -1000000;
                            }

                            public void draw(TickManager tickManager) {
                                options.draw();
                            }
                        });
                    }

                }
            }
        };

        this.client.getLevel().hudManager.addElement(this.rangeElement);
    }


    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.settlementObjectFormManager.updateButtons();
        super.draw(tickManager, perspective, renderBox);
        if (this.buildButton.isHovering()) {
            ListGameTooltips tooltips = new ListGameTooltips();
            tooltips.add(this.container.objectEntity.getPreset().recipe.getTooltip(null, perspective));
            Screen.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
        }
    }

    private void updateBuildActive() {
        this.buildButton.setActive(this.container.objectEntity.getPreset().recipe.canCraft(this.client.getLevel(), this.client.getPlayer(), this.getContainer().getCraftInventories(), true).canCraft());
    }

    public void dispose() {
        super.dispose();
        if (this.rangeElement != null) {
            this.rangeElement.remove();
        }

    }
}

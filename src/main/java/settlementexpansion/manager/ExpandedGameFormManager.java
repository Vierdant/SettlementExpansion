package settlementexpansion.manager;

import necesse.engine.input.InputPosition;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.ui.ButtonColor;
import settlementexpansion.ExpandedGame;
import settlementexpansion.ModResources;
import settlementexpansion.inventory.form.CurrentBlueprintRecipeForm;
import settlementexpansion.object.entity.BlueprintObjectEntity;

import java.awt.*;

public class ExpandedGameFormManager {

    private final Client client;
    private final ExpandedGame expanded;
    private boolean lastShowToolbar;
    private boolean lastInventoryExtended;
    private boolean lastIsDead;
    private boolean lastShowMap;
    private boolean lastIsRunning;
    public CurrentBlueprintRecipeForm currentBlueprintForm;
    private static Point lastBlueprintFormPos;
    private BlueprintObjectEntity lastBlueprintObjectEntity;

    public ExpandedGameFormManager(ExpandedGame expanded, Client client) {
        this.expanded = expanded;
        this.client = client;
    }

    public void updateActive(boolean forceUpdate) {
        PlayerMob player = this.client.getPlayer();
        ContainerComponent<?> focus = this.expanded.mainGame.formManager.focus;
        boolean invExtended = player.isInventoryExtended() && (focus == null || focus.shouldShowInventory());
        boolean showToolbar = focus == null || focus.shouldShowToolbar();
        boolean isDead = this.client.isDead;
        boolean showMap = this.expanded.mainGame.showMap();
        boolean isRunning = this.expanded.isRunning();

        if (forceUpdate || this.lastShowToolbar != showToolbar || this.lastInventoryExtended != invExtended || this.lastIsDead != isDead || this.lastShowMap != showMap || this.lastIsRunning != isRunning) {
            this.lastShowToolbar = showToolbar;
            this.lastInventoryExtended = invExtended;
            this.lastIsDead = isDead;
            this.lastShowMap = showMap;
            this.lastIsRunning = isRunning;

            if (this.currentBlueprintForm != null) {
                this.currentBlueprintForm.setHidden(!isRunning || showMap);
            }
        }
    }

    public void setup() {
        PlayerMob player = this.client.getPlayer();
        if (lastBlueprintFormPos != null && this.lastBlueprintObjectEntity != null) {
            this.currentBlueprintForm = this.expanded.mainGame.formManager.addComponent(new CurrentBlueprintRecipeForm(this.lastBlueprintObjectEntity) {
                public void onRemove() {
                    ExpandedGameFormManager.this.expanded.mainGame.formManager.removeComponent(this);
                    ExpandedGameFormManager.this.currentBlueprintForm = null;
                    ExpandedGameFormManager.this.lastBlueprintObjectEntity = null;
                    ExpandedGameFormManager.lastBlueprintFormPos = null;
                }
            });
            this.currentBlueprintForm.setPosition(lastBlueprintFormPos.x, lastBlueprintFormPos.y);
            this.currentBlueprintForm.update(player);
        }
    }

    public FormContentIconButton getBlueprintButton(BlueprintObjectEntity objectEntity, int x, int y) {
        this.lastBlueprintObjectEntity = objectEntity;
        FormContentIconButton button = new FormContentIconButton(x, y, FormInputSize.SIZE_32, ButtonColor.BASE, ModResources.pin_blueprint_icon, new LocalMessage("ui", "blueprintpinbutton"));
        button.onClicked((e) -> {
            if (this.currentBlueprintForm != null && this.currentBlueprintForm.objectEntity == objectEntity) {
                this.expanded.mainGame.formManager.removeComponent(this.currentBlueprintForm);
                this.currentBlueprintForm = null;
                lastBlueprintFormPos = null;
                lastBlueprintObjectEntity = null;
            } else if (this.currentBlueprintForm != null) {
                this.currentBlueprintForm.setObjectEntity(objectEntity);
            } else {
                this.currentBlueprintForm = this.expanded.mainGame.formManager.addComponent(new CurrentBlueprintRecipeForm(objectEntity) {
                    public void onRemove() {
                        ExpandedGameFormManager.this.expanded.mainGame.formManager.removeComponent(this);
                        ExpandedGameFormManager.this.currentBlueprintForm = null;
                        ExpandedGameFormManager.this.lastBlueprintObjectEntity = null;
                        ExpandedGameFormManager.lastBlueprintFormPos = null;
                    }
                });
                this.currentBlueprintForm.update(this.client.getPlayer());
                ControllerFocus currentFocus = this.expanded.mainGame.formManager.getCurrentFocus();
                Point pos;
                GameWindow window = WindowManager.getWindow();
                if (currentFocus != null) {
                    pos = new Point(currentFocus.boundingBox.x, currentFocus.boundingBox.y);
                } else {
                    InputPosition mousePos = window.mousePos();
                    pos = new Point(mousePos.hudX, mousePos.hudY);
                }

                GameBackground background = GameBackground.itemTooltip;
                this.currentBlueprintForm.setPosition(Math.min(pos.x + background.getContentPadding() + 1, window.getHudWidth() - this.currentBlueprintForm.getWidth() - background.getContentPadding() - 4), pos.y - this.currentBlueprintForm.getHeight() - 11 - 20);
            }
        });

        return button;
    }

    public void onWindowResized(GameWindow window) {}

    public void dispose() {
        if (this.currentBlueprintForm != null && !this.currentBlueprintForm.isDisposed()) {
            lastBlueprintFormPos = new Point(this.currentBlueprintForm.getX(), this.currentBlueprintForm.getY());
        }
    }
}

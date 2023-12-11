package settlementexpansion.inventory.form;

import necesse.engine.Screen;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.gameTooltips.ListGameTooltips;
import settlementexpansion.object.entity.BlueprintObjectEntity;

import java.awt.*;
import java.util.List;

public abstract class CurrentBlueprintRecipeForm extends Form {

    private ListGameTooltips tooltips;
    public BlueprintObjectEntity objectEntity;
    private boolean isClickRemoving;

    public CurrentBlueprintRecipeForm(BlueprintObjectEntity objectEntity) {
        super("currentblueprintrecipe", 200, 300);
        this.objectEntity = objectEntity;
        this.onDragged((e) -> {
            e.x = GameMath.limit(e.x, -this.getWidth() + 20, Screen.getHudWidth() - 20);
            e.y = GameMath.limit(e.y, -this.getHeight() + 20, Screen.getHudHeight() - 20);
        });
    }

    public static ListGameTooltips getTooltips(BlueprintObjectEntity objectEntity, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        if (objectEntity != null) {

            tooltips.add(objectEntity.getObject().getLocalization());
            tooltips.add(objectEntity.getPreset().getRecipe().getTooltip(perspective));
        }

        return tooltips;
    }

    public void setObjectEntity(BlueprintObjectEntity objectEntity) {
        this.objectEntity = objectEntity;
    }

    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleInputEvent(event, tickManager, perspective);
        if (!event.isUsed()) {
            if (event.getID() == -99) {
                boolean mouseOver = this.isMouseOver(event);
                if (event.state && mouseOver) {
                    this.isClickRemoving = true;
                } else {
                    if (this.isClickRemoving && mouseOver) {
                        this.onRemove();
                    }

                    this.isClickRemoving = false;
                }
            }

        }
    }

    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    public abstract void onRemove();

    public void update(PlayerMob perspective) {
        if (this.objectEntity != null) {
            this.tooltips = getTooltips(this.objectEntity, perspective);
            this.setWidth(this.tooltips.getWidth());
            this.setHeight(this.tooltips.getHeight());
        } else {
            this.setWidth(100);
            this.setHeight(200);
        }

        this.setDraggingBox(new Rectangle(this.getWidth(), this.getHeight()));
    }

    public void onWindowResized() {
        super.onWindowResized();
        this.setX(GameMath.limit(this.getX(), -this.getWidth() + 20, Screen.getHudWidth() - 20));
        this.setY(GameMath.limit(this.getY(), -this.getHeight() + 20, Screen.getHudHeight() - 20));
    }

    public void drawBase(TickManager tickManager) {
        int padding = GameBackground.itemTooltip.getContentPadding();
        GameBackground.itemTooltip.getDrawOptions(this.getX(), this.getY(), this.getWidth() + padding * 2, this.getHeight() + padding * 2).draw();
    }

    public void drawComponents(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        super.drawComponents(tickManager, perspective, renderBox);
        if (this.tooltips != null) {
            int padding = GameBackground.itemTooltip.getContentPadding();
            this.tooltips.draw(padding, padding, GameColor.DEFAULT_COLOR);
        }

    }

    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.update(perspective);
        super.draw(tickManager, perspective, renderBox);
    }

}

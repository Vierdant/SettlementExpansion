package settlementexpansion.inventory.form;

import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.*;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.settlement.*;
import necesse.gfx.forms.presets.containerComponent.settlement.diets.SettlementDietsForm;
import necesse.gfx.forms.presets.containerComponent.settlement.equipment.SettlementEquipmentForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.HUD;
import necesse.inventory.container.settlement.data.SettlementLockedBedData;
import necesse.inventory.container.settlement.data.SettlementSettlerBasicData;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;
import settlementexpansion.inventory.container.SettlementModContainer;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SettlementModContainerForm<T extends SettlementModContainer> extends SettlementContainerForm<T> {
    public static String lastOpenType;
    private final Form menuBar;
    private final FormSwitcher contentSwitcher;
    private final LinkedList<SettlementSubForm> menus;
    private final Form privateForm;
    private static final HashMap<LevelIdentifier, HashSet<Integer>> levelsSelectedSettlers = new HashMap<>();
    public final HashSet<Integer> selectedSettlers;
    public int settlerBasicsSubscription = -1;
    public ArrayList<SettlementSettlerBasicData> settlers = new ArrayList<>();
    public ArrayList<SettlementLockedBedData> lockedBeds = new ArrayList<>();
    private final SettlementModCommandForm<T> commandForm;
    public SettlementContainerGameTool tool;
    public HudDrawElement selectedSettlersHudElement;

    public SettlementModContainerForm(Client client, T container) {
        super(client, container);
        this.selectedSettlers = levelsSelectedSettlers.compute(client.getLevel().getIdentifier(), (k, v) 
                -> v == null ? new HashSet<>() : v);
        this.menuBar = this.addComponent(new Form("menubar", 800, 40));
        this.contentSwitcher = this.addComponent(new FormSwitcher());
        this.menus = new LinkedList<>();
        this.menus.add(new SettlementModSettingsForm<>(client, container, this));
        this.menus.add(new SettlementSettlersForm<>(client, container, this));
        this.menus.add(this.commandForm = new SettlementModCommandForm<>(client, container, this));
        this.menus.add(new SettlementEquipmentForm<>(client, container, this));
        this.menus.add(new SettlementDietsForm<>(client, container, this));
        this.menus.add(new SettlementRestrictForm<>(client, container, this));
        this.menus.add(new SettlementDefendZoneForm<>(client, container, this));
        this.menus.add(new SettlementWorkPrioritiesForm<>(client, container, this));
        this.menus.add(new SettlementAssignWorkForm<>(client, container, this));

        for (SettlementSubForm menu : this.menus) {
            this.contentSwitcher.addComponent((FormComponent) menu, (c, active) -> {
                ((SettlementSubForm) c).onSetCurrent(active);
            });
        }

        this.updateMenuBar();
        this.privateForm = this.contentSwitcher.addComponent(new Form(400, 40));
        this.onWindowResized();
    }

    protected void init() {
        this.container.onEvent(SettlementBasicsEvent.class, (e) -> {
            this.updatePrivateForm();
        });
        this.container.onEvent(SettlementSettlersChangedEvent.class, (event) -> {
            if (this.container.basics.hasAccess(this.client)) {
                this.container.requestSettlerBasics.runAndSend();
            }

        });
        this.container.onEvent(SettlementSettlerBasicsEvent.class, (event) -> {
            this.settlers = event.settlers;
            this.lockedBeds = event.lockedBeds;
            synchronized(this.selectedSettlers) {
                HashSet<Integer> lastSelected = new HashSet<>(this.selectedSettlers);
                this.selectedSettlers.clear();

                for (SettlementSettlerBasicData settler : this.settlers) {
                    if (lastSelected.contains(settler.mobUniqueID)) {
                        this.selectedSettlers.add(settler.mobUniqueID);
                    }
                }

                this.updateSelectedSettlers(false);
            }
        });
        if (this.selectedSettlersHudElement != null) {
            this.selectedSettlersHudElement.remove();
        }

        this.selectedSettlersHudElement = new HudDrawElement() {
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                if (!selectedSettlers.isEmpty()) {
                    final DrawOptionsList drawOptions = new DrawOptionsList();
                    synchronized(selectedSettlers) {
                        HashSet<Integer> removes = new HashSet<>();
                        Iterator<Integer> settlerIterator = selectedSettlers.iterator();

                        while(true) {
                            if (!settlerIterator.hasNext()) {
                                HashSet<Integer> settler = selectedSettlers;
                                Objects.requireNonNull(settler);
                                removes.forEach(settler::remove);
                                break;
                            }

                            int mobUniqueID = settlerIterator.next();
                            Mob mob = this.getLevel().entityManager.mobs.get(mobUniqueID, false);
                            if (mob instanceof CommandMob && ((CommandMob)mob).canBeCommanded(client)) {
                                Rectangle mobSelectBox = mob.getSelectBox();
                                if (camera.getBounds().intersects(mobSelectBox)) {
                                    drawOptions.add(HUD.levelBoundOptions(camera, new Color(255, 255, 255, 150), true, mobSelectBox));
                                }
                            } else {
                                removes.add(mobUniqueID);
                            }
                        }
                    }

                    list.add(new SortedDrawable() {
                        public int getPriority() {
                            return -1000000;
                        }

                        public void draw(TickManager tickManager) {
                            drawOptions.draw();
                        }
                    });
                }

            }
        };
        this.client.getLevel().hudManager.addElement(this.selectedSettlersHudElement);
        this.updatePrivateForm();
        if (lastOpenType != null) {
            if (!this.contentSwitcher.isCurrent(this.privateForm)) {

                for (SettlementSubForm menu : this.menus) {
                    if (lastOpenType.equals(menu.getTypeString())) {
                        menu.onMenuButtonClicked(this.contentSwitcher);
                        break;
                    }
                }
            } else {
                lastOpenType = null;
            }
        }

        super.init();
    }

    private void updateMenuBar() {
        if (this.menus == null) return;
        int minWidth = Math.min(50 * this.menus.size(), 200);
        int maxWidth = Screen.getHudWidth() - 200;
        this.menuBar.setWidth(GameMath.limit(200 * this.menus.size(), minWidth, Math.max(maxWidth, minWidth)));
        this.menuBar.clearComponents();
        int usedWidth = 4;
        int i = 0;

        for(Iterator<SettlementSubForm> var5 = this.menus.iterator(); var5.hasNext(); ++i) {
            final SettlementSubForm menu = var5.next();
            int remainingButtons = this.menus.size() - i;
            int remainingWidth = this.menuBar.getWidth() - 4 - usedWidth;
            int width = remainingWidth / remainingButtons;
            FormLocalTextButtonToggle button = this.menuBar.addComponent(new FormLocalTextButtonToggle(menu.getMenuButtonName(), usedWidth, 0, width, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE) {
                public boolean isToggled() {
                    return contentSwitcher.isCurrent((FormComponent)menu);
                }
            });
            button.controllerFocusHashcode = "settlementMenuButton" + menu.getTypeString();
            button.onClicked((e) -> {
                e.preventDefault();
                (e.from).playTickSound();
                if (this.contentSwitcher.isCurrent((FormComponent)menu)) {
                    this.contentSwitcher.clearCurrent();
                    lastOpenType = null;
                } else {
                    menu.onMenuButtonClicked(this.contentSwitcher);
                    lastOpenType = menu.getTypeString();
                }

            });
            usedWidth += width;
        }

        this.menuBar.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() - this.menuBar.getHeight() / 2 - Settings.UI.formSpacing - 28);
    }

    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updatePrivateFormActive(false);
        super.draw(tickManager, perspective, renderBox);
    }

    private void updatePrivateForm() {
        this.privateForm.clearComponents();
        FormFlow flow = new FormFlow(5);
        this.privateForm.addComponent(flow.next(new FormLocalLabel(this.container.basics.settlementName, new FontOptions(20), 0, this.privateForm.getWidth() / 2, 0, this.privateForm.getWidth() - 20), 10));
        this.privateForm.addComponent(flow.next(new FormLocalLabel(new LocalMessage("ui", "settlementispriv"), new FontOptions(16), 0, this.privateForm.getWidth() / 2, 0, this.privateForm.getWidth() - 20), 10));
        this.privateForm.addComponent(flow.next(new FormLocalLabel(new LocalMessage("ui", "settlementprivatetip"), new FontOptions(16), 0, this.privateForm.getWidth() / 2, 0, this.privateForm.getWidth() - 20), 10));
        this.privateForm.addComponent(flow.next(new FormLocalLabel(new LocalMessage("ui", "settlementowner", "owner", this.container.basics.ownerName), new FontOptions(16), 0, this.privateForm.getWidth() / 2, 0), 10));
        int buttonWidth = Math.min(this.privateForm.getWidth() - 8, 300);
        if (this.container.basics.isTeamPublic) {
            (this.privateForm.addComponent(flow.next(new FormLocalTextButton("ui", "teamjoin", this.privateForm.getWidth() / 2 - buttonWidth / 2, 0, buttonWidth, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE), 20))).onClicked((e) -> {
                this.container.requestJoin.runAndSend(true);
                (e.from).startCooldown(5000);
            });
        } else {
            (this.privateForm.addComponent(flow.next(new FormLocalTextButton("ui", "teamrequestjoin", this.privateForm.getWidth() / 2 - buttonWidth / 2, 0, buttonWidth, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE), 20))).onClicked((e) -> {
                this.container.requestJoin.runAndSend(false);
                e.from.startCooldown(5000);
            });
        }

        this.privateForm.setHeight(flow.next());
        this.updatePrivateFormActive(true);
    }

    private void updatePrivateFormActive(boolean forceUpdate) {
        if (!this.container.basics.hasAccess(this.client)) {
            if (forceUpdate || !this.contentSwitcher.isCurrent(this.privateForm)) {
                this.contentSwitcher.makeCurrent(this.privateForm);
                this.menuBar.setHidden(true);
                if (this.settlerBasicsSubscription != -1) {
                    this.container.subscribeSettlerBasics.unsubscribe(this.settlerBasicsSubscription);
                }

                this.settlerBasicsSubscription = -1;
                if (!this.selectedSettlers.isEmpty()) {
                    this.selectedSettlers.clear();
                    this.updateSelectedSettlers(false);
                }

                if (this.tool != null) {
                    Screen.clearGameTool(this.tool);
                }

                this.tool = null;
            }
        } else if (forceUpdate || this.contentSwitcher.isCurrent(this.privateForm)) {
            if (this.contentSwitcher.isCurrent(this.privateForm)) {
                this.contentSwitcher.clearCurrent();
            }

            this.menuBar.setHidden(false);
            if (this.settlerBasicsSubscription == -1) {
                this.settlerBasicsSubscription = this.container.subscribeSettlerBasics.subscribe();
                this.container.requestSettlerBasics.runAndSend();
                if (this.tool != null) {
                    Screen.clearGameTool(this.tool);
                }

                Screen.setGameTool(this.tool = new SettlementContainerGameTool(this.client, this.container, this));
            }
        }

    }

    public boolean isCurrent(SettlementSubForm form) {
        if (!contentSwitcher.hasComponent((FormComponent)form)) return false;
        return this.contentSwitcher.isCurrent((FormComponent)form);
    }

    public SettlementSubForm getCurrentSubForm() {
        FormComponent current = this.contentSwitcher.getCurrent();
        return current instanceof SettlementSubForm ? (SettlementSubForm)current : null;
    }

    public SettlementToolHandler getCurrentToolHandler() {
        SettlementSubForm current = this.getCurrentSubForm();
        return current != null ? current.getToolHandler() : null;
    }

    public void updateSelectedSettlers(boolean switchToCommandForm) {
        if (this.container.basics.hasAccess(this.client)) {
            if (!this.selectedSettlers.isEmpty()) {
                if (switchToCommandForm && !this.contentSwitcher.isCurrent(this.commandForm) || this.contentSwitcher.getCurrent() == null) {
                    this.commandForm.onMenuButtonClicked(this.contentSwitcher);
                    lastOpenType = this.commandForm.getTypeString();
                }

                this.commandForm.updateSelectedForm();
            } else if (this.contentSwitcher.isCurrent(this.commandForm)) {
                this.commandForm.updateCurrentForm();
            }

        }
    }

    public static boolean isShiftDown() {
        return Screen.input().isKeyDown(340) || Screen.input().isKeyDown(344);
    }

    public void selectOrDeselectSettler(int mobUniqueID) {
        this.selectOrDeselectSettler(!isShiftDown(), mobUniqueID);
    }

    public void selectOrDeselectSettler(boolean switchToCommandForm, int mobUniqueID) {
        if (this.selectedSettlers.contains(mobUniqueID) && this.selectedSettlers.size() > 1) {
            if (isShiftDown()) {
                this.deselectSettlers(switchToCommandForm, mobUniqueID);
            } else {
                this.selectSettlers(switchToCommandForm, mobUniqueID);
            }
        } else {
            this.selectSettlers(switchToCommandForm, mobUniqueID);
        }

    }

    public void deselectSettlers(Integer... mobUniqueIDs) {
        this.deselectSettlers(!isShiftDown(), mobUniqueIDs);
    }

    public void deselectSettlers(boolean switchToCommandForm, Integer... mobUniqueIDs) {
        this.deselectSettlers(switchToCommandForm, () -> {
            return GameUtils.arrayIterator(mobUniqueIDs);
        });
    }

    public void deselectSettlers(Iterable<Integer> mobUniqueIDs) {
        this.deselectSettlers(!isShiftDown(), mobUniqueIDs);
    }

    public void deselectSettlers(boolean switchToCommandForm, Iterable<Integer> mobUniqueIDs) {
        synchronized(this.selectedSettlers) {
            boolean update = false;

            int uniqueID;
            for(Iterator<Integer> var5 = mobUniqueIDs.iterator(); var5.hasNext(); update = this.selectedSettlers.remove(uniqueID) || update) {
                uniqueID = (Integer)var5.next();
            }

            if (update) {
                this.updateSelectedSettlers(switchToCommandForm);
            }

        }
    }

    public void selectSettlers(Integer... mobUniqueIDs) {
        this.selectSettlers(!isShiftDown(), mobUniqueIDs);
    }

    public void selectSettlers(boolean switchToCommandForm, Integer... mobUniqueIDs) {
        this.selectSettlers(switchToCommandForm, () -> {
            return GameUtils.arrayIterator(mobUniqueIDs);
        });
    }

    public void selectSettlers(Iterable<Integer> mobUniqueIDs) {
        this.selectSettlers(!isShiftDown(), mobUniqueIDs);
    }

    public void selectSettlers(boolean switchToCommandForm, Iterable<Integer> mobUniqueIDs) {
        synchronized(this.selectedSettlers) {
            if (!isShiftDown()) {
                this.selectedSettlers.clear();
            }
            for (int uniqueID : mobUniqueIDs) {
                this.selectedSettlers.add(uniqueID);
            }
        }

        this.updateSelectedSettlers(switchToCommandForm);
    }

    public void onWindowResized() {
        super.onWindowResized();
        this.updateMenuBar();
        if (this.privateForm != null) {
            this.privateForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
        }
    }

    public void dispose() {
        if (this.selectedSettlersHudElement != null) {
            this.selectedSettlersHudElement.remove();
        }

        this.selectedSettlersHudElement = null;
        Screen.clearGameTool(this.tool);
        super.dispose();
    }

    public boolean shouldOpenInventory() {
        return false;
    }

    public boolean shouldShowInventory() {
        return false;
    }

    public boolean shouldShowToolbar() {
        return false;
    }
}

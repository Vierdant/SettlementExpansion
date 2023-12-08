package settlementexpansion.inventory.container;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.level.maps.Level;
import settlementexpansion.inventory.action.ForceChangeClaimAction;
import settlementexpansion.inventory.event.SettlementClaimTickEvent;
import settlementexpansion.map.settlement.SettlementModData;
import settlementexpansion.object.entity.SettlementFlagModObjectEntity;

public class SettlementClaimContainer extends SettlementDependantContainer {
    public SettlementFlagModObjectEntity objectEntity;
    public ForceChangeClaimAction forceClaim;
    public SettlementClaimTickEvent claimTick;
    public int currentRefresh;
    public int lastRefresh;
    public boolean canClaim;
    public boolean timerStarted;
    public long timerStartPoint;
    public long timerPeriod;

    public SettlementClaimContainer(final NetworkClient client, int uniqueSeed, SettlementFlagModObjectEntity objectEntity) {
        super(client, uniqueSeed);
        this.objectEntity = objectEntity;
        this.timerPeriod = 5000L;
        this.canClaim = false;
        this.timerStarted = false;
        this.timerStartPoint = -1;
        this.claimTick = new SettlementClaimTickEvent(this);

        this.subscribeEvent(SettlementClaimTickEvent.class, (e) -> true, () -> true);
        this.onEvent(SettlementClaimTickEvent.class, (event) -> {
           this.claimTick = event;
           this.canClaim = event.canClaim;
           if (event.shouldTimerTick) {
               startTimer();
           }
        });

        if (this.client.isServerClient()) {
            new SettlementClaimTickEvent(this).applyAndSendToClient(this.client.getServerClient());
        }

        this.forceClaim = this.registerAction(new ForceChangeClaimAction(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.client.playerMob.isInCombat()) {
            if (this.client.isServerClient()) {
                this.client.getServerClient().closeContainer(true);
            }
        }

        if (this.client.isServerClient()) {
            if (this.timerStarted && !timerTicking() && !this.canClaim) {
                this.canClaim = checkDeadSettlers() && hasRequiredItem();
                new SettlementClaimTickEvent(this).applyAndSendToClient(this.client.getServerClient());
            }

            if (hasRequiredItem() && checkDeadSettlers() && !timerTicking() && !this.canClaim) {
                startTimer();
                new SettlementClaimTickEvent(this).applyAndSendToClient(this.client.getServerClient());
            }

            if (timerTicking()) {
                this.canClaim = false;

                if (secondsPassedSinceRefresh() == 1) {
                    this.lastRefresh = this.currentRefresh;
                    this.currentRefresh--;
                    new SettlementClaimTickEvent(this).applyAndSendToClient(this.client.getServerClient());
                }
            }

            if (this.timerStarted && this.canClaim && (!checkDeadSettlers() || !hasRequiredItem())) {
                this.canClaim = false;
                startTimer();
                new SettlementClaimTickEvent(this).applyAndSendToClient(this.client.getServerClient());
            }
        }
    }

    public SettlementFlagModObjectEntity getObjectEntity() {
        return this.objectEntity;
    }

    public boolean timerTicking() {
        return this.timerStartPoint != -1 && this.timerStartPoint + this.timerPeriod > this.objectEntity.getWorldEntity().getTime();
    }

    public void startTimer() {
        this.timerStartPoint = this.objectEntity.getWorldEntity().getTime();
        this.currentRefresh = (int)(this.timerPeriod / 1000L);
        this.timerStarted = true;
    }

    public int secondsPassedSinceRefresh() {
        return (int)(getTimerTimeLeft() / 1000) < currentRefresh ? 1 : 0;
    }

    public long getTimerTimeLeft() {
        return this.timerStartPoint + this.timerPeriod - this.objectEntity.getWorldEntity().getTime();
    }

    public boolean checkDeadSettlers() {
        return this.getLevelData().settlers.isEmpty();
    }

    public boolean hasRequiredItem() {
        return 0 < this.client.playerMob.getInv().main.getAmount(this.client.playerMob.getLevel(), this.client.playerMob, ItemRegistry.getItem("claimingscroll"), "forceclaim");
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        } else {
            Level level = client.getLevel();
            return !this.objectEntity.removed() && level.getObject(this.objectEntity.getX(), this.objectEntity.getY()).inInteractRange(level, this.objectEntity.getX(), this.objectEntity.getY(), client.playerMob);
        }
    }

    @Override
    protected Level getLevel() {
        return this.objectEntity.getLevel();
    }

    public SettlementModData getLevelModData() {
        if (!this.client.isServerClient()) {
            throw new IllegalStateException("Cannot get level data client side");
        } else {
            return SettlementModData.getSettlementModData(this.getLevel());
        }
    }
}

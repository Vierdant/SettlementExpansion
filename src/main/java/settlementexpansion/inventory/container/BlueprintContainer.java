package settlementexpansion.inventory.container;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.TileDamageType;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.StringCustomAction;
import necesse.inventory.container.settlement.SettlementContainerObjectStatusManager;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.level.maps.Level;
import settlementexpansion.object.entity.BlueprintObjectEntity;

import java.awt.*;

public class BlueprintContainer extends SettlementDependantContainer {
    public final EmptyCustomAction buildAction;
    public final StringCustomAction setWoodType;
    public final StringCustomAction setWallType;
    public final StringCustomAction setFloorType;
    public final BlueprintObjectEntity objectEntity;
    public SettlementContainerObjectStatusManager settlementObjectManager;

    public BlueprintContainer(NetworkClient client, int uniqueSeed, BlueprintObjectEntity objectEntity, PacketReader reader) {
        super(client, uniqueSeed);
        this.objectEntity = objectEntity;
        this.settlementObjectManager = new SettlementContainerObjectStatusManager(this, objectEntity.getLevel(), objectEntity.getTileX(), objectEntity.getTileY(), reader);
        this.setWoodType = this.registerAction(new StringCustomAction() {
            @Override
            protected void run(String value) {
                objectEntity.getPreset().setFurnitureType(value);
                objectEntity.markDirty();
            }
        });

        this.setWallType = this.registerAction(new StringCustomAction() {
            @Override
            protected void run(String value) {
                objectEntity.getPreset().setCurrentWall(value);
                objectEntity.markDirty();
            }
        });

        this.setFloorType = this.registerAction(new StringCustomAction() {
            @Override
            protected void run(String value) {
                objectEntity.getPreset().setCurrentFloor(value);
                objectEntity.markDirty();
            }
        });

        this.buildAction = this.registerAction(new EmptyCustomAction() {
            @Override
            protected void run() {
                int x = objectEntity.getTileX();
                int y = objectEntity.getTileY();
                int rotation = getLevel().getObjectRotation(x, y);
                Point placeTile = objectEntity.getPlaceTile(rotation);
                objectEntity.getPreset().applyToLevel(getLevel(), placeTile.x, placeTile.y);
                objectEntity.getPreset().getRecipe().consumeBuildMaterials(getClientInventory());
                getLevel().entityManager.doDamage(x, y, 100, TileDamageType.Object, -1, null);
                close();
            }
        });
    }

    @Override
    protected Level getLevel() {
        return this.client.isServerClient() ? this.client.getServerClient().getLevel() : this.client.playerMob.getLevel();
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
}

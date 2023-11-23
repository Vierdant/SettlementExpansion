package settlementexpansion.inventory.container;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.object.OEInventoryContainer;
import settlementexpansion.object.entity.LockedInventoryObjectEntity;

public class LockedInventoryContainer extends OEInventoryContainer {

    public LockedInventoryObjectEntity lockedObjectEntity;
    public EmptyCustomAction lockButton;

    public LockedInventoryContainer(NetworkClient client, int uniqueSeed, LockedInventoryObjectEntity objectEntity, PacketReader reader) {
        super(client, uniqueSeed, objectEntity, reader);
        this.lockedObjectEntity = objectEntity;

        this.lockButton = registerAction(new EmptyCustomAction() {
            @Override
            protected void run() {
                objectEntity.switchLockState();
                if (objectEntity.getLockState()) {
                    objectEntity.setCurrentOwner(client.authentication);
                }
            }
        });
    }

}

package settlementexpansion.inventory.container;

import necesse.engine.GameTileRange;
import necesse.engine.Settings;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.container.settlement.SettlementContainerObjectStatusManager;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.level.maps.Level;
import settlementexpansion.object.entity.BlueprintObjectEntity;

import java.util.Collection;
import java.util.LinkedHashSet;

public class BlueprintContainer extends SettlementDependantContainer {
    public final EmptyCustomAction buildAction;
    public final BlueprintObjectEntity objectEntity;
    public SettlementContainerObjectStatusManager settlementObjectManager;

    public BlueprintContainer(NetworkClient client, int uniqueSeed, BlueprintObjectEntity objectEntity, PacketReader reader) {
        super(client, uniqueSeed);
        this.objectEntity = objectEntity;
        this.settlementObjectManager = new SettlementContainerObjectStatusManager(this, objectEntity.getLevel(), objectEntity.getTileX(), objectEntity.getTileY(), reader);

        this.buildAction = this.registerAction(new EmptyCustomAction() {
            @Override
            protected void run() {
                System.out.println("placing");
            }
        });
    }

    @Override
    protected Level getLevel() {
        return this.client.isServerClient() ? this.client.getServerClient().getLevel() : this.client.playerMob.getLevel();
    }

    public static void openAndSendContainer(int containerID, ServerClient client, Level level, int tileX, int tileY, Packet extraContent) {
        if (!level.isServerLevel()) {
            throw new IllegalStateException("Level must be a server level");
        } else {
            Packet packet = new Packet();
            PacketWriter writer = new PacketWriter(packet);
            writer.putNextContentPacket(client.playerMob.getInv().getTempInventoryPacket(0));
            if (extraContent != null) {
                writer.putNextContentPacket(extraContent);
            }

            PacketOpenContainer p = PacketOpenContainer.LevelObject(containerID, tileX, tileY, packet);
            ContainerRegistry.openAndSendContainer(client, p);
        }
    }

    public static void openAndSendContainer(int containerID, ServerClient client, Level level, int tileX, int tileY) {
        openAndSendContainer(containerID, client, level, tileX, tileY, null);
    }
}

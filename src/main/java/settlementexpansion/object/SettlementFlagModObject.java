package settlementexpansion.object;

import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SettlementFlagObjectEntity;
import necesse.inventory.item.Item;
import necesse.level.gameObject.SettlementFlagObject;
import necesse.level.maps.Level;
import settlementexpansion.item.placeable.SettlementFlagModObjectItem;
import settlementexpansion.object.entity.SettlementFlagModObjectEntity;
import settlementexpansion.registry.ContainerModRegistry;

public class SettlementFlagModObject extends SettlementFlagObject {

    public SettlementFlagModObject() {
        super();
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new SettlementFlagModObjectEntity(level, x, y);
    }

    public Item generateNewObjectItem() {
        return new SettlementFlagModObjectItem(this);
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServerLevel()) {
            SettlementFlagObjectEntity objectEntity = (SettlementFlagObjectEntity)level.entityManager.getObjectEntity(x, y);
            PacketOpenContainer p = PacketOpenContainer.ObjectEntity(ContainerModRegistry.SETTLEMENT_CONTAINER, objectEntity, objectEntity.getContainerContentPacket(player.getServerClient()));
            ContainerRegistry.openAndSendContainer(player.getServerClient(), p);
        }

    }
}

package settlementexpansion.registry;

import necesse.engine.network.PacketReader;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.container.object.OEInventoryContainer;
import settlementexpansion.inventory.container.*;
import settlementexpansion.inventory.form.*;
import settlementexpansion.entity.mob.friendly.BlacksmithModHumanMob;
import settlementexpansion.object.entity.BlueprintObjectEntity;
import settlementexpansion.object.entity.LockedInventoryObjectEntity;
import settlementexpansion.object.entity.SettlementFlagModObjectEntity;

public class ContainerModRegistry {

    public static int OE_LOCKED_INVENTORY_CONTAINER;
    public static int BLACKSMITH_CONTAINER;
    public static int STUDYTABLE_CONTAINER;
    public static int TOOLSRACK_CONTAINER;
    public static int BLUEPRINT_CONTAINER;
    public static int SETTLEMENT_CONTAINER;
    public static int SETTLEMENT_CLAIM_CONTAINER;

    public static void registerContainers() {
        OE_LOCKED_INVENTORY_CONTAINER =
                ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) ->
                                new LockedInventoryContainerForm<>(client, new LockedInventoryContainer(client.getClient(), uniqueSeed, (LockedInventoryObjectEntity) oe,
                                        new PacketReader(content))), (client, uniqueSeed, oe, content, serverObject) ->
                new LockedInventoryContainer(client, uniqueSeed, (LockedInventoryObjectEntity) oe, new PacketReader(content)));

        BLACKSMITH_CONTAINER = ContainerRegistry.registerMobContainer((client, uniqueSeed, mob, content) ->
                new BlacksmithContainerForm<>(client, new BlacksmithContainer(client.getClient(), uniqueSeed, (BlacksmithModHumanMob)mob,
                        new PacketReader(content))), (client, uniqueSeed, mob, content, serverObject) ->
                new BlacksmithContainer(client, uniqueSeed, (BlacksmithModHumanMob) mob, new PacketReader(content)));

        STUDYTABLE_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) ->
                new StudyTableContainerForm(client, new OEInventoryContainer(client.getClient(), uniqueSeed, (OEInventory)oe,
                        new PacketReader(content))), (client, uniqueSeed, oe, content, serverObject) ->
                new OEInventoryContainer(client, uniqueSeed, (OEInventory)oe, new PacketReader(content)));

        TOOLSRACK_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) ->
                new ToolsRackContainerForm(client, new OEInventoryContainer(client.getClient(), uniqueSeed, (OEInventory)oe,
                        new PacketReader(content))), (client, uniqueSeed, oe, content, serverObject) ->
                new OEInventoryContainer(client, uniqueSeed, (OEInventory)oe, new PacketReader(content)));

        BLUEPRINT_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) ->
                new BlueprintContainerForm<>(client, new BlueprintContainer(client.getClient(), uniqueSeed, (BlueprintObjectEntity) oe,
                        new PacketReader(content))), (client, uniqueSeed, oe, content, serverObject) ->
                new BlueprintContainer(client, uniqueSeed, (BlueprintObjectEntity) oe, new PacketReader(content)));

        SETTLEMENT_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) ->
                new SettlementModContainerForm<>(client, new SettlementModContainer(client.getClient(), uniqueSeed, (SettlementFlagModObjectEntity)oe,
                        content)), (client, uniqueSeed, oe, content, serverObject) ->
                new SettlementModContainer(client, uniqueSeed, (SettlementFlagModObjectEntity)oe, content));
        SETTLEMENT_CLAIM_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) ->
                new SettlementClaimForm(client, new SettlementClaimContainer(client.getClient(), uniqueSeed, (SettlementFlagModObjectEntity)oe
                )), (client, uniqueSeed, oe, content, serverObject) ->
                new SettlementClaimContainer(client, uniqueSeed, (SettlementFlagModObjectEntity)oe));

        ContainerRegistry.PVP_TEAMS_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) ->
                new PvPTeamsModContainerForm(client,
                        new PvPTeamsModContainer(client.getClient(), uniqueSeed, content)), (client, uniqueSeed, content, serverObject) ->
                new PvPTeamsModContainer(client, uniqueSeed, content));
    }
}

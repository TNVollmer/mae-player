package thkoeln.dungeon.player.core.domainprimitives.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandTest {
    private UUID robotId, gameId, playerId, planetId;
    private ItemType itemType;
    private int numberOf;
    private Capability capability;
    Command move, item, robot, upgrade, regen;


    @BeforeEach
    public void setUp() {
        robotId = UUID.randomUUID();
        gameId = UUID.randomUUID();
        playerId = UUID.randomUUID();
        planetId = UUID.randomUUID();
        itemType = ItemType.HEALTH_RESTORE;
        numberOf = 2;
        capability = Capability.forTypeAndLevel(CapabilityType.ENERGY_REGEN, 3);
        move = Command.createMove( robotId, planetId, gameId, playerId );
        item = Command.createItemPurchase( itemType, numberOf, robotId, gameId, playerId );
        robot = Command.createRobotPurchase( 2, gameId, playerId );
        upgrade = Command.createUpgrade( capability, robotId, gameId, playerId );
        regen = Command.createRegeneration( robotId, gameId, playerId );
    }


    @Test
    public void testCreateMoveValidation() {
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createMove(null, planetId, gameId, playerId);
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createMove(robotId, null, gameId, playerId);
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createMove(robotId, planetId, null, playerId);
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createMove(robotId, planetId, gameId, null);
        });
    }


    @Test
    public void testCreateItemPurchaseValidation() {
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createItemPurchase(null, numberOf, robotId, gameId, playerId);
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createItemPurchase(itemType, -1, robotId, gameId, playerId);
        });
        assertNull(Command.createItemPurchase(itemType, 0, robotId, gameId, playerId));
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createItemPurchase(itemType, numberOf, null, gameId, playerId);
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createItemPurchase(itemType, numberOf, robotId, null, playerId);
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createItemPurchase(itemType, numberOf, robotId, gameId, null);
        });
    }


    @Test
    public void testCreateRobotPurchaseValidation() {
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createRobotPurchase( -1, gameId, playerId );
        });
        assertNull(Command.createRobotPurchase(0, gameId, playerId) );
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createRobotPurchase( numberOf, null, playerId );
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createRobotPurchase( numberOf, gameId, null );
        });
    }


    @Test
    public void testCreateUpgradeValidation() {
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createUpgrade( null, robotId, gameId, playerId );
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createUpgrade( capability, null, gameId, playerId );
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createUpgrade( capability, robotId, null, playerId );
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createUpgrade( capability, robotId, gameId, null );
        });
    }

    @Test
    public void testCreateRegenValidation() {
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createRegeneration( null, gameId, playerId );
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createRegeneration( robotId, null, playerId );
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            Command.createRegeneration( robotId, gameId, null );
        });
    }

    @Test
    public void testCommandType() {
        Assertions.assertEquals( CommandType.MOVEMENT, move.getCommandType() );
        assertEquals( CommandType.BUYING, item.getCommandType() );
        assertEquals( CommandType.BUYING, robot.getCommandType() );
        assertEquals( CommandType.BUYING, upgrade.getCommandType() );
        assertEquals( CommandType.REGENERATE, regen.getCommandType() );
    }

    @Test
    public void testPlayerId() {
        assertEquals( playerId, move.getPlayerId() );
        assertEquals( playerId, item.getPlayerId() );
        assertEquals( playerId, robot.getPlayerId() );
        assertEquals( playerId, upgrade.getPlayerId() );
        assertEquals( playerId, regen.getPlayerId() );
    }

    @Test
    public void testRobotId() {
        assertEquals( robotId, move.getCommandObject().getRobotId() );
        assertEquals( robotId, item.getCommandObject().getRobotId() );
        assertEquals( robotId, upgrade.getCommandObject().getRobotId() );
        assertEquals( robotId, regen.getCommandObject().getRobotId() );
    }

    @Test
    public void testOtherProperties() {
        assertEquals( itemType.name(), item.getCommandObject().getItemName() );
        assertEquals( capability.toStringForCommand(), upgrade.getCommandObject().getItemName() );
    }


}

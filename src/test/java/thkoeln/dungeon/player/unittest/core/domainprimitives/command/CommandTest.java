package thkoeln.dungeon.player.unittest.core.domainprimitives.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.domainprimitives.command.CommandType;
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
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createMove(null, planetId, gameId, playerId);
        });
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createMove(robotId, null, gameId, playerId);
        });
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createMove(robotId, planetId, null, playerId);
        });
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createMove(robotId, planetId, gameId, null);
        });
    }


    @Test
    public void testCreateItemPurchaseValidation() {
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createItemPurchase(null, numberOf, robotId, gameId, playerId);
        });
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createItemPurchase(itemType, -1, robotId, gameId, playerId);
        });
        Assertions.assertNull(Command.createItemPurchase(itemType, 0, robotId, gameId, playerId));
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createItemPurchase(itemType, numberOf, null, gameId, playerId);
        });
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createItemPurchase(itemType, numberOf, robotId, null, playerId);
        });
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createItemPurchase(itemType, numberOf, robotId, gameId, null);
        });
    }


    @Test
    public void testCreateRobotPurchaseValidation() {
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createRobotPurchase( -1, gameId, playerId );
        });
        Assertions.assertNull(Command.createRobotPurchase(0, gameId, playerId) );
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createRobotPurchase( numberOf, null, playerId );
        });
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createRobotPurchase( numberOf, gameId, null );
        });
    }


    @Test
    public void testCreateUpgradeValidation() {
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createUpgrade( null, robotId, gameId, playerId );
        });
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createUpgrade( capability, null, gameId, playerId );
        });
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createUpgrade( capability, robotId, null, playerId );
        });
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createUpgrade( capability, robotId, gameId, null );
        });
    }

    @Test
    public void testCreateRegenValidation() {
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createRegeneration( null, gameId, playerId );
        });
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createRegeneration( robotId, null, playerId );
        });
        Assertions.assertThrows(DomainPrimitiveException.class, () -> {
            Command.createRegeneration( robotId, gameId, null );
        });
    }

    @Test
    public void testCommandType() {
        Assertions.assertEquals( CommandType.MOVEMENT, move.getCommandType() );
        Assertions.assertEquals( CommandType.BUYING, item.getCommandType() );
        Assertions.assertEquals( CommandType.BUYING, robot.getCommandType() );
        Assertions.assertEquals( CommandType.BUYING, upgrade.getCommandType() );
        Assertions.assertEquals( CommandType.REGENERATE, regen.getCommandType() );
    }

    @Test
    public void testPlayerId() {
        Assertions.assertEquals( playerId, move.getPlayerId() );
        Assertions.assertEquals( playerId, item.getPlayerId() );
        Assertions.assertEquals( playerId, robot.getPlayerId() );
        Assertions.assertEquals( playerId, upgrade.getPlayerId() );
        Assertions.assertEquals( playerId, regen.getPlayerId() );
    }

    @Test
    public void testRobotId() {
        Assertions.assertEquals( robotId, move.getCommandObject().getRobotId() );
        Assertions.assertEquals( robotId, item.getCommandObject().getRobotId() );
        Assertions.assertEquals( robotId, upgrade.getCommandObject().getRobotId() );
        Assertions.assertEquals( robotId, regen.getCommandObject().getRobotId() );
    }

    @Test
    public void testOtherProperties() {
        Assertions.assertEquals( itemType.name(), item.getCommandObject().getItemName() );
        Assertions.assertEquals( capability.toStringForCommand(), upgrade.getCommandObject().getItemName() );
    }


}

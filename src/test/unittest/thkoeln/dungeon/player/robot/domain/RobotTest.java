package unittest.thkoeln.dungeon.player.robot.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.robot.domain.Robot;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RobotTest {
    protected Robot robot;

    @BeforeEach
    public void setup() {
        robot = new Robot(UUID.randomUUID(), Player.ownPlayer("test", "test@test.test"), new Planet(UUID.randomUUID()), 20, 100);
    }

    @Test
    public void testAddResources() {
        MineableResource emptyResource = MineableResource.empty(MineableResourceType.COAL);
        MineableResource resourceCoal = MineableResource.fromTypeAndAmount(MineableResourceType.COAL, 2);
        MineableResource resourceIron = MineableResource.fromTypeAndAmount(MineableResourceType.IRON, 2);

        Assertions.assertEquals(2, emptyResource.add(resourceCoal).getAmount());

        robot.storeResources(resourceCoal);
        Assertions.assertEquals(2, robot.getInventory().getUsedCapacity());
        robot.storeResources(resourceCoal);
        Assertions.assertEquals(4, robot.getInventory().getUsedCapacity());
        robot.storeResources(resourceCoal);
        Assertions.assertEquals(6, robot.getInventory().getUsedCapacity());
        robot.storeResources(resourceIron);
        Assertions.assertEquals(8, robot.getInventory().getUsedCapacity());

        assertFalse(robot.isFull());
        MineableResource fillMax = MineableResource.fromTypeAndAmount(MineableResourceType.COAL, 12);
        robot.storeResources(fillMax);
        assertTrue(robot.isFull());
    }

}

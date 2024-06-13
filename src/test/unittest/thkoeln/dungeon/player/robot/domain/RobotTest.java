package thkoeln.dungeon.player.robot.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.player.domain.Player;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RobotTest {
    protected Robot robot;
    protected Player player;

    @BeforeEach
    public void setup() {
        player = Player.ownPlayer("test", "test@test.test");
        RobotDecisionMaker.clear();
        robot = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), 20, 20, 100);
    }

    @Test
    public void testTypeSetting() {
        Robot robotMiner1 = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), 20, 20, 100);
        Robot robotMiner2 = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), 20, 20, 100);
        Robot robotMiner3 = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), 20, 20, 100);

        Robot robotWarrior1 = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), 20, 20, 100);

        Assertions.assertEquals(RobotType.Scout, robot.getRobotType());
        Assertions.assertEquals(RobotType.Miner, robotMiner1.getRobotType());
        Assertions.assertEquals(RobotType.Miner, robotMiner2.getRobotType());
        Assertions.assertEquals(RobotType.Miner, robotMiner3.getRobotType());
        Assertions.assertEquals(RobotType.Warrior, robotWarrior1.getRobotType());
    }

    @Test
    public void testAddResources() {
        MineableResource emptyResource = MineableResource.empty(MineableResourceType.COAL);
        MineableResource resourceCoal = MineableResource.fromTypeAndAmount(MineableResourceType.COAL, 2);
        MineableResource resourceIron = MineableResource.fromTypeAndAmount(MineableResourceType.IRON, 2);

        Assertions.assertEquals(2, emptyResource.add(resourceCoal).getAmount());

        robot.setResourceInInventory(resourceCoal);
        Assertions.assertEquals(2, robot.getInventory().getUsedCapacity());
        robot.setResourceInInventory(resourceCoal);
        Assertions.assertEquals(4, robot.getInventory().getUsedCapacity());
        robot.setResourceInInventory(resourceCoal);
        Assertions.assertEquals(6, robot.getInventory().getUsedCapacity());
        robot.setResourceInInventory(resourceIron);
        Assertions.assertEquals(8, robot.getInventory().getUsedCapacity());

        assertFalse(robot.isFull());
        MineableResource fillMax = MineableResource.fromTypeAndAmount(MineableResourceType.COAL, 12);
        robot.setResourceInInventory(fillMax);
        assertTrue(robot.isFull());
    }

}

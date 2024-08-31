package thkoeln.dungeon.player.robot.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.player.domain.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RobotTest {
    protected List<Robot> robots = new ArrayList<>();
    protected Robot robot;
    protected Player player;

    @BeforeEach
    public void setup() {
        player = Player.ownPlayer("test", "test@test.test");
        RobotType type = RobotDecisionMaker.getNextRobotType(robots);
        robot = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), type, 20, 20, 100);
        robots.add(robot);
    }

    @Test
    public void testTypeSetting() {
        RobotType nextRobotType = RobotDecisionMaker.getNextRobotType(robots);
        Robot robotMiner1 = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), nextRobotType, 20, 20, 100);
        robots.add(robotMiner1);
        nextRobotType = RobotDecisionMaker.getNextRobotType(robots);
        Robot robotMiner2 = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), nextRobotType, 20, 20, 100);
        robots.add(robotMiner2);

        nextRobotType = RobotDecisionMaker.getNextRobotType(robots);
        Robot robotWarrior1 = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), nextRobotType, 20, 20, 100);
        robots.add(robotWarrior1);

        nextRobotType = RobotDecisionMaker.getNextRobotType(robots);
        Robot robotMiner3 = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), nextRobotType, 20, 20, 100);
        robots.add(robotMiner3);

        Assertions.assertEquals(RobotType.Scout, robot.getRobotType());
        Assertions.assertEquals(RobotType.Miner, robotMiner1.getRobotType());
        Assertions.assertEquals(RobotType.Miner, robotMiner2.getRobotType());
        Assertions.assertEquals(RobotType.Warrior, robotMiner3.getRobotType());
        Assertions.assertEquals(RobotType.Miner, robotWarrior1.getRobotType());
    }

    @Test
    public void testSetResources() {
        MineableResource resourceCoal = MineableResource.fromTypeAndAmount(MineableResourceType.COAL, 2);
        MineableResource resourceIron = MineableResource.fromTypeAndAmount(MineableResourceType.IRON, 2);

        robot.setResourceInInventory(resourceCoal);
        Assertions.assertEquals(2, robot.getInventory().getUsedCapacity());
        robot.setResourceInInventory(resourceIron);
        Assertions.assertEquals(4, robot.getInventory().getUsedCapacity());

        assertFalse(robot.isFull());
        MineableResource fillMax = MineableResource.fromTypeAndAmount(MineableResourceType.COAL, 18);
        robot.setResourceInInventory(fillMax);
        assertTrue(robot.isFull());
    }

    @Test
    public void testEnergy() {
        Assertions.assertFalse(robot.canNotMove());
        robot.setEnergy(10);
        Assertions.assertTrue(robot.canNotMove());
    }

}

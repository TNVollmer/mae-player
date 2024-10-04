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
import static thkoeln.dungeon.player.core.domainprimitives.robot.RobotType.*;

public class RobotTest {
    protected List<Robot> robots = new ArrayList<>();
    protected Robot robot;
    protected Player player;

    @BeforeEach
    public void setup() {
        RobotType.setDefaultType(MINER);
        RobotType.setPercentages(70, 30, 20);
        RobotType.setMaxCounts(0, 4, 10);
        RobotType.setCreateAfter(0,0, 10);
        RobotType.setUpgradeOrder(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        player = Player.ownPlayer("test", "test@test.test");
        robot = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), nextRobotType(), 20, 20, 100);
        robots.add(robot);
    }

    @Test
    public void testTypeSetting() {
        RobotType nextRobotType = nextRobotType();
        Robot robotMiner1 = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), nextRobotType, 20, 20, 100);
        robots.add(robotMiner1);
        nextRobotType = nextRobotType();
        Robot robotMiner2 = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), nextRobotType, 20, 20, 100);
        robots.add(robotMiner2);

        nextRobotType = nextRobotType();
        Robot robotWarrior1 = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), nextRobotType, 20, 20, 100);
        robots.add(robotWarrior1);

        nextRobotType = nextRobotType();
        Robot robotMiner3 = new Robot(UUID.randomUUID(), player, new Planet(UUID.randomUUID()), nextRobotType, 20, 20, 100);
        robots.add(robotMiner3);

        Assertions.assertEquals(SCOUT, robot.getRobotType());
        Assertions.assertEquals(MINER, robotMiner1.getRobotType());
        Assertions.assertEquals(MINER, robotMiner2.getRobotType());
        Assertions.assertEquals(SCOUT, robotMiner3.getRobotType());
        Assertions.assertEquals(MINER, robotWarrior1.getRobotType());
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

    private RobotType nextRobotType() {
        int robotCount = robots.size();
        int scoutCount = 0;
        int minerCount = 0;
        int warriorCount = 0;

        for (Robot r : robots) {
            switch (r.getRobotType()) {
                case MINER -> minerCount++;
                case SCOUT -> scoutCount++;
                case WARRIOR -> warriorCount++;
            }
        }

        if (robotCount == 0 || scoutCount < SCOUT.maxCount() && (scoutCount * 100 / robotCount) < SCOUT.percentage())
            return SCOUT;

        if ((minerCount * 100 / robotCount) < MINER.percentage())
            return MINER;

        if ((warriorCount * 100 / robotCount) < WARRIOR.percentage() && robotCount >= WARRIOR.createAfter())
            return WARRIOR;

        return RobotType.getDefaultType();
    }

}

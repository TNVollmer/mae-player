package integrationtest.thkoeln.dungeon.player.robot.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import thkoeln.dungeon.player.DungeonPlayerConfiguration;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;
import thkoeln.dungeon.player.core.events.concreteevents.robot.move.RobotMovePlanetDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.move.RobotMovedEvent;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.planet.domain.PlanetRepository;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;
import thkoeln.dungeon.player.robot.application.RobotApplicationService;
import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static thkoeln.dungeon.player.core.domainprimitives.robot.RobotType.MINER;
import static thkoeln.dungeon.player.core.domainprimitives.robot.RobotType.SCOUT;

@DirtiesContext
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
@ActiveProfiles( "test" )
public class RobotApplicationServiceTest {

    protected Player player;
    protected Robot s1;
    protected Planet p1, p2, p3;

    protected RobotApplicationService robotApplicationService;

    @Autowired
    protected RobotRepository robotRepository;
    @Autowired
    protected PlanetRepository planetRepository;
    @Autowired
    protected PlayerRepository playerRepository;

    @BeforeEach
    public void setUp() {
        robotApplicationService = new RobotApplicationService(robotRepository, planetRepository, playerRepository);
        player = Player.ownPlayer("", "");
        player.assignPlayerId(UUID.randomUUID());
        playerRepository.save(player);


        robotRepository.deleteAll();

        p1 = new Planet(UUID.randomUUID());
        p2 = new Planet(UUID.randomUUID());
        p3 = new Planet(UUID.randomUUID());

        planetRepository.save(p1);
        planetRepository.save(p2);
        planetRepository.save(p3);

        s1 = new Robot(UUID.randomUUID(), player, p1, RobotType.SCOUT, 20, 15, 10);
        robotRepository.save(s1);
    }

    @Test
    public void testRobotSaved() {
        List<Robot> robots = new ArrayList<>();
        robotRepository.findAll().forEach(robots::add);
        assertEquals(1, robots.size());

        UUID robotId = UUID.randomUUID();

        Robot robot = new Robot(robotId, player, p1, RobotType.SCOUT, 20, 15, 10);
        robotRepository.save(robot);

        robots.clear();
        robotRepository.findAll().forEach(robots::add);

        assertEquals(2, robots.size());
    }

    @Test
    public void testRobotMovedEvent() {
        RobotMovedEvent movedEvent = new RobotMovedEvent();
        movedEvent.setRobotId(s1.getRobotId());

        RobotMovePlanetDto robotMovePlanetDtoFrom = new RobotMovePlanetDto();
        robotMovePlanetDtoFrom.setId(p1.getPlanetId());
        robotMovePlanetDtoFrom.setMovementDifficulty(1);
        movedEvent.setFromPlanet(robotMovePlanetDtoFrom);

        RobotMovePlanetDto robotMovePlanetDtoTo = new RobotMovePlanetDto();
        robotMovePlanetDtoTo.setId(p2.getPlanetId());

        movedEvent.setToPlanet(robotMovePlanetDtoTo);

        robotApplicationService.onRobotMoved(movedEvent);
        Robot robot = robotApplicationService.getRobot(s1.getRobotId());

        assertEquals(s1.getId(), robot.getId());

        assertEquals(robot.getRobotId(), s1.getRobotId());
        assertEquals(robot.getPlanet().getPlanetId(), p2.getPlanetId());
    }

    @Test
    public void testTypeSetting() {
        robotRepository.deleteAll();

        RobotType nextRobotType = robotApplicationService.nextRobotType();
        Robot robotScout1 = new Robot(UUID.randomUUID(), player, p1, nextRobotType, 20,20,100);
        robotRepository.save(robotScout1);

        nextRobotType = robotApplicationService.nextRobotType();
        Robot robotMiner1 = new Robot(UUID.randomUUID(), player, p1, nextRobotType, 20, 20, 100);
        robotRepository.save(robotMiner1);
        nextRobotType = robotApplicationService.nextRobotType();
        Robot robotMiner2 = new Robot(UUID.randomUUID(), player, p1, nextRobotType, 20, 20, 100);
        robotRepository.save(robotMiner2);

        nextRobotType = robotApplicationService.nextRobotType();
        Robot robotMiner3 = new Robot(UUID.randomUUID(), player, p1, nextRobotType, 20, 20, 100);
        robotRepository.save(robotMiner3);

        nextRobotType = robotApplicationService.nextRobotType();
        Robot robotScout2 = new Robot(UUID.randomUUID(), player, p1, nextRobotType, 20, 20, 100);
        robotRepository.save(robotScout2);

        assertEquals(5, robotRepository.count());

        assertEquals(SCOUT, robotScout1.getRobotType());
        assertEquals(MINER, robotMiner1.getRobotType());
        assertEquals(MINER, robotMiner2.getRobotType());
        assertEquals(MINER, robotMiner3.getRobotType());
        assertEquals(SCOUT, robotScout2.getRobotType());
    }

}

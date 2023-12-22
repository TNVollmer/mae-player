package thkoeln.dungeon.player.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetNeighboursDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotRevealedDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotsRevealedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;
import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotPlanet;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class RobotEventListener {

    private final Logger logger = LoggerFactory.getLogger(RobotApplicationService.class);
    private final RobotRepository robotRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public RobotEventListener(RobotRepository robotRepository, PlayerRepository playerRepository) {
        this.robotRepository = robotRepository;
        this.playerRepository = playerRepository;
    }

    @EventListener(RobotsRevealedEvent.class)
    public void displayRobotData(RobotsRevealedEvent robotsRevealedEvent) {
        logger.info("Robot data: " + Arrays.toString(robotsRevealedEvent.getRobots()));
    }

    @EventListener(RobotSpawnedEvent.class)
    public void saveNewRobot(RobotSpawnedEvent robotSpawnedEvent) {
        RobotDto robotDto = robotSpawnedEvent.getRobotDto();
        Robot newRobot = new Robot(robotDto.getId(), ("Robot" + robotRepository.findAll().size()), robotDto.getPlanet().getPlanetId());
        robotRepository.save(newRobot);
        logger.info("Robot spawned: " + newRobot);
        Player player = playerRepository.findAll().get(0);
        player.getRobots().add(newRobot);
        playerRepository.save(player);
    }

    @EventListener(RobotsRevealedEvent.class)
    public void saveRobot(RobotsRevealedEvent robotsRevealedEvent) {
        List<Robot> robots = robotRepository.findAll();
        for (Robot robot : robots) {
            for (RobotRevealedDto robotRevealedDto : robotsRevealedEvent.getRobots()) {
                if (robot.getId().equals(robotRevealedDto.getRobotId())) {
                    robot.setRobotPlanet(RobotPlanet.planetWithoutNeighbours(robotRevealedDto.getPlanetId()));
                    robotRepository.save(robot);
                    logger.info("Updated robot: " + robot.getId() + " with planet: " + robotRevealedDto.getPlanetId());
                }
            }
        }
    }

    @EventListener(PlanetDiscoveredEvent.class)
    public void savePlanet(PlanetDiscoveredEvent planetDiscoveredEvent) {
        List<Robot> robotsOnPlanet = robotRepository.findByRobotPlanetPlanetId(planetDiscoveredEvent.getPlanetId());
        if (robotsOnPlanet.isEmpty()) {
            logger.info("No robots on planet: " + planetDiscoveredEvent.getPlanetId());
            return;
        }
        PlanetNeighboursDto[] planetNeighbours = planetDiscoveredEvent.getNeighbours();
        for (Robot robot : robotsOnPlanet) {
            RobotPlanet updatedRobotPlanet = RobotPlanet.planetWithNeighbours(
                    planetDiscoveredEvent.getPlanetId(),
                    planetNeighbours,
                    planetDiscoveredEvent.getMovementDifficulty(),
                    MineableResource.fromTypeAndAmount(planetDiscoveredEvent.getResource().getResourceType(), planetDiscoveredEvent.getResource().getCurrentAmount())
            );
            robot.setRobotPlanet(updatedRobotPlanet);
            robotRepository.save(robot);
            logger.info("Updated robot: " + robot.getId() + " with planet: " + updatedRobotPlanet);
        }
    }
}

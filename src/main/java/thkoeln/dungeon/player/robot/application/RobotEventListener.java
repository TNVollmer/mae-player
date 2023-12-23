package thkoeln.dungeon.player.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotInventory;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetNeighboursDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.mine.RobotResourceMinedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.move.RobotMovedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotRevealedDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotsRevealedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
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

    private final PlayerApplicationService playerApplicationService;

    @Autowired
    public RobotEventListener(RobotRepository robotRepository, PlayerRepository playerRepository, PlayerApplicationService playerApplicationService) {
        this.robotRepository = robotRepository;
        this.playerRepository = playerRepository;
        this.playerApplicationService = playerApplicationService;
    }

    @EventListener(RobotsRevealedEvent.class)
    public void displayRobotData(RobotsRevealedEvent robotsRevealedEvent) {
        logger.info("Robots revealed: " + Arrays.asList(robotsRevealedEvent.getRobots()).size());
    }

    @EventListener(RobotSpawnedEvent.class)
    public void saveNewRobot(RobotSpawnedEvent robotSpawnedEvent) {
        RobotDto robotDto = robotSpawnedEvent.getRobotDto();
        Robot newRobot = Robot.of(robotDto, ("Robot" + (robotRepository.findAll().size() + 1)));
        robotRepository.save(newRobot);
        logger.info("Robot spawned: " + newRobot.getRobotId());
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
        player.getRobots().add(newRobot);
        playerRepository.save(player);
    }

    @EventListener(RobotsRevealedEvent.class)
    public void updateRobot(RobotsRevealedEvent robotsRevealedEvent) {
        List<Robot> robots = robotRepository.findAll();
        for (Robot robot : robots) {
            for (RobotRevealedDto robotRevealedDto : robotsRevealedEvent.getRobots()) {
                if (robot.getRobotId().equals(robotRevealedDto.getRobotId())) {
                    if (!robot.getRobotPlanet().getPlanetId().equals(robotRevealedDto.getPlanetId())) {
                        robot.setRobotPlanet(RobotPlanet.planetWithoutNeighbours(robotRevealedDto.getPlanetId()));
                        logger.info("Updated robot: " + robot.getRobotId() + " with planet: " + robotRevealedDto.getPlanetId());
                    }
                    robot.setEnergy(robotRevealedDto.getEnergy());
                    robot.setHealth(robotRevealedDto.getHealth());
                    robotRepository.save(robot);
                }
            }
        }
    }

    @EventListener(PlanetDiscoveredEvent.class)
    public void savePlanet(PlanetDiscoveredEvent planetDiscoveredEvent) {
        List<Robot> robotsOnPlanet = robotRepository.findByRobotPlanetPlanetId(planetDiscoveredEvent.getPlanetId());
        if (robotsOnPlanet.isEmpty()) {
            logger.error("No robots on planet: " + planetDiscoveredEvent.getPlanetId());
            return;
        }
        PlanetNeighboursDto[] planetNeighbours = planetDiscoveredEvent.getNeighbours();
        for (Robot robot : robotsOnPlanet) {
            RobotPlanet updatedRobotPlanet;
            try {
                updatedRobotPlanet = RobotPlanet.planetWithNeighbours(
                        planetDiscoveredEvent.getPlanetId(),
                        planetNeighbours,
                        planetDiscoveredEvent.getMovementDifficulty(),
                        MineableResource.fromTypeAndAmount(planetDiscoveredEvent.getResource().getResourceType(), planetDiscoveredEvent.getResource().getCurrentAmount())
                );
                logger.info("RESOURCE --> Mineable resource found: " + updatedRobotPlanet.getMineableResource().getType() + " with amount: " + updatedRobotPlanet.getMineableResource().getAmount());
            } catch (NullPointerException e) {
                logger.info("RESOURCE --> No mineable resource on planet: " + planetDiscoveredEvent.getPlanetId());
                updatedRobotPlanet = RobotPlanet.planetWithNeighbours(
                        planetDiscoveredEvent.getPlanetId(),
                        planetNeighbours,
                        planetDiscoveredEvent.getMovementDifficulty(),
                        null
                );
            }
            robot.setRobotPlanet(updatedRobotPlanet);
            robotRepository.save(robot);
            logger.info("Updated robot: " + robot.getRobotId() + " with planet: " + updatedRobotPlanet.getPlanetId());
        }
    }

    @EventListener(RobotResourceMinedEvent.class)
    public void updatePlanetAndRobotResource(RobotResourceMinedEvent robotResourceMinedEvent) {
        Robot robot = robotRepository.findByRobotId(robotResourceMinedEvent.getRobotId());
        robot.getRobotPlanet().updateMineableResource(robotResourceMinedEvent.minedResourceAsDomainPrimitive());
        robot.getRobotInventory().updateResource(robotResourceMinedEvent.minedResourceAsDomainPrimitive());
        robotRepository.save(robot);
        logger.info("Updated robot: " + robot.getRobotId() + " with planet: " + robot.getRobotPlanet() + ". Mined new resource: " + robotResourceMinedEvent.getMinedResource() + " with amount: " + robotResourceMinedEvent.getMinedAmount());
    }

    @EventListener(RobotMovedEvent.class)
    public void updateRobotPlanet(RobotMovedEvent robotMovedEvent) {
        Robot robot = robotRepository.findByRobotId(robotMovedEvent.getRobotId());
        robot.setRobotPlanet(RobotPlanet.planetWithoutNeighbours(robotMovedEvent.getToPlanet().getId()));
        robot.getRobotPlanet().setMovementDifficulty(robotMovedEvent.getToPlanet().getMovementDifficulty());
        robot.setEnergy(robotMovedEvent.getRemainingEnergy());
        robotRepository.save(robot);
        logger.info("Updated robot: " + robot.getRobotId() + " with planet: " + robotMovedEvent.getToPlanet().getId());
    }
}

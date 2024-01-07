package thkoeln.dungeon.player.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetNeighboursDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.RobotRegeneratedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.mine.RobotResourceMinedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.mine.RobotResourceRemovedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.move.RobotMovedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotRevealedDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotsRevealedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotPlanet;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class RobotEventListener {

    private final Logger logger = LoggerFactory.getLogger(RobotApplicationService.class);
    private final RobotRepository robotRepository;

    @Autowired
    public RobotEventListener(RobotRepository robotRepository) {
        this.robotRepository = robotRepository;
    }

    @EventListener(RobotsRevealedEvent.class)
    private void displayRobotData(RobotsRevealedEvent robotsRevealedEvent) {
        logger.info("Robots revealed: " + Arrays.asList(robotsRevealedEvent.getRobots()).size());
    }

    @EventListener(RobotSpawnedEvent.class)
    private void saveNewRobot(RobotSpawnedEvent robotSpawnedEvent) {
        RobotDto robotDto = robotSpawnedEvent.getRobotDto();
        Robot newRobot = Robot.of(robotDto, ("Robot" + (robotRepository.findAll().size() + 1)));
        newRobot.setPlayerOwned(true);
        robotRepository.save(newRobot);
        logger.info("Robot spawned: " + newRobot.getRobotId());
    }

    @EventListener(RobotsRevealedEvent.class)
    private void updateRobot(RobotsRevealedEvent robotsRevealedEvent) {
        List<Robot> robots = robotRepository.findByPlayerOwned(true);
        for (RobotRevealedDto robotRevealedDto : robotsRevealedEvent.getRobots()) {
            boolean isEnemyRobot = true;
            for (Robot robot : robots) {
                if (robot.getRobotId().equals(robotRevealedDto.getRobotId())) {
                    if (!robot.getRobotPlanet().getPlanetId().equals(robotRevealedDto.getPlanetId())) {
                        robot.setRobotPlanet(RobotPlanet.planetWithoutNeighbours(robotRevealedDto.getPlanetId()));
                        logger.info("Updated robot: " + robot.getRobotId() + " with planet: " + robotRevealedDto.getPlanetId());
                    }
                    robot.setEnergy(robotRevealedDto.getEnergy());
                    robot.setHealth(robotRevealedDto.getHealth());
                    robot.setDamageLevel(robotRevealedDto.getLevels().getDamageLevel());
                    robot.setMiningLevel(robotRevealedDto.getLevels().getMiningLevel());
                    robot.setMiningSpeedLevel(robotRevealedDto.getLevels().getMiningSpeedLevel());
                    robot.setEnergyLevel(robotRevealedDto.getLevels().getEnergyLevel());
                    robot.setEnergyRegenLevel(robotRevealedDto.getLevels().getEnergyRegenLevel());
                    robot.setHealthLevel(robotRevealedDto.getLevels().getHealthLevel());
                    isEnemyRobot = false;
                    robotRepository.save(robot);
                }
            }
            if (isEnemyRobot) {
                List<Robot> enemyRobots = robotRepository.findByPlayerOwned(false);
                for (Robot enemyRobot : enemyRobots) {
                    if (enemyRobot.getRobotId().equals(robotRevealedDto.getRobotId())) {
                        if (!enemyRobot.getRobotPlanet().getPlanetId().equals(robotRevealedDto.getPlanetId())) {
                            enemyRobot.setRobotPlanet(RobotPlanet.planetWithoutNeighbours(robotRevealedDto.getPlanetId()));
                            logger.info("Updated enemy robot: " + enemyRobot.getRobotId() + " with planet: " + robotRevealedDto.getPlanetId());
                        }
                        enemyRobot.setEnergy(robotRevealedDto.getEnergy());
                        enemyRobot.setHealth(robotRevealedDto.getHealth());
                        robotRepository.save(enemyRobot);
                    } else {
                        logger.error("WARNING --> ENEMY ROBOT DETECTED: " + robotRevealedDto.getRobotId() + " on planet: " + robotRevealedDto.getPlanetId());
                        Robot newEnemyRobot = Robot.ofEnemy(robotRevealedDto, "Enemy Robot");
                        robotRepository.save(newEnemyRobot);
                    }
                }
            }
        }
    }

    @EventListener(PlanetDiscoveredEvent.class)
    private void savePlanet(PlanetDiscoveredEvent planetDiscoveredEvent) {
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
    private void updatePlanetAndRobotResource(RobotResourceMinedEvent robotResourceMinedEvent) {
        Robot robot = robotRepository.findByRobotId(robotResourceMinedEvent.getRobotId());
        robot.getRobotPlanet().updateMineableResource(robotResourceMinedEvent.minedResourceAsDomainPrimitive());
        robot.getRobotInventory().updateResource(robotResourceMinedEvent.minedResourceAsDomainPrimitive());
        robotRepository.save(robot);
        logger.info("Updated robot: " + robot.getRobotId() + " with planet: " + robot.getRobotPlanet() + ". Mined new resource: " + robotResourceMinedEvent.getMinedResource() + " with amount: " + robotResourceMinedEvent.getMinedAmount());
    }

    @EventListener(RobotMovedEvent.class)
    private void updateRobotPlanet(RobotMovedEvent robotMovedEvent) {
        Robot robot = robotRepository.findByRobotId(robotMovedEvent.getRobotId());
        robot.setRobotPlanet(RobotPlanet.planetWithoutNeighbours(robotMovedEvent.getToPlanet().getId()));
        robot.getRobotPlanet().setMovementDifficulty(robotMovedEvent.getToPlanet().getMovementDifficulty());
        robot.setEnergy(robotMovedEvent.getRemainingEnergy());
        robotRepository.save(robot);
        logger.info("Updated robot: " + robot.getRobotId() + " with planet: " + robotMovedEvent.getToPlanet().getId());
    }

    @EventListener(RobotResourceRemovedEvent.class)
    private void removeRobotResource(RobotResourceRemovedEvent robotResourceRemovedEvent) {
        Robot robot = robotRepository.findByRobotId(robotResourceRemovedEvent.getRobotId());
        robot.getRobotInventory().removeResource(robotResourceRemovedEvent.removedResourceAsDomainPrimitive());
        robotRepository.save(robot);
        logger.info("Removed resource from robot: " + robot.getRobotId() + " with resource: " + robotResourceRemovedEvent.getRemovedResource() + " with amount: " + robotResourceRemovedEvent.getRemovedAmount());
    }

    @EventListener(RobotRegeneratedEvent.class)
    private void updateRobotEnergy(RobotRegeneratedEvent robotRegeneratedEvent) {
        Robot robot = robotRepository.findByRobotId(robotRegeneratedEvent.getRobotId());
        robot.setEnergy(robotRegeneratedEvent.getAvailableEnergy());
        robotRepository.save(robot);
        logger.info("Updated robot: " + robot.getRobotId() + " with energy: " + robotRegeneratedEvent.getAvailableEnergy());
    }
}

package thkoeln.dungeon.player.robot.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.domainprimitives.command.CommandType;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.CapabilityType;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;
import thkoeln.dungeon.player.core.events.concreteevents.robot.change.RobotRegeneratedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.change.RobotRestoredAttributesEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.change.RobotUpgradedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.fight.RobotAttackedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.mine.RobotResourceMinedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.mine.RobotResourceRemovedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.move.RobotMovedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotRevealedDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotsRevealedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.planet.domain.PlanetRepository;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;
import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotDecisionMaker;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RobotApplicationService {
    private final RobotRepository robotRepository;
    private final PlanetRepository planetRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public RobotApplicationService(RobotRepository robotRepository, PlanetRepository planetRepository, PlayerRepository playerRepository) {
        this.robotRepository = robotRepository;
        this.planetRepository = planetRepository;
        this.playerRepository = playerRepository;
    }

    @EventListener(RobotSpawnedEvent.class)
    public void onRobotSpawned(RobotSpawnedEvent event) {
        RobotDto dto = event.getRobotDto();
        UUID id = dto.getId();
        Player player = playerRepository.findAll().get(0);
        Planet planet = getPlanet(dto.getPlanet().getPlanetId());
        planet.setMovementDifficulty(dto.getPlanet().getMovementDifficulty());
        if (planet.getResources() == null && dto.getPlanet().getResourceType() != null) {
            planet.setResources(MineableResource.fromTypeAndAmount(MineableResourceType.valueOf(dto.getPlanet().getResourceType()), 1));
            log.info("Due to Robot discovered set Planet {} Resources: {}", planet.getPlanetId(), planet.getResources());
        }
        planetRepository.save(planet);
        RobotType type = RobotDecisionMaker.getNextRobotType(robotRepository.findAll());
        Robot robot =  new Robot(id, player, planet, type, dto.getInventory().getMaxStorage(), dto.getMaxEnergy(), dto.getMaxHealth());
        choseNextTask(robot);
        log.info("Robot {} ({}) spawned!", robot.getRobotId(), robot.getRobotType());
    }

    @Async
    @EventListener(RobotsRevealedEvent.class)
    public void onRobotsRevealed(RobotsRevealedEvent event) {
        List<UUID> ids = getAllRobotIDs();
        List<Robot> warriors = robotRepository.findByRobotType(RobotType.Warrior);
        RobotRevealedDto[] revealedRobots = event.getRobots();
        Integer count = 0;
        for (RobotRevealedDto robotRevealedDto : revealedRobots) {
            if (ids.contains(robotRevealedDto.getRobotId())) continue;
            count++;
            for (Robot robot : warriors) {
                if (robot.hasCommand() && robot.getCommandType() != CommandType.MOVEMENT) continue;
                if (robot.getPlanet().getPlanetId() == robotRevealedDto.getPlanetId()) {
                    log.info("Enemy Robot {} is on the same Planet as {}", robotRevealedDto.getRobotId(), robot.getRobotId());
                    robot.clearQueue();
                    robot.queueFirst(
                            Command.createFight(robot.getRobotId(), robot.getPlayer().getGameId(), robot.getPlayer().getPlayerId(), robotRevealedDto.getRobotId())
                    );
                    log.info("Robot {} ({}) now attacks {}", robot.getRobotId(), robot.getRobotType(), robotRevealedDto.getRobotId());
                }
            }
        }
        log.info("{} Robots revealed / {} are enemies", revealedRobots.length, count);
        robotRepository.saveAll(warriors);
    }

    @Async
    @EventListener(RobotMovedEvent.class)
    public void onRobotMoved(RobotMovedEvent event) {
        Robot robot = getRobot(event.getRobotId());
        Planet planet = getPlanet(event.getToPlanet().getId());
        if (planet.getMovementDifficulty() == null) {
            planet.setMovementDifficulty(event.getToPlanet().getMovementDifficulty());
            planetRepository.save(planet);
        }
        robot.move(planet);
        robot.setEnergy(event.getRemainingEnergy());
        choseNextTask(robot);
        log.info("Robot {} ({}) has {} Energy left and moved to Planet {}", robot.getRobotId(), robot.getRobotType(), robot.getEnergy(), planet.getId());
    }

    @Async
    @EventListener(RobotRegeneratedEvent.class)
    public void onRobotRegenerated(RobotRegeneratedEvent event) {
        Robot robot = getRobot(event.getRobotId());
        robot.setEnergy(event.getAvailableEnergy());
        choseNextTask(robot);
        log.info("Robot {} ({}): regenerated to {} Energy", robot.getRobotId(), robot.getRobotType(), robot.getEnergy());
    }

    @Async
    @EventListener(RobotRestoredAttributesEvent.class)
    public void onRobotAttributesRestored(RobotRestoredAttributesEvent event) {
        Robot robot = getRobot(event.getRobotId());
        robot.setEnergy(event.getAvailableEnergy());
        robot.setHealth(event.getAvailableHealth());
        choseNextTask(robot);
        log.info("Robot {} ({}): attributes restored!", robot.getRobotId(), robot.getRobotType());
    }

    @Async
    @EventListener(RobotResourceMinedEvent.class)
    public void onRobotResourceMined(RobotResourceMinedEvent event) {
        Robot robot = getRobot(event.getRobotId());
        robot.setResourceInInventory(event.getResourceInventory().getResource());
        choseNextTask(robot);
        log.info("Robot {} ({}) mined: {} {}", robot.getRobotId(), robot.getRobotType(), event.getMinedAmount(), event.getMinedResource());
        log.info("Robot {} ({}): Inventory: {}", robot.getRobotId(), robot.getRobotType(), robot.getInventory().getUsedCapacity());
    }

    @Async
    @EventListener(RobotResourceRemovedEvent.class)
    public void onResourceRemoved(RobotResourceRemovedEvent event) {
        Robot robot = getRobot(event.getRobotId());
        robot.setResourceInInventory(MineableResource.fromTypeAndAmount(MineableResourceType.valueOf(event.getRemovedResource()), 0));
        choseNextTask(robot);
        log.info("Robot {} ({}) resource removed: {} {}", robot.getRobotId(), robot.getRobotType(), event.getRemovedAmount(), event.getRemovedResource());
    }

    @Async
    @EventListener(RobotAttackedEvent.class)
    public void onRobotAttacked(RobotAttackedEvent event) {
        List<UUID> ids = getAllRobotIDs();

        if (ids.contains(event.getAttacker().getRobotId())) {
            Robot attacker = getRobot(event.getAttacker().getRobotId());
            attacker.setEnergy(event.getAttacker().getAvailableEnergy());
            choseNextTask(attacker);
            log.info("Robot {} ({}) attacked {}", attacker.getRobotId(), attacker.getRobotType(), event.getTarget().getRobotId());
        }

        if (ids.contains(event.getTarget().getRobotId())) {
            Robot target = getRobot(event.getTarget().getRobotId());
            log.info("Robot {} ({}) was attacked by {}", target.getRobotId(), target.getRobotType(), event.getAttacker().getRobotId());
            target.setHealth(event.getTarget().getAvailableHealth());
            if (event.getTarget().getAlive()) {
                target.executeOnAttackBehaviour();
                robotRepository.save(target);
            } else {
                robotRepository.delete(target);
            }
        }
    }

    @Async
    @EventListener(RobotUpgradedEvent.class)
    public void onRobotUpgrade(RobotUpgradedEvent event) {
        RobotDto dto = event.getRobotDto();
        Robot robot = getRobot(event.getRobotId());
        robot.upgradeCapability(CapabilityType.valueOf(event.getUpgrade()));
        robot.changeInventorySize(dto.getInventory().getMaxStorage());
        robot.setMaxEnergy(dto.getMaxEnergy());
        robot.setMaxHealth(dto.getMaxHealth());
        robot.setDamage(dto.getAttackDamage());
        robot.chooseNextUpgrade();
        robotRepository.save(robot);
        log.info("Robot {} ({}) Upgrading: {}", robot.getRobotId(), robot.getRobotType(), event.getUpgrade());
    }

    private List<UUID> getAllRobotIDs() {
        List<UUID> ids = new ArrayList<>();
        for (Robot robot : robotRepository.findAll()) {
            ids.add(robot.getRobotId());
        }
        return ids;
    }

    private void choseNextTask(Robot robot) {
        if (robot.hasCommand()) robot.removeCommand();
        if (!robot.hasCommand() && robot.getPlanet().isExplored())
            robot.chooseNextCommand();
        if (robot.getCommandType() == CommandType.MOVEMENT && robot.canNotMove())
            robot.queueFirst(Command.createRegeneration(robot.getRobotId(), robot.getPlayer().getGameId(), robot.getPlayer().getPlayerId()));
        robotRepository.save(robot);
        log.info("Robot {} ({}) Next Command: {} (Queue size: {})", robot.getRobotId(), robot.getRobotType(), robot.getCommandType(), robot.getQueueSize());
    }

    private Robot getRobot(UUID robotId) {
        return robotRepository.findByRobotId(robotId).orElseThrow(() -> new RuntimeException("No robot found with id: " + robotId));
    }

    private Planet getPlanet(UUID planetId) {
        return planetRepository.findByPlanetId(planetId).orElse(new Planet(planetId));
    }
}

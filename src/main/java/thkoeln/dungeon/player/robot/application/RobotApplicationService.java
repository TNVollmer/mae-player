package thkoeln.dungeon.player.robot.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.CapabilityType;
import thkoeln.dungeon.player.core.events.concreteevents.robot.change.RobotRegeneratedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.change.RobotUpgradedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.fight.RobotAttackedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.mine.RobotResourceMinedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.mine.RobotResourceRemovedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.move.RobotMovedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.planet.domain.PlanetRepository;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;
import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

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

    @Async
    @EventListener(RobotSpawnedEvent.class)
    public void onRobotSpawned(RobotSpawnedEvent event) {
        Robot robot = createFromDto(event.getRobotDto());
        planetRepository.save(robot.getPlanet());
        //TODO: assign robot type
        choseNextTask(robot);
        log.info("Robot {} spawned!", robot.getRobotId());
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
    }

    @Async
    @EventListener(RobotRegeneratedEvent.class)
    public void onRobotRegenerated(RobotRegeneratedEvent event) {
        Robot robot = getRobot(event.getRobotId());
        robot.setEnergy(event.getAvailableEnergy());
        choseNextTask(robot);
    }

    @Async
    @EventListener(RobotResourceMinedEvent.class)
    public void onRobotResourceMined(RobotResourceMinedEvent event) {
        Robot robot = getRobot(event.getRobotId());

        MineableResource minedResource = MineableResource.fromTypeAndAmount(MineableResourceType.valueOf(event.getMinedResource()), event.getMinedAmount());
        robot.storeResources(minedResource);
        log.info("Robot {} mined: {} {}", robot.getId(), event.getMinedAmount(), event.getMinedResource());
        log.info("Inventory: {}", robot.getInventory().getUsedCapacity());

        choseNextTask(robot);
    }

    @Async
    @EventListener(RobotResourceRemovedEvent.class)
    public void onResourceRemoved(RobotResourceRemovedEvent event) {
        Robot robot = getRobot(event.getRobotId());
        robot.removeResources(MineableResource.fromTypeAndAmount(MineableResourceType.valueOf(event.getRemovedResource()), event.getRemovedAmount()));
        choseNextTask(robot);
    }

    @Async
    @EventListener(RobotAttackedEvent.class)
    public void onRobotAttacked(RobotAttackedEvent event) {
        Robot attacker = getRobot(event.getAttacker().getRobotId());
        Robot target = getRobot(event.getTarget().getRobotId());
        target.setHealth(event.getTarget().getAvailableHealth());

        //TODO: get player and check if target is once own robot
        //if (target.getPlayer() == )
        target.escape();
    }

    @EventListener(RobotUpgradedEvent.class)
    public void onRobotUpgrade(RobotUpgradedEvent event) {
        Robot robot = getRobot(event.getRobotId());
        //TODO: better upgrading
        robot.upgradeCapability(CapabilityType.valueOf(event.getUpgrade()));
        updateFromDto(robot, event.getRobotDto());
        log.info("Upgrading {}: {}", robot.getRobotId(), event.getUpgrade());
        choseNextTask(robot);
    }

    private Robot createFromDto(RobotDto dto) {
        UUID id = dto.getId();
        Player player = playerRepository.findByPlayerId(dto.getPlayer()).get();
        Planet planet = getPlanet(dto.getPlanet().getPlanetId());
        planet.setMovementDifficulty(dto.getPlanet().getMovementDifficulty());
        if (planet.getResources() == null && dto.getPlanet().getResourceType() != null)
            planet.setResources(MineableResource.fromTypeAndAmount(MineableResourceType.valueOf(dto.getPlanet().getResourceType()), 1));
        log.info("Planet {} Resources: {}", planet.getPlanetId(), planet.getResources());

        return new Robot(id, player, planet, dto.getInventory().getMaxStorage(), dto.getEnergy());
    }

    private void updateFromDto(Robot robot, RobotDto dto) {
        robot.changeInventorySize(dto.getInventory().getMaxStorage());
        robot.setMaxEnergy(dto.getMaxEnergy());
        robot.setMaxHealth(dto.getMaxHealth());
    }

    private void choseNextTask(Robot robot) {
        if (!robot.hasCommand()) robot.chooseNextCommand();
        robotRepository.save(robot);
        log.info("Next Command for {}: {}", robot.getRobotId(), robot.getCommandType());
    }

    private Robot getRobot(UUID robotId) {
        return robotRepository.findByRobotId(robotId).orElseThrow(() -> new RuntimeException("No robot found with id: " + robotId));
    }

    private Planet getPlanet(UUID planetId) {
        return planetRepository.findByPlanetId(planetId).orElse(new Planet(planetId));
    }
}

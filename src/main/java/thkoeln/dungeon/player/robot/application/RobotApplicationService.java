package thkoeln.dungeon.player.robot.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.core.events.concreteevents.robot.mine.RobotResourceMinedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotPlanetDto;
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

    @EventListener(RobotSpawnedEvent.class)
    public void onRobotSpawned(RobotSpawnedEvent event) {
        Player player = playerRepository.findAll().get(0);
        Planet planet = getPlanet(event.getRobotDto().getPlanet());
        Robot robot = new Robot(event.getRobotDto().getId(), player, planet);
        planetRepository.save(robot.getPlanet());
        robotRepository.save(robot);
        log.info("Robot {} spawned!", robot.getId());
    }

    @EventListener(RobotResourceMinedEvent.class)
    public void onRobotResourceMined(RobotResourceMinedEvent event) {
        Robot robot = getRobot(event.getRobotId());

        MineableResource minedResource = event.getResourceInventory().getResource();
        robot.setInventory(robot.getInventory().addMineableResource(minedResource));
        log.info("Robot {} mined: {} {}", robot.getId(), event.getMinedAmount(), event.getMinedResource());

        robotRepository.save(robot);
    }

    private Robot getRobot(UUID robotId) {
        return robotRepository.findByRobotId(robotId).orElseThrow(() -> new RuntimeException("No robot found with id: " + robotId));
    }

    private Planet getPlanet(RobotPlanetDto planetDto) {
        return planetRepository.findByPlanetId(planetDto.getPlanetId()).orElse(
                new Planet(planetDto.getPlanetId(), MineableResource.empty(MineableResourceType.valueOf(planetDto.getResourceType()))));
    }
}

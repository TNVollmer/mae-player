package thkoeln.dungeon.player.planet.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetNeighboursDto;
import thkoeln.dungeon.player.core.events.concreteevents.planet.ResourceMinedEvent;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.planet.domain.PlanetRepository;
import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PlanetApplicationService {

    private final PlanetRepository planetRepository;
    private final RobotRepository robotRepository;

    @Autowired
    public PlanetApplicationService(PlanetRepository planetRepository, RobotRepository robotRepository) {
        this.planetRepository = planetRepository;
        this.robotRepository = robotRepository;
    }

    @EventListener(PlanetDiscoveredEvent.class)
    public void onPlanetDiscovered(PlanetDiscoveredEvent event) {
        Planet planet = getPlanetOrCreate(event.getPlanetId());
        planet.explore();
        planet.setMovementDifficulty(event.getMovementDifficulty());
        if (event.getResource() != null) {
            MineableResource resource = MineableResource.fromTypeAndAmount(event.getResource().getResourceType(), event.getResource().getCurrentAmount());
            if (planet.getResources() != resource) planet.setResources(resource);
        }

        List<Planet> planets = new ArrayList<>();
        for (PlanetNeighboursDto neighboursDto : event.getNeighbours()) {
            Planet neighbour = getPlanetOrCreate(neighboursDto.getId());
            planet.addNeighbor(neighbour, neighboursDto.getDirection());
            neighbour.addNeighbor(planet, neighboursDto.getDirection().getOppositeDirection());
            planets.add(neighbour);
        }
        planets.add(planet);
        planetRepository.saveAll(planets);

        List<Robot> robots = robotRepository.findByPlanet(planet);
        log.info("{} Robots are on Planet {}", robots.size(), planet.getPlanetId());
        for (Robot robot : robots) {
            if (robot.hasCommand()) continue;
            robot.chooseNextCommand();
            if (robot.canNotMove())
                robot.queueFirst(Command.createRegeneration(robot.getRobotId(), robot.getPlayer().getGameId(), robot.getPlayer().getPlayerId()));
            robotRepository.save(robot);
        }

        log.info("Discovered Planet {} with {} and Movement Difficulty of {}", planet.getPlanetId(), planet.getResources(), planet.getMovementDifficulty());
    }

    @Async
    @EventListener(ResourceMinedEvent.class)
    public void onPlanetResourceMined(ResourceMinedEvent event) {
        Planet planet = getPlanetOrCreate(event.getPlanetId());
        planet.setResources(MineableResource.fromTypeAndAmount(event.getResource().getResourceType(), event.getResource().getCurrentAmount()));
        planetRepository.save(planet);
        log.info("Planet {} has {} left", planet.getPlanetId(), planet.getResources());
    }

    private Planet getPlanetOrCreate(UUID planetId) {
        return planetRepository.findByPlanetId(planetId).orElse(new Planet(planetId));
    }

}

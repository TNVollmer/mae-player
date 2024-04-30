package thkoeln.dungeon.player.planet.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.core.domainprimitives.status.Activity;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetResourceDto;
import thkoeln.dungeon.player.core.events.concreteevents.planet.ResourceMinedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotPlanetDto;
import thkoeln.dungeon.player.game.domain.GameException;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.planet.domain.PlanetRepository;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

import java.util.UUID;

@Service
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
        MineableResource resource = MineableResource.fromTypeAndAmount(event.getResource().getResourceType(), event.getResource().getCurrentAmount());
        Planet planet = getPlanetOrCreate(event.getPlanetId(), resource);
        if (planet.getResources() != resource) {
            planet.setResources(resource);
        }

        planetRepository.save(planet);
    }

    @EventListener(ResourceMinedEvent.class)
    public void onResourceMined(ResourceMinedEvent event) {
        Planet planet = getPlanet(event.getPlanetId());
        planet.getResources().subtractAmount(event.getMinedAmount());

        planetRepository.save(planet);

        if (!planet.hasResources()) {
            robotRepository.findByPlanet(planet).forEach(robot -> robot.setCurrentActivity(Activity.IDLE));
        }
    }

    private Planet getPlanet(UUID planetId) {
        return planetRepository.findByPlanetId(planetId).orElseThrow(() -> new GameException("Should not throw!"));
    }

    private Planet getPlanetOrCreate(UUID planetId, MineableResource resource) {
        return planetRepository.findByPlanetId(planetId).orElse(
                new Planet(planetId, resource));
    }

}

package thkoeln.dungeon.player.planet.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetNeighboursDto;
import thkoeln.dungeon.player.core.events.concreteevents.planet.ResourceMinedEvent;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.planet.domain.PlanetRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PlanetApplicationService {

    private final PlanetRepository planetRepository;

    @Autowired
    public PlanetApplicationService(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    @Async
    @EventListener(PlanetDiscoveredEvent.class)
    public void onPlanetDiscovered(PlanetDiscoveredEvent event) {
        Planet planet = getPlanetOrCreate(event.getPlanetId());
        planet.explore();
        planet.setMovementDifficulty(event.getMovementDifficulty());
        if (event.getResource() != null) {
            MineableResource resource = MineableResource.fromTypeAndAmount(event.getResource().getResourceType(), event.getResource().getCurrentAmount());
            if (planet.getResources() != resource) planet.setResources(resource);
        }

        List<Planet> neighbours = new ArrayList<>();
        for (PlanetNeighboursDto neighboursDto : event.getNeighbours()) {
            Planet neighbour = getPlanetOrCreate(neighboursDto.getId());
            planet.addNeighbor(neighbour, neighboursDto.getDirection());
            neighbour.addNeighbor(planet, neighboursDto.getDirection().getOppositeDirection());
            neighbours.add(neighbour);
        }

        planetRepository.saveAll(neighbours);
        planetRepository.save(planet);

        log.info("Discovered Planet {} with {}", planet.getPlanetId(), planet.getResources());
    }

    @Async
    @EventListener(ResourceMinedEvent.class)
    public void onResourceMined(ResourceMinedEvent event) {
        Planet planet = getPlanetOrCreate(event.getPlanetId());
        planet.minedResource(MineableResource.fromTypeAndAmount(planet.getResources().getType(), event.getMinedAmount()));

        planetRepository.save(planet);
    }

    private Planet getPlanetOrCreate(UUID planetId) {
        return planetRepository.findByPlanetId(planetId).orElse(new Planet(planetId));
    }

}

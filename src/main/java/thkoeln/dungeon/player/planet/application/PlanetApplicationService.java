package thkoeln.dungeon.player.planet.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.player.planet.domain.PlanetRepository;

@Service
public class PlanetApplicationService {

    private final PlanetRepository planetRepository;

    @Autowired
    public PlanetApplicationService(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    @EventListener(PlanetDiscoveredEvent.class)
    public void onPlanetDiscovered(PlanetDiscoveredEvent event) {

    }

}

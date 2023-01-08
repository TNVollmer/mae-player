package thkoeln.dungeon.monte.planet.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.eventlistener.AbstractEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.planet.PlanetDiscoveredEvent;

@Service
public class PlanetEventHandler {
    private Logger logger = LoggerFactory.getLogger(PlanetEventHandler.class);
    private PlanetApplicationService planetApplicationService;

    @Autowired
    public PlanetEventHandler( PlanetApplicationService planetApplicationService ) {
        this.planetApplicationService = planetApplicationService;
    }

    /**
     * Dispatch to the appropriate application service method
     * @param event
     */
    public void handlePlanetRelatedEvent( AbstractEvent event ) {
        switch ( event.getEventHeader().getEventType() ) {
            case PLANET_DISCOVERED:
                planetApplicationService.addPlanetNeighbours( (PlanetDiscoveredEvent)event );
                break;
            default:
        }
    }

}

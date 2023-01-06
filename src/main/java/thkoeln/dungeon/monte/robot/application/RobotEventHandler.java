package thkoeln.dungeon.monte.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.eventlistener.AbstractEvent;
import thkoeln.dungeon.monte.eventlistener.concreteevents.robot.RobotMovedIntegrationEvent;
import thkoeln.dungeon.monte.eventlistener.concreteevents.robot.RobotSpawnedEvent;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;
import thkoeln.dungeon.monte.planet.domain.Planet;

import static java.lang.Boolean.TRUE;

@Service
public class RobotEventHandler {
    private Logger logger = LoggerFactory.getLogger(RobotEventHandler.class);
    private RobotApplicationService robotApplicationService;
    private PlanetApplicationService planetApplicationService;

    @Autowired
    public RobotEventHandler( RobotApplicationService robotApplicationService,
                              PlanetApplicationService planetApplicationService ) {
        this.robotApplicationService = robotApplicationService;
        this.planetApplicationService = planetApplicationService;
    }

    /**
     * Dispatch to the appropriate application service method
     * @param event
     */
    public void handleRobotRelatedEvent( AbstractEvent event ) {
        switch ( event.getEventHeader().getEventType() ) {
            case ROBOT_SPAWNED:
                robotApplicationService.addNewRobotFromEvent( (RobotSpawnedEvent) event );
                break;
            case ROBOT_MOVED:
                robotApplicationService.moveRobotFromEvent( (RobotMovedIntegrationEvent) event );
                break;
            default:
        }
    }

}

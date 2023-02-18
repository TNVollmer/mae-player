package thkoeln.dungeon.monte.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.core.eventlistener.AbstractEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.move.RobotMovedIntegrationEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.RobotRegeneratedIntegrationEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.spawn.RobotPlanetDto;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.robot.domain.RobotException;

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
        if ( !event.isValid() ) throw new RobotException( "!event.isValid()" );
        switch ( event.getEventHeader().getEventType() ) {
            case ROBOT_SPAWNED:
                handleRobotSpawnedEvent( (RobotSpawnedEvent) event );
                break;
            case ROBOT_MOVED:
                robotApplicationService.moveRobotFromExternalEvent( (RobotMovedIntegrationEvent) event );
                break;
            case ROBOT_REGENERATED_INTEGRATION:
                robotApplicationService.regenerateRobotFromExternalEvent( (RobotRegeneratedIntegrationEvent) event );
                break;
            default:
        }
    }


    private void handleRobotSpawnedEvent( RobotSpawnedEvent event ) {
        logger.info( "Handling ROBOT_SPAWNED event ..." );
        RobotPlanetDto robotPlanetDto = event.getRobotDto().getPlanet();
        Planet planet = planetApplicationService.addOrUpdatePlanet(
                robotPlanetDto.getPlanetId(), Energy.from( robotPlanetDto.getMovementDifficulty() ), TRUE );
        robotApplicationService.addNewOwnRobot( event.getRobotDto().getId(), planet );
    }

}

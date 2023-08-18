package thkoeln.dungeon.monte.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.core.eventlistener.AbstractEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.mine.RobotResourceMinedEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.move.RobotMovedEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.RobotRegeneratedEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.spawn.RobotPlanetDto;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.monte.robot.domain.RobotException;

import java.util.UUID;

@Service
public class RobotEventHandler {
    private Logger logger = LoggerFactory.getLogger(RobotEventHandler.class);
    private RobotApplicationService robotApplicationService;

    @Autowired
    public RobotEventHandler( RobotApplicationService robotApplicationService ) {
        this.robotApplicationService = robotApplicationService;
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
            case ROBOT_REGENERATED:
                robotApplicationService.regenerateRobotFromExternalEvent( (RobotRegeneratedEvent) event );
                break;
            case ROBOT_RESOURCE_MINED:
                handleResourceMinedEvent( (RobotResourceMinedEvent) event );
                break;
            default:
        }
    }


    private void handleRobotSpawnedEvent( RobotSpawnedEvent event ) {
        logger.info( "Handling ROBOT_SPAWNED event ..." );
        robotApplicationService.addNewOwnRobot( event.getRobotDto().getId() );
    }


    private void handleResourceMinedEvent( RobotResourceMinedEvent event ) {
        logger.info( "Handling ROBOT_RESOURCE_MINED_INTEGRATION event ..." );
        MineableResource resource = event.minedResourceAsDomainPrimitive();
        robotApplicationService.robotHasMined( event.getRobotId(), resource, event.getResourceInventory().getResource() );
    }

}

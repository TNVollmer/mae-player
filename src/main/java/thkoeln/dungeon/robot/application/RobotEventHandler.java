package thkoeln.dungeon.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventlistener.AbstractEvent;
import thkoeln.dungeon.eventlistener.concreteevents.robot.RobotSpawnedEvent;

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
        switch ( event.getEventHeader().getEventType() ) {
            case ROBOT_SPAWNED:
                handleRobotSpawnedEvent( (RobotSpawnedEvent) event );
                break;
            default:
        }
    }

    public void handleRobotSpawnedEvent( RobotSpawnedEvent event ) {
        logger.info( "xxxxxxxxxxxxxx RobotSpawnedEvent ");
    }
}

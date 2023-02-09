package thkoeln.dungeon.monte.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.eventlistener.AbstractEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.move.RobotMovedIntegrationEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.RobotRegeneratedIntegrationEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal.RobotsRevealedIntegrationEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.spawn.RobotSpawnedEvent;

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
                robotApplicationService.addNewRobotFromEvent( (RobotSpawnedEvent) event );
                break;
            case ROBOT_MOVED:
                robotApplicationService.moveRobotFromEvent( (RobotMovedIntegrationEvent) event );
                break;
            case ROBOT_REGENERATED_INTEGRATION:
                robotApplicationService.regenerateRobotFromEvent( (RobotRegeneratedIntegrationEvent) event );
                break;
            default:
        }
    }

}

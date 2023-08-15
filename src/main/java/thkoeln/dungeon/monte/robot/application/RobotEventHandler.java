package thkoeln.dungeon.monte.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.core.eventlistener.AbstractEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.mine.RobotResourceMinedIntegrationEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.move.RobotMovedIntegrationEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.RobotRegeneratedIntegrationEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.spawn.RobotPlanetDto;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.robot.domain.RobotException;

import java.util.UUID;

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
                handleRobotMovedIntegrationEvent( (RobotMovedIntegrationEvent) event );
                break;
            case ROBOT_REGENERATED:
                robotApplicationService.regenerateRobotFromExternalEvent( (RobotRegeneratedIntegrationEvent) event );
                break;
            case ROBOT_RESOURCE_MINED:
                handleResourceMinedEvent( (RobotResourceMinedIntegrationEvent) event );
                break;
            default:
        }
    }


    private void handleRobotSpawnedEvent( RobotSpawnedEvent event ) {
        logger.info( "Handling ROBOT_SPAWNED event ..." );
        RobotPlanetDto robotPlanetDto = event.getRobotDto().getPlanet();
        Planet planet = planetApplicationService.addOrUpdatePlanet(
                robotPlanetDto.getPlanetId(), Energy.from( robotPlanetDto.getMovementDifficulty() ) );
        robotApplicationService.addNewOwnRobot( event.getRobotDto().getId(), planet );
    }


    private void handleRobotMovedIntegrationEvent( RobotMovedIntegrationEvent event ) {
        logger.info( "Handling ROBOT_MOVED_INTEGRATION event ..." );
        UUID planetId = event.getToPlanet().getId();
        Energy movementDifficulty = Energy.from( event.getToPlanet().getMovementDifficulty() );
        Planet planet = planetApplicationService.addOrUpdatePlanet( planetId, movementDifficulty );
        Energy updatedEnergy = Energy.from( event.getRemainingEnergy() );
        robotApplicationService.moveRobotToNewPlanet( event.getRobotId(), planet, updatedEnergy );
    }




    private void handleResourceMinedEvent( RobotResourceMinedIntegrationEvent event ) {
        logger.info( "Handling ROBOT_RESOURCE_MINED_INTEGRATION event ..." );
        MineableResource resource = event.minedResourceAsDomainPrimitive();
        robotApplicationService.robotHasMined( event.getRobotId(), resource, event.getResourceInventory().getResource() );
    }

}

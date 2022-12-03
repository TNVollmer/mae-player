package thkoeln.dungeon.eventconsumer.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventconsumer.map.SpaceStationCreatedEvent;
import thkoeln.dungeon.eventconsumer.map.SpaceStationEventCreatedRepository;
import thkoeln.dungeon.planet.application.PlanetApplicationService;

import java.util.UUID;

@Service
public class RobotEventConsumerService {
    private Logger logger = LoggerFactory.getLogger( RobotEventConsumerService.class );

    private MovementEventRepository movementEventRepository;
    private NeighboursEventRepository neighboursEventRepository;
    private PlanetApplicationService planetApplicationService;


    @Autowired
    public RobotEventConsumerService( MovementEventRepository movementEventRepository,
                                      NeighboursEventRepository neighboursEventRepository,
                                      PlanetApplicationService planetApplicationService ) {
        this.movementEventRepository = movementEventRepository;
        this.neighboursEventRepository = neighboursEventRepository;
        this.planetApplicationService = planetApplicationService;
    }

    /**
     * Event published by MapService, sending the list of newly created space stations
     */
/*
    @KafkaListener( topics = "movement" )
    public void consumeMovementEvent(
            @Header String eventId, @Header String timestamp, @Header String transactionId, @Payload String payload ) {
        MovementEvent movementEvent = new MovementEvent()
                .fillWithPayload( payload )
                .fillHeader( eventId, timestamp, transactionId );
        if ( movementEvent.isValid() ) {
            logger.info( "Successfully consumed MovementEvent " + movementEvent );
            movementEventRepository.save( movementEvent );
            // Processing is delayed until also the "neighbours" event (containing the planet neighbours) has
            // been caught - no good doing this now, will only lead to race conditions (e.g. neighbours event
            // consumed before movement event). This is called following the "roundStatus - ended" event.
        }
        else {
            logger.warn( "Caught invalid MovementEvent " + movementEvent );
        }
    }
*/

    /**
     * Event published by MapService, sending the list of newly created space stations
     */
/*
    @KafkaListener( topics = "neighbours" )
    public void consumeNeighboursEvent(
            @Header String eventId, @Header String timestamp, @Header String transactionId, @Payload String payload ) {
        NeighboursEvent neighboursEvent = new NeighboursEvent()
                .fillWithPayload( payload )
                .fillHeader( eventId, timestamp, transactionId );
        if ( neighboursEvent.isValid() ) {
            logger.info( "Successfully consumed NeighboursEvent " + neighboursEvent );
            neighboursEventRepository.save( neighboursEvent );
            // Processing is delayed - no good doing this now, will only lead to race conditions (e.g. neighbours event
            // consumed before movement event). This is called following the "roundStatus - ended" event.
        }
        else {
            logger.warn( "Caught invalid NeighboursEvent " + neighboursEvent );
        }
    }
*/

}

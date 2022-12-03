package thkoeln.dungeon.eventconsumer.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.planet.domain.PlanetDomainService;

import java.util.UUID;

@Service
public class MapEventConsumerService {
    private Logger logger = LoggerFactory.getLogger( MapEventConsumerService.class );

    private SpaceStationEventCreatedRepository spaceStationEventCreatedRepository;
    private GameWorldCreatedEventRepository gameWorldEventCreatedRepository;
    private PlanetDomainService planetDomainService;


    @Autowired
    public MapEventConsumerService( GameWorldCreatedEventRepository gameWorldEventCreatedRepository,
                                    SpaceStationEventCreatedRepository spaceStationEventCreatedRepository,
                                    PlanetDomainService planetDomainService ) {
        this.gameWorldEventCreatedRepository = gameWorldEventCreatedRepository;
        this.spaceStationEventCreatedRepository = spaceStationEventCreatedRepository;
        this.planetDomainService = planetDomainService;
    }

    /**
     * Event published by MapService, sending the list of newly created space stations
     */
/*
    @KafkaListener( topics = "gameworld-created" )
    public void consumeGameWorldCreatedEvent(
            @Header String eventId, @Header String timestamp, @Header String transactionId, @Payload String payload ) {
        GameWorldCreatedEvent gameWorldCreatedEvent = new GameWorldCreatedEvent()
                .fillWithPayload( payload )
                .fillHeader( eventId, timestamp, transactionId );
        if ( gameWorldCreatedEvent.isValid() ) {
            gameWorldEventCreatedRepository.save( gameWorldCreatedEvent );
            logger.info( "Successfully consumed gameworld-created event " + gameWorldCreatedEvent );
            for ( UUID spaceStationId : gameWorldCreatedEvent.getSpaceStationIds() ) {
                planetDomainService.addPlanetWithoutNeighbours( spaceStationId, true );
            }
        }
        else {
            logger.warn( "Caught invalid GameWorldCreatedEvent " + gameWorldCreatedEvent );
        }
    }

*/



    /**
     * Event published by MapService, informing about a new space station (e.g. transformed from a regular planet?)
     */
/*
    @KafkaListener( topics = "spacestation-created" )
    public void consumeSpaceStationCreatedEvent(
            @Header String eventId, @Header String timestamp, @Header String transactionId, @Payload String payload ) {
        SpaceStationCreatedEvent spaceStationCreatedEvent = new SpaceStationCreatedEvent()
                .fillWithPayload( payload )
                .fillHeader( eventId, timestamp, transactionId );
        if ( spaceStationCreatedEvent.isValid() ) {
            logger.info( "Successfully consumed gameworld-created event " + spaceStationCreatedEvent );
            spaceStationEventCreatedRepository.save( spaceStationCreatedEvent );
            planetDomainService.addPlanetWithoutNeighbours( spaceStationCreatedEvent.getPlanetId(), true );
        }
        else {
            logger.warn( "Caught invalid SpaceStationCreatedEvent " + spaceStationCreatedEvent );
        }
    }
*/
}

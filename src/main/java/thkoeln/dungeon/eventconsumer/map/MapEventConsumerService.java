package thkoeln.dungeon.eventconsumer.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventconsumer.game.GameStatusEvent;
import thkoeln.dungeon.eventconsumer.game.GameStatusEventRepository;
import thkoeln.dungeon.eventconsumer.game.PlayerStatusEvent;
import thkoeln.dungeon.eventconsumer.game.PlayerStatusEventRepository;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.planet.application.PlanetApplicationService;
import thkoeln.dungeon.player.application.PlayerApplicationService;

import java.util.UUID;

@Service
public class MapEventConsumerService {
    private Logger logger = LoggerFactory.getLogger( MapEventConsumerService.class );

    private SpaceStationEventCreatedRepository spaceStationEventCreatedRepository;
    private GameWorldEventCreatedRepository gameWorldEventCreatedRepository;
    private PlanetApplicationService planetApplicationService;


    @Autowired
    public MapEventConsumerService( GameWorldEventCreatedRepository gameWorldEventCreatedRepository,
                                    SpaceStationEventCreatedRepository spaceStationEventCreatedRepository,
                                    PlanetApplicationService planetApplicationService ) {
        this.gameWorldEventCreatedRepository = gameWorldEventCreatedRepository;
        this.spaceStationEventCreatedRepository = spaceStationEventCreatedRepository;
        this.planetApplicationService = planetApplicationService;
    }

    /**
     * Event published by MapService, sending the list of newly created space stations
     */
    @KafkaListener( topics = "gameworld-created" )
    public void consumeGameWorldCreatedEvent(
            @Header String eventId, @Header String timestamp, @Header String transactionId, @Payload String payload ) {
        GameWorldCreatedEvent gameWorldCreatedEvent = new GameWorldCreatedEvent()
                .fillWithPayload( payload )
                .fillHeader( eventId, timestamp, transactionId );
        gameWorldEventCreatedRepository.save( gameWorldCreatedEvent );
        if ( gameWorldCreatedEvent.isValid() ) {
            logger.info( "Successfully consumed gameworld-created event " + gameWorldCreatedEvent );
            for ( UUID spaceStationId : gameWorldCreatedEvent.getSpaceStationIds() ) {
                planetApplicationService.addPlanetWithoutNeighbours( spaceStationId, true );
            }
        }
        else {
            logger.warn( "Caught invalid GameWorldCreatedEvent " + gameWorldCreatedEvent );
        }
    }


    /**
     * Event published by MapService, informing about a new space station (e.g. transformed from a regular planet?)
     */
    @KafkaListener( topics = "spacestation-created" )
    public void consumeSpaceStationCreatedEvent(
            @Header String eventId, @Header String timestamp, @Header String transactionId, @Payload String payload ) {
        SpaceStationCreatedEvent spaceStationCreatedEvent = new SpaceStationCreatedEvent()
                .fillWithPayload( payload )
                .fillHeader( eventId, timestamp, transactionId );
        spaceStationEventCreatedRepository.save( spaceStationCreatedEvent );
        if ( spaceStationCreatedEvent.isValid() ) {
            logger.info( "Successfully consumed gameworld-created event " + spaceStationCreatedEvent );
            planetApplicationService.addPlanetWithoutNeighbours( spaceStationCreatedEvent.getPlanetId(), true );
        }
        else {
            logger.warn( "Caught invalid GameWorldCreatedEvent " + spaceStationCreatedEvent );
        }
    }

}

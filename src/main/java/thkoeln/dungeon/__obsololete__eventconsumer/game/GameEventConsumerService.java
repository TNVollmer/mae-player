package thkoeln.dungeon.__obsololete__eventconsumer.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.__obsololete__eventconsumer.robot.*;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.planet.domain.PlanetDomainService;
import thkoeln.dungeon.player.application.PlayerApplicationService;

@Service
public class GameEventConsumerService {
    private Logger logger = LoggerFactory.getLogger( GameEventConsumerService.class );
    private GameApplicationService gameApplicationService;
    private PlayerApplicationService playerApplicationService;
    private __OBSOLETE_GameStatusEventRepository gameStatusEventRepository;
    private PlayerStatusEventRepository playerStatusEventRepository;
    private MovementEventRepository movementEventRepository;
    private NeighboursEventRepository neighboursEventRepository;
    private PlanetDomainService planetDomainService;


    @Autowired
    public GameEventConsumerService( GameApplicationService gameApplicationService,
                                     __OBSOLETE_GameStatusEventRepository gameStatusEventRepository,
                                     PlayerStatusEventRepository playerStatusEventRepository,
                                     PlayerApplicationService playerApplicationService,
                                     MovementEventRepository movementEventRepository,
                                     NeighboursEventRepository neighboursEventRepository,
                                     PlanetDomainService planetDomainService ) {
        this.gameApplicationService = gameApplicationService;
        this.gameStatusEventRepository = gameStatusEventRepository;
        this.playerStatusEventRepository = playerStatusEventRepository;
        this.playerApplicationService = playerApplicationService;
        this.movementEventRepository = movementEventRepository;
        this.neighboursEventRepository = neighboursEventRepository;
        this.planetDomainService = planetDomainService;
    }

    /**
     * "Status changed" event published by GameService, esp. after a game has been created, started, or finished
     */
/*
    @KafkaListener( topics = "status" )
    public void consumeGameStatusEvent( @Header String eventId, @Header String timestamp, @Header String transactionId,
                                        @Payload String payload ) {
        logger.info( "Consume game status event with payload " + payload );
        __OBSOLETE_GameStatusEvent gameStatusEvent = new __OBSOLETE_GameStatusEvent()
                .fillWithPayload( payload )
                .fillHeader( eventId, timestamp, transactionId );
        if ( gameStatusEvent.isValid() ) {
            gameStatusEventRepository.save( gameStatusEvent );
            switch ( gameStatusEvent.getStatus() ) {
                case CREATED:
                    playerApplicationService.letPlayerJoinOpenGame();
                    break;
                case RUNNING:
                    gameApplicationService.gameExternallyStarted( gameStatusEvent.getGameId() );
                    break;
                case FINISHED:
                    gameApplicationService.gameExternallyFinished( gameStatusEvent.getGameId() );
                    break;
            }
        }
        else {
            logger.warn( "Caught invalid __OBSOLETE_GameStatusEvent " + gameStatusEvent );
        }
    }
*/

    /**
     * Event published by GameService after registering a player. Needed to get the playerId ... <sigh>
     */
/*
    @KafkaListener( topics = "playerStatus" )
    public void consumePlayerStatusEvent( @Header String eventId, @Header String timestamp, @Header String transactionId,
                                          @Payload String payload ) {
        logger.info( "Consume playerStatus event with payload " + payload );
        PlayerStatusEvent playerStatusEvent = new PlayerStatusEvent()
                .fillWithPayload( payload )
                .fillHeader( eventId, timestamp, transactionId );
        if ( playerStatusEvent.isValid() ) {
            playerStatusEventRepository.save( playerStatusEvent );
        }
        else {
            logger.warn( "Caught invalid PlayerStatusEvent " + playerStatusEvent );
        }
    }

    @KafkaListener( topics = "roundStatus" )
    public void consumeRoundStatusEvent( @Header String eventId, @Header String timestamp, @Header String transactionId,
                                         @Payload String payload ) {
        logger.info( "Consume playerStatus event with payload " + payload );
        RoundStatusEvent roundStatusEvent = new RoundStatusEvent()
                .fillWithPayload( payload )
                .fillHeader( eventId, timestamp, transactionId );
        if ( roundStatusEvent.isValid() ) {
            // this is pretty much a temporary implementation ... not much business logic in terms of
            // connecting the planets into a map. Will be refactored later.
            List<MovementEvent> unprocessedMovementEvents = movementEventRepository.findByProcessed( Boolean.FALSE );
            for ( MovementEvent unprocessedMovementEvent: unprocessedMovementEvents ) {
                unprocessedMovementEvent.setProcessed( true );
                movementEventRepository.save( unprocessedMovementEvent );
                if ( !unprocessedMovementEvent.getSuccess() ) {
                    logger.warn( "Movement event " + unprocessedMovementEvent + " was not successful!" );
                    break;
                }
                // todo: resources also need to be stored
                UUID planetId = unprocessedMovementEvent.getMovedToPlanetDto().getPlanetId();
                planetDomainService.visitPlanet( planetId,
                        unprocessedMovementEvent.getMovedToPlanetDto().getMovementDifficulty() );
                List<NeighboursEvent> neighboursEvents =
                        neighboursEventRepository.findByTransactionId( unprocessedMovementEvent.getTransactionId() );
                if( neighboursEvents.size() == 0 ) {
                    logger.warn( "No NeighboursEvents for MovementEvent " + unprocessedMovementEvent );
                    return;
                }
                else if ( neighboursEvents.size() > 1 ) {
                    logger.warn( ">1 NeighboursEvents for MovementEvent " + unprocessedMovementEvent );
                }
                NeighboursEvent neighboursEvent = neighboursEvents.get( 0 );
                for ( NeighbourPlanetDto neighbourPlanetDto: neighboursEvent.getNeighbourPlanetDtos() ) {
                    // todo: planet type also need to be stored
                    planetDomainService.addNeighbourToPlanet( planetId, neighbourPlanetDto.getPlanetId(),
                            neighbourPlanetDto.getDirection(), neighbourPlanetDto.getMovementDifficulty() );
                }
            }
            // - find fitting neighbours events
            // - create the neighbour planets
            // - connect them to the visited planet
        }
        else {
            logger.warn( "Caught invalid RoundStartedEvent " + roundStatusEvent );
        }
    }

 */
}

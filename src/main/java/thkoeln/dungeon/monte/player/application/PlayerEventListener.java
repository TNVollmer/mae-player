package thkoeln.dungeon.monte.player.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.eventlistener.AbstractEvent;
import thkoeln.dungeon.monte.core.eventlistener.EventFactory;
import thkoeln.dungeon.monte.core.eventlistener.EventHeader;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.game.GameStatusEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal.RobotsRevealedEvent;
import thkoeln.dungeon.monte.game.application.GameApplicationService;
import thkoeln.dungeon.monte.game.domain.GameStatus;
import thkoeln.dungeon.monte.player.domain.Player;
import thkoeln.dungeon.monte.robot.application.RobotApplicationService;
import thkoeln.dungeon.monte.robot.application.RobotEventHandler;

import java.util.Set;

@Service
public class PlayerEventListener {
    private Logger logger = LoggerFactory.getLogger(PlayerEventListener.class);
    private EventFactory eventFactory;
    private GameApplicationService gameApplicationService;
    private PlayerApplicationService playerApplicationService;
    private RobotEventHandler robotEventHandler;
    private RobotApplicationService robotApplicationService;
    @Autowired
    public PlayerEventListener( EventFactory eventFactory,
                                GameApplicationService gameApplicationService,
                                PlayerApplicationService playerApplicationService,
                                RobotEventHandler robotEventHandler,
                                RobotApplicationService robotApplicationService
    ) {
        this.eventFactory = eventFactory;
        this.gameApplicationService = gameApplicationService;
        this.playerApplicationService = playerApplicationService;
        this.robotEventHandler = robotEventHandler;
        this.robotApplicationService = robotApplicationService;
    }


    /**
     * Listener to all events that the core services send to the player
     * @param eventIdStr
     * @param transactionIdStr
     * @param playerIdStr
     * @param type
     * @param version
     * @param timestampStr
     * @param payload
     */
    @RabbitListener( id = "player-queue" )
    public void receiveEvent( @Header( required = false, value = EventHeader.EVENT_ID_KEY ) String eventIdStr,
                              @Header( required = false, value = EventHeader.TRANSACTION_ID_KEY ) String transactionIdStr,
                              @Header( required = false, value = EventHeader.PLAYER_ID_KEY ) String playerIdStr,
                              @Header( required = false, value = EventHeader.TYPE_KEY ) String type,
                              @Header( required = false, value = EventHeader.VERSION_KEY ) String version,
                              @Header( required = false, value = EventHeader.TIMESTAMP_KEY ) String timestampStr,
                              String payload ) {
        try {
            EventHeader eventHeader =
                    new EventHeader( type, eventIdStr, playerIdStr, transactionIdStr, timestampStr, version );
            AbstractEvent newEvent = eventFactory.fromHeaderAndPayload( eventHeader, payload );
            logger.info( "======== EVENT =====> " + newEvent.toStringShort() );
            logger.debug( "======== EVENT (detailed) =====>\n" + newEvent );
            if ( !newEvent.isValid() ) {
                logger.warn( "Event invalid: " + newEvent );
                return;
            }
            if ( eventHeader.getEventType().isRobotRelated() ) robotEventHandler.handleRobotRelatedEvent( newEvent );
            else handlePlayerRelatedEvent( newEvent );
        }
        catch ( Exception e ) {
            logger.error ( "!!!!!!!!!!!!!! EVENT ERROR !!!!!!!!!!!!!\n" + e );
        }
    }


    /**
     * Dispatch to the appropriate application service method
     * @param event
     */
    private void handlePlayerRelatedEvent( AbstractEvent event ) {
        switch ( event.getEventHeader().getEventType() ) {
            case GAME_STATUS:
                handleGameStatusEvent( (GameStatusEvent) event );
                break;
            case ROUND_STATUS:
                handleRoundStatusEvent( (RoundStatusEvent) event );
                break;
            case TRADABLE_PRICES:
                logger.info( "TradeablePricesEvent - no handling at the moment, assume prices to be fix." );
                break;
            case ROBOT_REVEALED:
                handleRobotsRevealedIntegrationEvent( (RobotsRevealedEvent) event );
                break;
            default:
        }
    }


    private void handleGameStatusEvent( GameStatusEvent gameStatusEvent ) {
        if ( GameStatus.CREATED.equals( gameStatusEvent.getStatus() ) ) {
            gameApplicationService.fetchRemoteGame();
            playerApplicationService.registerPlayer();
            playerApplicationService.letPlayerJoinOpenGame();
        }
        else if ( GameStatus.RUNNING.equals( gameStatusEvent.getStatus() ) ) {
            gameApplicationService.startGame( gameStatusEvent.getGameId() );
        }
        else if ( GameStatus.FINISHED.equals( gameStatusEvent.getStatus() ) ) {
            playerApplicationService.cleanupAfterFinishingGame();
        }
    }


    private void handleRoundStatusEvent( RoundStatusEvent event ) {
        if ( event.getRoundStatus() == RoundStatusType.STARTED ) {
            gameApplicationService.roundStarted( event.getRoundNumber() );
        }
    }


    private void handleRobotsRevealedIntegrationEvent( RobotsRevealedEvent event ) {
        logger.info( "Handling RobotsRevealedIntegrationEvent - player aspects ..." );
        Set<String> playerShortNames = event.playerShortNames();
        for ( String playerShortName : playerShortNames ) {
            Player enemyPlayer = playerApplicationService.addEnemyPlayer( playerShortName );
            Character enemyLetter = ( enemyPlayer == null ) ? null : enemyPlayer.getEnemyChar();
            event.updateEnemyChar( playerShortName, enemyLetter );
        }
        // the rest is now robot related
        robotApplicationService.updateRobotsFromExternalEvent( event );
    }

}

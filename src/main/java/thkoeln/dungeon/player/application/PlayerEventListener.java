package thkoeln.dungeon.player.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.domainprimitives.Money;
import thkoeln.dungeon.eventlistener.AbstractEvent;
import thkoeln.dungeon.eventlistener.EventFactory;
import thkoeln.dungeon.eventlistener.EventHeader;
import thkoeln.dungeon.eventlistener.concreteevents.BankInitializedEvent;
import thkoeln.dungeon.eventlistener.concreteevents.GameStatusEvent;
import thkoeln.dungeon.eventlistener.concreteevents.RoundStatusEvent;
import thkoeln.dungeon.eventlistener.concreteevents.TradeablePricesEvent;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.robot.application.RobotEventHandler;

import static thkoeln.dungeon.eventlistener.EventHeader.*;
import static thkoeln.dungeon.game.domain.GameStatus.*;

@Service
public class PlayerEventListener {
    private Logger logger = LoggerFactory.getLogger(PlayerEventListener.class);
    private Environment environment;
    private EventFactory eventFactory;
    private GameApplicationService gameApplicationService;
    private PlayerApplicationService playerApplicationService;
    private RobotEventHandler robotEventHandler;

    @Autowired
    public PlayerEventListener( Environment environment,
                                EventFactory eventFactory,
                                GameApplicationService gameApplicationService,
                                PlayerApplicationService playerApplicationService,
                                RobotEventHandler robotEventHandler ) {
        this.eventFactory = eventFactory;
        this.gameApplicationService = gameApplicationService;
        this.playerApplicationService = playerApplicationService;
        this.environment = environment;
        this.robotEventHandler = robotEventHandler;
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
    public void receiveEvent( @Header( EVENT_ID_KEY ) String eventIdStr,
                              @Header( required = false, value = TRANSACTION_ID_KEY ) String transactionIdStr,
                              @Header( required = false, value = PLAYER_ID_KEY ) String playerIdStr,
                              @Header( TYPE_KEY ) String type,
                              @Header( VERSION_KEY ) String version,
                              @Header( TIMESTAMP_KEY ) String timestampStr,
                              String payload ) {
        EventHeader eventHeader =
                new EventHeader( type, eventIdStr, playerIdStr, transactionIdStr, timestampStr, version );
        AbstractEvent newEvent = eventFactory.fromHeaderAndPayload( eventHeader, payload );
        logger.info( environment.getProperty( "ANSI_BLUE" ) + "======== EVENT =====>\n" +
                newEvent + environment.getProperty( "ANSI_RESET" ) );
        if ( !newEvent.isValid() ) {
            logger.error( "Event invalid: " + newEvent );
            return;
        }
        if ( eventHeader.getEventType().isRobotRelated() ) {
            robotEventHandler.handleRobotRelatedEvent( newEvent );
        }
        else {
            handlePlayerRelatedEvent(newEvent);
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
            case BANK_INITIALIZED:
                handleBankInitializedEvent( (BankInitializedEvent) event );
                break;
            case ROUND_STATUS:
                handleRoundStatusEvent( (RoundStatusEvent) event );
                break;
            case TRADABLE_PRICES:
                handleTradablePricesEvent( (TradeablePricesEvent) event );
                break;
            case ROBOT_SPAWNED:
                handleTradablePricesEvent( (TradeablePricesEvent) event );
                break;
            default:
        }
    }


    private void handleGameStatusEvent( GameStatusEvent gameStatusEvent ) {
        if ( CREATED.equals( gameStatusEvent.getStatus() ) ) {
            gameApplicationService.fetchRemoteGame();
            playerApplicationService.registerPlayer();
            playerApplicationService.letPlayerJoinOpenGame();
        }
        else if ( RUNNING.equals( gameStatusEvent.getStatus() ) ) {
            gameApplicationService.startGame( gameStatusEvent.getGameId() );
        }
        else if ( FINISHED.equals( gameStatusEvent.getStatus() ) ) {
            gameApplicationService.finishGame( gameStatusEvent.getGameId() );
        }
    }


    private void handleBankInitializedEvent( BankInitializedEvent bankInitializedEvent ) {
        playerApplicationService.adjustBankAccount(
                bankInitializedEvent.getPlayerId(), bankInitializedEvent.getBalance() );
    }


    private void handleRoundStatusEvent( RoundStatusEvent event ) {
        // todo this logic should be moved elsewhere - the handler just just delegate
        logger.info( "Round started: Buy robots!" );
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
        Money priceForRobot = Money.fromInteger( 100 );
        int numOfNewRobots = player.getMoney().canBuyThatManyFor( priceForRobot );
        playerApplicationService.buyRobots( numOfNewRobots );

        logger.info( environment.getProperty( "ANSI_RED" ) +
                "------> more business logic to be added!" + environment.getProperty( "ANSI_RESET" ) );
    }


    private void handleTradablePricesEvent( TradeablePricesEvent event ) {
        logger.info( "TradeablePricesEvent - no handling at the moment, assume prices to be fix." );
    }

}

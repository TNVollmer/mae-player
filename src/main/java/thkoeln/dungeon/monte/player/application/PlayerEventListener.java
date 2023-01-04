package thkoeln.dungeon.monte.player.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.monte.eventlistener.AbstractEvent;
import thkoeln.dungeon.monte.eventlistener.EventFactory;
import thkoeln.dungeon.monte.eventlistener.EventHeader;
import thkoeln.dungeon.monte.eventlistener.concreteevents.game.GameStatusEvent;
import thkoeln.dungeon.monte.eventlistener.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.monte.eventlistener.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.monte.eventlistener.concreteevents.trading.BankInitializedEvent;
import thkoeln.dungeon.monte.eventlistener.concreteevents.trading.TradeablePricesEvent;
import thkoeln.dungeon.monte.game.application.GameApplicationService;
import thkoeln.dungeon.monte.game.domain.GameStatus;
import thkoeln.dungeon.monte.planet.application.PlanetEventHandler;
import thkoeln.dungeon.monte.player.domain.Player;
import thkoeln.dungeon.monte.robot.application.RobotEventHandler;
import thkoeln.dungeon.monte.core.util.AbstractPrinter;

@Service
public class PlayerEventListener {
    private Logger logger = LoggerFactory.getLogger(PlayerEventListener.class);
    private EventFactory eventFactory;
    private GameApplicationService gameApplicationService;
    private PlayerApplicationService playerApplicationService;
    private PlanetEventHandler planetEventHandler;
    private RobotEventHandler robotEventHandler;
    private PlayerPrinter playerPrinter;

    @Autowired
    public PlayerEventListener( EventFactory eventFactory,
                                GameApplicationService gameApplicationService,
                                PlayerApplicationService playerApplicationService,
                                PlanetEventHandler planetEventHandler,
                                RobotEventHandler robotEventHandler,
                                PlayerPrinter playerPrinter ) {
        this.eventFactory = eventFactory;
        this.gameApplicationService = gameApplicationService;
        this.playerApplicationService = playerApplicationService;
        this.robotEventHandler = robotEventHandler;
        this.planetEventHandler = planetEventHandler;
        this.playerPrinter = playerPrinter;
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
    public void receiveEvent( @Header( EventHeader.EVENT_ID_KEY ) String eventIdStr,
                              @Header( required = false, value = EventHeader.TRANSACTION_ID_KEY ) String transactionIdStr,
                              @Header( required = false, value = EventHeader.PLAYER_ID_KEY ) String playerIdStr,
                              @Header( EventHeader.TYPE_KEY ) String type,
                              @Header( EventHeader.VERSION_KEY ) String version,
                              @Header( EventHeader.TIMESTAMP_KEY ) String timestampStr,
                              String payload ) {
        EventHeader eventHeader =
                new EventHeader( type, eventIdStr, playerIdStr, transactionIdStr, timestampStr, version );
        AbstractEvent newEvent = eventFactory.fromHeaderAndPayload( eventHeader, payload );
        logger.info( AbstractPrinter.BLUE + "======== EVENT =====>\n" + newEvent + AbstractPrinter.RESET );
        if ( !newEvent.isValid() ) {
            logger.error( "Event invalid: " + newEvent );
            return;
        }
        if ( eventHeader.getEventType().isRobotRelated() ) robotEventHandler.handleRobotRelatedEvent( newEvent );
        else if ( eventHeader.getEventType().isPlanetRelated() ) planetEventHandler.handlePlanetRelatedEvent( newEvent);
        else handlePlayerRelatedEvent( newEvent );
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
        if ( GameStatus.CREATED.equals( gameStatusEvent.getStatus() ) ) {
            gameApplicationService.fetchRemoteGame();
            playerApplicationService.registerPlayer();
            playerApplicationService.letPlayerJoinOpenGame();
        }
        else if ( GameStatus.RUNNING.equals( gameStatusEvent.getStatus() ) ) {
            gameApplicationService.startGame( gameStatusEvent.getGameId() );
        }
        else if ( GameStatus.FINISHED.equals( gameStatusEvent.getStatus() ) ) {
            gameApplicationService.finishGame( gameStatusEvent.getGameId() );
        }
    }


    private void handleBankInitializedEvent( BankInitializedEvent bankInitializedEvent ) {
        playerApplicationService.adjustBankAccount(
                bankInitializedEvent.getPlayerId(), bankInitializedEvent.getBalance() );
    }


    private void handleRoundStatusEvent( RoundStatusEvent event ) {
        if ( event.getRoundStatus() == RoundStatusType.STARTED ) {
            gameApplicationService.roundStarted( event.getRoundNumber() );
            playerPrinter.printStatus();
        }
    }


    private void handleTradablePricesEvent( TradeablePricesEvent event ) {
        logger.info( "TradeablePricesEvent - no handling at the moment, assume prices to be fix." );
    }

}

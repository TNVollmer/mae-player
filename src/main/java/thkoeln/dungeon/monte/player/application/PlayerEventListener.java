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
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal.RobotsRevealedIntegrationEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.trading.BankAccountTransactionBookedEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.trading.BankInitializedEvent;
import thkoeln.dungeon.monte.game.application.GameApplicationService;
import thkoeln.dungeon.monte.game.domain.GameStatus;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;
import thkoeln.dungeon.monte.planet.application.PlanetEventHandler;
import thkoeln.dungeon.monte.player.domain.Player;
import thkoeln.dungeon.monte.printer.devices.console.ConsoleOutput;
import thkoeln.dungeon.monte.printer.printers.PlayerPrinter;
import thkoeln.dungeon.monte.robot.application.RobotApplicationService;
import thkoeln.dungeon.monte.robot.application.RobotEventHandler;

import java.util.Set;

@Service
public class PlayerEventListener {
    private Logger logger = LoggerFactory.getLogger(PlayerEventListener.class);
    private EventFactory eventFactory;
    private GameApplicationService gameApplicationService;
    private PlayerApplicationService playerApplicationService;
    private PlanetEventHandler planetEventHandler;
    private PlanetApplicationService planetApplicationService;
    private RobotEventHandler robotEventHandler;
    private RobotApplicationService robotApplicationService;
    private PlayerPrinter playerPrinter;

    @Autowired
    public PlayerEventListener( EventFactory eventFactory,
                                GameApplicationService gameApplicationService,
                                PlayerApplicationService playerApplicationService,
                                PlanetEventHandler planetEventHandler,
                                PlanetApplicationService planetApplicationService,
                                RobotEventHandler robotEventHandler,
                                RobotApplicationService robotApplicationService,
                                PlayerPrinter playerPrinter ) {
        this.eventFactory = eventFactory;
        this.gameApplicationService = gameApplicationService;
        this.playerApplicationService = playerApplicationService;
        this.robotEventHandler = robotEventHandler;
        this.planetEventHandler = planetEventHandler;
        this.planetApplicationService = planetApplicationService;
        this.playerPrinter = playerPrinter;
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
            logger.info( ConsoleOutput.BLUE + "======== EVENT =====> " + newEvent.toStringShort() + ConsoleOutput.RESET );
            logger.debug( ConsoleOutput.BLUE + "======== EVENT (detailed) =====>\n" + newEvent + ConsoleOutput.RESET );
            if ( !newEvent.isValid() ) {
                logger.warn( "Event invalid: " + newEvent );
                return;
            }
            if ( eventHeader.getEventType().isRobotRelated() ) robotEventHandler.handleRobotRelatedEvent( newEvent );
            else if ( eventHeader.getEventType().isPlanetRelated() ) planetEventHandler.handlePlanetRelatedEvent( newEvent);
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
            case BANK_INITIALIZED:
                BankInitializedEvent bankInitializedEvent = (BankInitializedEvent) event;
                playerApplicationService.adjustBankAccount(
                        bankInitializedEvent.getPlayerId(), bankInitializedEvent.getBalance() );
                break;
            case BANK_ACCOUNT_TRANSACTION_BOOKED:
                BankAccountTransactionBookedEvent transactionBookedEvent =
                        (BankAccountTransactionBookedEvent) event;
                playerApplicationService.adjustBankAccount(
                        transactionBookedEvent.getPlayerId(), transactionBookedEvent.getBalance() );
                break;
            case ROUND_STATUS:
                handleRoundStatusEvent( (RoundStatusEvent) event );
                break;
            case TRADABLE_PRICES:
                logger.info( "TradeablePricesEvent - no handling at the moment, assume prices to be fix." );
                break;
            case ROBOT_REVEALED_INTEGRATION:
                handleRobotsRevealedIntegrationEvent( (RobotsRevealedIntegrationEvent) event );
                break;
            default:
        }
    }


    private void handleGameStatusEvent( GameStatusEvent gameStatusEvent ) {
        if ( GameStatus.CREATED.equals( gameStatusEvent.getStatus() ) ) {
            gameApplicationService.fetchRemoteGame();
            playerApplicationService.registerPlayer();
            playerApplicationService.letPlayerJoinOpenGame();
            playerPrinter.printStatus();
        }
        else if ( GameStatus.RUNNING.equals( gameStatusEvent.getStatus() ) ) {
            gameApplicationService.startGame( gameStatusEvent.getGameId() );
            playerPrinter.printStatus();
        }
        else if ( GameStatus.FINISHED.equals( gameStatusEvent.getStatus() ) ) {
            playerApplicationService.cleanupAfterFinishingGame();
            playerPrinter.printStatus();
        }
    }


    private void handleRoundStatusEvent( RoundStatusEvent event ) {
        if ( event.getRoundStatus() == RoundStatusType.STARTED ) {
            gameApplicationService.roundStarted( event.getRoundNumber() );
            playerApplicationService.submitRoundCommands();
        }
        if ( event.getRoundStatus() == RoundStatusType.ENDED ) {
            playerPrinter.printStatus();
        }
    }


    private void handleRobotsRevealedIntegrationEvent( RobotsRevealedIntegrationEvent event ) {
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

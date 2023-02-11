package thkoeln.dungeon.monte.player.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.monte.game.application.GameApplicationService;
import thkoeln.dungeon.monte.game.domain.Game;
import thkoeln.dungeon.monte.player.domain.Player;
import thkoeln.dungeon.monte.player.domain.PlayerException;
import thkoeln.dungeon.monte.player.domain.PlayerRepository;
import thkoeln.dungeon.monte.player.domain.PlayerStrategy;
import thkoeln.dungeon.monte.core.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.monte.robot.application.RobotApplicationService;
import thkoeln.dungeon.monte.trading.application.TradingAccountApplicationService;
import thkoeln.dungeon.monte.trading.domain.TradingAccount;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This game class encapsulates the game tactics for a simple autonomous controlling of a robot
 * swarm. It has the following structure:
 * - the "round started" event triggers the main round() method
 * - if there is enough money, new robots are bought (or, depending on configuration, existing robots are upgraded)
 * - for each robot, the proper command is chosen and issued (based on the configured tactics)
 * - each time an answer is received (with transaction id), the robots and the map are updated.
 */
@Service
public class PlayerApplicationService {
    private Logger logger = LoggerFactory.getLogger(PlayerApplicationService.class);
    private PlayerRepository playerRepository;
    private GameApplicationService gameApplicationService;
    private GameServiceRESTAdapter gameServiceRESTAdapter;
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;
    private TradingAccountApplicationService tradingAccountApplicationService;
    private RobotApplicationService robotApplicationService;
    private PlayerStrategy playerStrategy;


    @Value("${dungeon.playerName}")
    private String playerName;

    @Value("${dungeon.playerEmail}")
    private String playerEmail;

    @Autowired
    public PlayerApplicationService(
            PlayerRepository playerRepository,
            GameApplicationService gameApplicationService,
            GameServiceRESTAdapter gameServiceRESTAdapter,
            RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry,
            TradingAccountApplicationService tradingAccountApplicationService,
            RobotApplicationService robotApplicationService,
            PlayerStrategy playerStrategy ) {
        this.playerRepository = playerRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.gameApplicationService = gameApplicationService;
        this.rabbitListenerEndpointRegistry = rabbitListenerEndpointRegistry;
        this.tradingAccountApplicationService = tradingAccountApplicationService;
        this.robotApplicationService = robotApplicationService;
        this.playerStrategy = playerStrategy;
    }


    /**
     * Fetch the existing player. If there isn't one yet, it is created and stored to the database.
     * @return The current player.
     */
    public Player queryAndIfNeededCreatePlayer() {
        Player player = null;
        List<Player> players = playerRepository.findAll();
        if ( players.size() >= 1 ) {
            player = players.get( 0 );
        }
        else {
            player = new Player();
            player.setName( playerName );
            player.setEmail( playerEmail );
            playerRepository.save( player );
            logger.info( "Created new player (not yet registered): " + player );
        }
        player.setStrategy( playerStrategy );
        return player;
    }


    /**
     * Register the current player (or do nothing, if it is already registered)
     */
    public void registerPlayer() {
        Player player = queryAndIfNeededCreatePlayer();
        if ( player.getPlayerId() != null ) {
            logger.info( "Player " + player + " is already registered." );
            return;
        }
        UUID playerId = gameServiceRESTAdapter.sendGetRequestForPlayerId( player.getName(), player.getEmail() );
        if ( playerId == null ) {
            playerId = gameServiceRESTAdapter.sendPostRequestForPlayerId( player.getName(), player.getEmail() );
        }
        if ( playerId == null ) {
            logger.warn( "Registration for player " + player + " failed." );
            return;
        }
        player.assignPlayerId( playerId );
        Game activeGame = gameApplicationService.queryActiveGame();
        if ( activeGame != null ) player.setGameId( gameApplicationService.queryActiveGame().getGameId() );

        // We need the queue now, not at joining the game ... so we "guess" the queue name.
        openRabbitQueue( player );
        playerRepository.save( player );
        logger.info( "PlayerId sucessfully obtained for " + player + ", is now registered." );
    }


    /**
     * Check if our player is not currently in a game, and if so, let him join the game -
     * if there is one, and it is open.
     */
    public void letPlayerJoinOpenGame() {
        logger.info( "Trying to join game ..." );
        Player player = queryAndIfNeededCreatePlayer();
        Game activeGame = gameApplicationService.queryActiveGame();
        if ( activeGame == null ) {
            logger.info( "No open game at the moment - cannot join a game." );
            return;
        }
        if ( !activeGame.getOurPlayerHasJoined() ) {
            String playerQueue =
                    gameServiceRESTAdapter.sendPutRequestToLetPlayerJoinGame( activeGame.getGameId(), player.getPlayerId() );
            if ( playerQueue == null ) {
                logger.warn( "letPlayerJoinOpenGame: no join happened!" );
                return;
            }
            // Player queue is set already at registering - but we do it again
            if ( playerQueue != null ) player.setPlayerQueue( playerQueue );
        }
        openRabbitQueue( player );
        playerRepository.save( player );
        logger.info( "Player successfully joined game " + activeGame + ", listening via player queue " +
                player.getPlayerQueue() );
    }


    /**
     * Try to open the queue using the given name
     * @param player
     */
    protected void openRabbitQueue( Player player ) {
        String playerQueue = player.getPlayerQueue();
        if ( playerQueue == null ) throw new PlayerException( "playerQueue == null" );
        AbstractMessageListenerContainer listenerContainer = (AbstractMessageListenerContainer)
                rabbitListenerEndpointRegistry.getListenerContainer( "player-queue" );
        logger.debug( "listenerContainer.isRunning(): " + listenerContainer.isRunning() );
        String[] queueNames = listenerContainer.getQueueNames();
        if ( !Arrays.stream(queueNames).anyMatch( s->s.equals( playerQueue ) ) ) {
            listenerContainer.addQueueNames( player.getPlayerQueue() );
            logger.info( "Added queue " + playerQueue + " to listener." );
        }
        else {
            logger.info( "Queue " + playerQueue + " is already listened to.");
        }

    }


    /**
     * @param playerId
     * @param creditBalanceAsInt
     */
    public void adjustBankAccount( UUID playerId, Integer creditBalanceAsInt ) {
        logger.info( "Adjust bank account to " + creditBalanceAsInt );
        Money newCreditBalance = Money.from( creditBalanceAsInt );
        Player player = queryAndIfNeededCreatePlayer();
        tradingAccountApplicationService.updateCreditBalance( newCreditBalance );
        playerRepository.save( player );
    }




    public void submitRoundCommands() {
        logger.info( "Define and then submit commands ..." );
        Player player = queryAndIfNeededCreatePlayer();
        TradingAccount tradingAccount = tradingAccountApplicationService.queryAndIfNeededCreateTradingAccount();
        Command playerCommand = player.decideNextCommand( tradingAccount );
        tradingAccountApplicationService.save( tradingAccount );
        robotApplicationService.decideAllRobotCommands();
        List<Command> allCommands = robotApplicationService.currentRobotCommands();
        if ( playerCommand != null ) allCommands.add( playerCommand );

        Game currentGame = gameApplicationService.queryActiveGame();
        if ( currentGame != null && currentGame.getGameStatus().isRunning() ) {
            for ( Command command : allCommands ) gameServiceRESTAdapter.sendPostRequestForCommand( command );
        }
        logger.info( "Sent " + allCommands.size() + " commands!" );
    }

}

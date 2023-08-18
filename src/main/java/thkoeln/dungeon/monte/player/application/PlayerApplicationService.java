package thkoeln.dungeon.monte.player.application;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.game.application.GameApplicationService;
import thkoeln.dungeon.monte.game.domain.Game;
import thkoeln.dungeon.monte.player.domain.Player;
import thkoeln.dungeon.monte.player.domain.PlayerException;
import thkoeln.dungeon.monte.player.domain.PlayerRepository;
import thkoeln.dungeon.monte.core.restadapter.GameServiceRESTAdapter;

import java.util.Arrays;
import java.util.List;
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
    private RabbitAdmin rabbitAdmin;


    @Value("${dungeon.playerName}")
    private String playerName;

    @Value("${dungeon.playerEmail}")
    private String playerEmail;

    private static final int INDEX_FOR_A_IN_ASCII_TABLE = 65;

    @Autowired
    public PlayerApplicationService(
            PlayerRepository playerRepository,
            GameApplicationService gameApplicationService,
            GameServiceRESTAdapter gameServiceRESTAdapter,
            RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry,
            RabbitAdmin rabbitAdmin
    ) {
        this.playerRepository = playerRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.gameApplicationService = gameApplicationService;
        this.rabbitListenerEndpointRegistry = rabbitListenerEndpointRegistry;
        this.rabbitAdmin = rabbitAdmin;
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
            player = Player.ownPlayer( playerName, playerEmail );
            playerRepository.save( player );
            logger.info( "Created new player (not yet registered): " + player );
        }
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
        playerRepository.save( player );
        logger.info( "PlayerId sucessfully obtained for " + player + ", is now registered." );
    }


    /**
     * Check if our player is not currently in a game, and if so, let him join the game -
     * if there is one, and it is open.
     * @return True, if the player joined a game, false otherwise.
     */
    public boolean letPlayerJoinOpenGame() {
        logger.info( "Trying to join game ..." );
        Player player = queryAndIfNeededCreatePlayer();
        Game activeGame = gameApplicationService.queryAndIfNeededFetchRemoteGame();
        if ( activeGame == null ) {
            logger.info( "No open game at the moment - cannot join a game." );
            return false;
        }
        if ( !activeGame.getOurPlayerHasJoined() ) {
            String playerExchange =
                    gameServiceRESTAdapter.sendPutRequestToLetPlayerJoinGame( activeGame.getGameId(), player.getPlayerId() );
            if ( playerExchange == null ) {
                logger.warn( "letPlayerJoinOpenGame: no join happened!" );
                return false;
            }
            // Player queue is set already at registering - but we do it again
            if ( playerExchange != null ) player.setPlayerExchange( playerExchange );
        }
        player.setGameId( activeGame.getGameId() );
        openRabbitQueue( player );
        playerRepository.save( player );
        logger.info( "Player successfully joined game " + activeGame + ", listening via player queue " +
                player.getPlayerExchange() );
        return true;
    }


    /**
     * Poll in regular intervals if there is now game open, and if so, join it.
     */
    public void pollForOpenGame() {
        logger.info( "Polling for open game ..." );
        while ( !letPlayerJoinOpenGame() ) {
            logger.info( "No open game at the moment - polling for open game again in 5 seconds ..." );
            try {
                Thread.sleep( 5000 );
            }
            catch ( InterruptedException e ) {
                logger.error( "pollForOpenGame: sleep interrupted!" );
            }
        }
    }



    /**
     * Try to open the queue using the given name
     * @param player
     */
    protected void openRabbitQueue( Player player ) {
        String playerExchange = player.getPlayerExchange();
        if ( playerExchange == null ) throw new PlayerException( "playerExchange == null" );
        AbstractMessageListenerContainer listenerContainer = (AbstractMessageListenerContainer)
                rabbitListenerEndpointRegistry.getListenerContainer( "player-queue" );
        logger.debug( "listenerContainer.isRunning(): " + listenerContainer.isRunning() );

        var playerQueueId = "player-queue";
        var queue = QueueBuilder.durable(playerQueueId)
            .build();
        var exchange = ExchangeBuilder.topicExchange(player.getPlayerExchange())
            .build();
        var binding = BindingBuilder
            .bind(queue)
            .to(exchange)
            .with("#")
            .noargs();
        rabbitAdmin.declareBinding(binding);

        String[] queueNames = listenerContainer.getQueueNames();

        if ( !Arrays.stream(queueNames).anyMatch( s->s.equals( playerQueueId ) ) ) {
            listenerContainer.addQueueNames( playerQueueId );
            logger.info( "Added queue " + playerQueueId + " to listener." );
        }
        else {
            logger.info( "Queue " + playerQueueId + " is already listened to.");
        }

    }


    public void cleanupAfterFinishingGame() {
        logger.info( "Cleaning up after finishing game ..." );
        Player player = queryAndIfNeededCreatePlayer();
        gameApplicationService.finishGame();
        player.setGameId( null );
        playerRepository.save( player );
        logger.info( "Cleaned up after finishing game." );
    }

    /**
     * Add another (enemy) player that was revealed via the RobotsRevealedIntegrationEvent.
     * We only get an 8-char-shortname, basically the first 8 char of the player id.
     * It might just as well be our own player - therefore we need to check this match.
     * @param playerShortName
     * @return the new enemy player, or the one found in the database, or null, if the short name belongs to my
     *          own player.
     */
    public Player addEnemyPlayer( String playerShortName ) {
        if ( playerShortName == null ) throw new PlayerException( "playerShortName == null" );
        logger.info( "Learned about a new player with short name " + playerShortName );
        Player meMyselfAndI = queryAndIfNeededCreatePlayer();
        if ( meMyselfAndI.matchesShortName( playerShortName ) ) {
            logger.info( "... oh, that is me." );
            return null;
        }
        List<Player> enemies = playerRepository.findByEnemyShortName( playerShortName );
        if ( enemies.size() > 0 ) {
            logger.info( "The enemy exists already in my database." );
            return enemies.get( 0 );
        }
        Player newEnemyPlayer = Player.enemyPlayer( playerShortName );
        newEnemyPlayer.setEnemyChar( defineNextEnemyLetter() );
        playerRepository.save( newEnemyPlayer );
        return newEnemyPlayer;
    }


    private char defineNextEnemyLetter() {
        int numberOfEnemies = playerRepository.countAllByEnemyCharIsNotNull();
        Character c = Character.valueOf( (char) (INDEX_FOR_A_IN_ASCII_TABLE + numberOfEnemies) );
        return c;
    }

}

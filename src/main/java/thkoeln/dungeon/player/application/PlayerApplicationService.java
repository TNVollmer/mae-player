package thkoeln.dungeon.player.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.domainprimitives.Moneten;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;

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
    private ModelMapper modelMapper = new ModelMapper();

    private PlayerRepository playerRepository;
    private GameApplicationService gameApplicationService;
    private GameServiceRESTAdapter gameServiceRESTAdapter;
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;


    @Value("${dungeon.playerName}")
    private String playerName;

    @Value("${dungeon.playerEmail}")
    private String playerEmail;

    @Autowired
    public PlayerApplicationService(
            PlayerRepository playerRepository,
            GameApplicationService gameApplicationService,
            GameServiceRESTAdapter gameServiceRESTAdapter,
            RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry ) {
        this.playerRepository = playerRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.gameApplicationService = gameApplicationService;
        this.rabbitListenerEndpointRegistry = rabbitListenerEndpointRegistry;
    }


    /**
     * Fetch the existing player. If there isn't one yet, it is created and stored to the database.
     * @return The current player.
     */
    public Player queryAndIfNeededCreatePlayer() {
        Player player = null;
        List<Player> players = playerRepository.findAll();
        if ( players.size() >= 1 ) {
            return players.get( 0 );
        }
        else {
            player = new Player();
            player.setName( playerName );
            player.setEmail( playerEmail );
            playerRepository.save(player);
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
            logger.error( "Registration for player " + player + " failed." );
            return;
        }
        player.assignPlayerId( playerId );
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
        Player player = queryAndIfNeededCreatePlayer();
        Optional<Game> perhapsOpenGame = gameApplicationService.queryActiveGame();
        if ( !perhapsOpenGame.isPresent() ) {
            logger.info( "No open game at the moment - cannot join a game." );
            return;
        }
        Game game = perhapsOpenGame.get();
        String playerQueue =
                gameServiceRESTAdapter.sendPutRequestToLetPlayerJoinGame( game.getGameId(), player.getPlayerId() );
        if ( playerQueue == null ) {
            logger.warn( "letPlayerJoinOpenGame: no join happened!" );
            return;
        }
        // Player queue is set already at registering
        // player.setPlayerQueue( playerQueue );
        // openRabbitQueue( player );
        player.setCurrentGame( game );
        playerRepository.save( player );
        logger.info( "Player successfully joined game " + game + ", listening via player queue " + playerQueue );
    }


    protected void openRabbitQueue( Player player ) {
        AbstractMessageListenerContainer listenerContainer = (AbstractMessageListenerContainer)
                rabbitListenerEndpointRegistry.getListenerContainer( "player-queue" );
        listenerContainer.addQueueNames( player.getPlayerQueue() );
    }


    /**
     * @param playerId
     * @param moneyAsInt
     */
    public void adjustBankAccount( UUID playerId, Integer moneyAsInt ) {
        Moneten newMoney = Moneten.fromInteger( moneyAsInt );
        Player player = queryAndIfNeededCreatePlayer();
        player.setMoneten( newMoney );
        playerRepository.save( player );
    }

}

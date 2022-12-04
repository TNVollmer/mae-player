package thkoeln.dungeon.player.application;

import lombok.val;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.domainprimitives.Moneten;
import thkoeln.dungeon.eventlistener.__OBSOLETE_RabbitMQListener;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.application.events.GameEventsCallback;
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


    @Value("${dungeon.playerName}")
    private String playerName;

    @Value("${dungeon.playerEmail}")
    private String playerEmail;

    @Autowired
    public PlayerApplicationService(
            PlayerRepository playerRepository,
            GameApplicationService gameApplicationService,
            GameServiceRESTAdapter gameServiceRESTAdapter ) {
        this.playerRepository = playerRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.gameApplicationService = gameApplicationService;
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
        player.setPlayerId( playerId );
        playerRepository.save( player );
        logger.info( "PlayerId sucessfully obtained for " + player + ", is now registered." );
    }


    /**
     * Check if our player is not currently in a game, and if so, let him join the game -
     * if there is one, and it is open.
     */
    public void letPlayerJoinOpenGame() {
        Player player = queryAndIfNeededCreatePlayer();
        Optional<Game> perhapsOpenGame = gameApplicationService.retrieveActiveGame();
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
        player.setPlayerQueue( playerQueue );
        playerRepository.save( player );
        logger.info( "Player successfully joined game " + game + ", listening via player queue " + playerQueue );

        // todo add queue here
        // queue: player-fe529bc7-1bae-4017-8691-ccc6a744ff05
    }



    /**
     * @param playerId
     * @param moneyAsInt
     */
    public void adjustBankAccount( UUID playerId, Integer moneyAsInt ) {
        Moneten newMoney = Moneten.fromInteger( moneyAsInt );
        Player foundPlayer = findUniquePlayerById(playerId);
        foundPlayer.setMoneten( newMoney );
        playerRepository.save(foundPlayer);
    }

    /**
     * Find a unique player using the given id.
     * Throws PlayerApplicationException if no or more players are found.
     * todo should not be needed
     */
    public Player findUniquePlayerById( UUID playerId ) {
        val foundPlayers = playerRepository.findByPlayerId( playerId );
        if ( foundPlayers.size() != 1 ) {
            throw new PlayerApplicationException( "Found not exactly 1 player with playerId " + playerId
                    + ", but " + foundPlayers.size() );
        }
        return foundPlayers.get( 0 );
    }
}

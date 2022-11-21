package thkoeln.dungeon.player.application;

import lombok.val;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.domainprimitives.Moneten;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.PlayerRegistryDto;
import thkoeln.dungeon.restadapter.RESTAdapterException;

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
    private ModelMapper modelMapper = new ModelMapper();
    private Environment env;

    private PlayerRepository playerRepository;
    private GameApplicationService gameApplicationService;
    private GameRepository gameRepository;
    private GameServiceRESTAdapter gameServiceRESTAdapter;

    @Value("${dungeon.playerName}")
    private String playerName;

    @Value("${dungeon.playerEmail}")
    private String playerEmail;

    @Autowired
    public PlayerApplicationService(
            PlayerRepository playerRepository,
            GameApplicationService gameApplicationService,
            GameRepository gameRepository,
            GameServiceRESTAdapter gameServiceRESTAdapter,
            Environment env ) {
        this.playerRepository = playerRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.gameRepository = gameRepository;
        this.gameApplicationService = gameApplicationService;
        this.env = env;
    }

    /**
     * Create player(s), if not there already
     */
    public void createPlayer() {
        List<Player> players = playerRepository.findAll();
        if (players.size() > 0) return;
        Player player = new Player();
        player.setName( playerName );
        player.setEmail( playerEmail );
        playerRepository.save(player);
        logger.info("Created new player: " + player);
    }


    /**
     * Obtain the bearer token for all players defined in this service
     */
    public void obtainPlayerId() {
        List<Player> players = playerRepository.findAll();
        if ( players.size() != 1 ) logger.error( "Found " + players.size() + " players!" );
        obtainPlayerId( players.get( 0 ) );
    }


    /**
     * Obtain the bearer token for one specific player
     * @param player
     */
    protected void obtainPlayerId( Player player ) {
        if ( player.getPlayerId() != null ) return;
        UUID playerId = gameServiceRESTAdapter.obtainPlayerIdForPlayer( player.getName(), player.getEmail() );
        if ( playerId == null ) throw new PlayerApplicationException( "Can't register player " + player );
        player.setPlayerId( playerId );
        playerRepository.save( player );
        logger.info( "PlayerId sucessfully obtained for " + player );
    }



    /**
     * We have received the event that a game has been created. So make sure that the game state is suitable,
     * and that our player(s) can join.
     * for the game.
     * @param gameId
     */
    public void registerPlayerForGame( UUID gameId ) {
        Game game = gameApplicationService.gameExternallyCreated( gameId );
        List<Player> players = playerRepository.findAll();
        for (Player player : players) registerOnePlayerForGame( player, game );
    }

    /**
     * Register one specific player for a game
     * @param player
     * @param game
     */
    protected void registerOnePlayerForGame( Player player, Game game ) {
        if ( player.getPlayerId() == null ) {
            logger.error( "Player" + player + " has no player ID!" );
            return;
        }
        try {
            UUID transactionId = gameServiceRESTAdapter.registerPlayerForGame( game.getGameId(), player.getPlayerId() );
            if ( transactionId != null ) {
                player.registerFor( game, transactionId );
                playerRepository.save( player );
                logger.info( "Player " + player + " successfully registered for game " + game +
                        " with transactionId " + transactionId );
            }
        } catch ( RESTAdapterException e ) {
            // shouldn't happen - cannot do more than logging and retrying later
            logger.error( "Something went wrong ... ");
            // todo - err msg wrong
            logger.error( "Could not register " + player + " for " + game +
                    "\nOriginal Exception:\n" + e.getMessage() + "\n" + e.getStackTrace() );
        }
    }

     /**
     * Method to be called when the answer event after a game registration has been received
     */
    public void assignPlayerId( UUID registrationTransactionId, UUID playerId ) {
        logger.info( "Assign playerId from game registration" );
        if ( registrationTransactionId == null )
            throw new PlayerApplicationException( "registrationTransactionId cannot be null!" );
        if ( playerId == null )  throw new PlayerApplicationException( "PlayerId cannot be null!" );
        List<Player> foundPlayers =
                playerRepository.findByRegistrationTransactionId( registrationTransactionId );
        if ( foundPlayers.size() != 1 ) {
            throw new PlayerApplicationException( "Found not exactly 1 player with transactionId"
                    + registrationTransactionId + ", but " + foundPlayers.size() );
        }
        Player player = foundPlayers.get( 0 );
        player.setPlayerId( playerId );
        playerRepository.save( player );
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

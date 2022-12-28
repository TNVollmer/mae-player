package thkoeln.dungeon.monte.player.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.domainprimitives.*;
import thkoeln.dungeon.monte.game.application.GameApplicationService;
import thkoeln.dungeon.monte.game.domain.Game;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;
import thkoeln.dungeon.monte.planet.application.PlanetConsolePrintDto;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.player.domain.Player;
import thkoeln.dungeon.monte.player.domain.PlayerException;
import thkoeln.dungeon.monte.player.domain.PlayerRepository;
import thkoeln.dungeon.monte.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.monte.robot.application.RobotApplicationService;

import java.util.*;

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
    private RobotApplicationService robotApplicationService;
    private PlanetApplicationService planetApplicationService;
    private GameServiceRESTAdapter gameServiceRESTAdapter;
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;
    private Environment environment;


    @Value("${dungeon.playerName}")
    private String playerName;

    @Value("${dungeon.playerEmail}")
    private String playerEmail;

    @Autowired
    public PlayerApplicationService(
            PlayerRepository playerRepository,
            GameApplicationService gameApplicationService,
            GameServiceRESTAdapter gameServiceRESTAdapter,
            RobotApplicationService robotApplicationService,
            PlanetApplicationService planetApplicationService,
            Environment environment,
            RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry ) {
        this.playerRepository = playerRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.gameApplicationService = gameApplicationService;
        this.robotApplicationService = robotApplicationService;
        this.environment = environment;
        this.rabbitListenerEndpointRegistry = rabbitListenerEndpointRegistry;
        this.planetApplicationService = planetApplicationService;
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
        logger.info( "Trying to join game ..." );
        Player player = queryAndIfNeededCreatePlayer();
        Optional<Game> perhapsOpenGame = gameApplicationService.queryActiveGame();
        if ( !perhapsOpenGame.isPresent() ) {
            logger.info( "No open game at the moment - cannot join a game." );
            return;
        }
        Game game = perhapsOpenGame.get();
        if ( !game.getOurPlayerHasJoined() ) {
            String playerQueue =
                    gameServiceRESTAdapter.sendPutRequestToLetPlayerJoinGame( game.getGameId(), player.getPlayerId() );
            if ( playerQueue == null ) {
                logger.warn( "letPlayerJoinOpenGame: no join happened!" );
                return;
            }
            // Player queue is set already at registering - but we do it again
            if ( playerQueue != null ) player.setPlayerQueue( playerQueue );
        }
        openRabbitQueue( player );
        playerRepository.save( player );
        logger.info( "Player successfully joined game " + game + ", listening via player queue " + player.getPlayerQueue() );
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
     * @param moneyAsInt
     */
    public void adjustBankAccount( UUID playerId, Integer moneyAsInt ) {
        logger.info( "Adjust bank account to " + moneyAsInt );
        Money newMoney = Money.fromInteger( moneyAsInt );
        Player player = queryAndIfNeededCreatePlayer();
        player.setMoney( newMoney );
        playerRepository.save( player );
    }




    /**
     * Buys new robots via REST command to Game service
     * todo move to strategy class
     * @param numOfNewRobots
     */
    public void buyRobots( int numOfNewRobots ) {
        if ( numOfNewRobots < 0 ) throw new PlayerException( "numOfNewRobots < 0" );
        Player player = queryAndIfNeededCreatePlayer();
        Optional<Game> currentGameOptional = gameApplicationService.queryActiveGame();
        if ( currentGameOptional.isPresent() && currentGameOptional.get().getGameStatus().isRunning() ) {
            CommandObject commandObject = new CommandObject(
                    CommandType.BUYING, null, null, "ROBOT", numOfNewRobots);
            Command command = new Command(
                    currentGameOptional.get().getGameId(), player.getPlayerId(), null, CommandType.BUYING, commandObject);
            gameServiceRESTAdapter.sendPostRequestForCommand(command);
        }
    }


    /**
     * @return Print the complete player status formatted for the console.
     */
    public String consolePrintStatus() {
        String printString =  environment.getProperty( "ANSI_RED" );
        printString += gameApplicationService.consolePrintStatus() + robotApplicationService.consolePrintStatus();
        printString += environment.getProperty( "ANSI_RESET" );
        printString += consolePrintMapWithPlanetsAndRobots();
        return printString;
    }


    /**
     * @return The map (or several cluster maps) of all known planets formatted for the console.
     *      This involves planets, but also robots located on planets. "planet" package doesn't know
     *      "robot" (but the other way around), so the best way to orchestrate this is from here.
     */
    public String consolePrintMapWithPlanetsAndRobots() {
        String printString = "";
        int currentClusterNumber = 0;
        Map<Planet, TwoDimDynamicArray<Planet>> allClusterMap = planetApplicationService.allPlanetsAsClusterMap();
        for ( TwoDimDynamicArray<Planet> planetCluster : allClusterMap.values() ) {
            currentClusterNumber += 1;
            printString += "\nPlanet cluster no. " + currentClusterNumber + ":\n";
            printString += consolePrintOneMapCluster( planetCluster );
        }
        return printString;
    }

    /**
     * todo this looks wrong here ... maybe move to a console printer class
     */
    private String consolePrintOneMapCluster(TwoDimDynamicArray<Planet> planetCluster ) {
        Coordinate bottomRightCorner = planetCluster.getMaxCoordinate();
        String printString = PlanetConsolePrintDto.printTopRow( bottomRightCorner );
        TwoDimDynamicArray<PlanetConsolePrintDto> printArray = new TwoDimDynamicArray<>( bottomRightCorner );
        for ( int y = 0; y < bottomRightCorner.getY(); y++ ) {
            for ( int x = 0; x < bottomRightCorner.getX(); x++) {
                Planet planet = planetCluster.at( x, y );
                PlanetConsolePrintDto planetNeighboursDto = new PlanetConsolePrintDto ( planet );
                planetNeighboursDto.setRobotString(
                        robotApplicationService.consolePrintRobotsForPlanetOnMap( planet ) );
                printArray.put( x, y, planetNeighboursDto );
            }
        }
        for ( int y = 0; y < bottomRightCorner.getY(); y++ ) {
            for ( int cellLineNumber = 0; cellLineNumber <= 3; cellLineNumber++ ) {
                if ( cellLineNumber == 2 ) printString += PlanetConsolePrintDto.printRowNumber( y );
                else printString += "\n" + PlanetConsolePrintDto.empty();
                for (int x = 0; x < bottomRightCorner.getX(); x++) {
                    printString += printArray.at( x, y ).printLine( cellLineNumber );
                }
            }
        }
        return printString;
    }

}

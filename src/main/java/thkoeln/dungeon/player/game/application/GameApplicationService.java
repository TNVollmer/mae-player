package thkoeln.dungeon.player.game.application;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.restadapter.GameDto;
import thkoeln.dungeon.player.core.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.player.game.domain.Game;
import thkoeln.dungeon.player.game.domain.GameException;
import thkoeln.dungeon.player.game.domain.GameRepository;
import thkoeln.dungeon.player.game.domain.GameStatus;
import thkoeln.dungeon.player.planet.domain.PlanetRepository;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class GameApplicationService {
    private final GameRepository gameRepository;
    private final GameServiceRESTAdapter gameServiceRESTAdapter;
    private final Environment environment;
    private final RobotRepository robotRepository;
    private final PlanetRepository planetRepository;

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public GameApplicationService(GameRepository gameRepository,
                                  GameServiceRESTAdapter gameServiceRESTAdapter,
                                  Environment environment, RobotRepository robotRepository, PlanetRepository planetRepository) {
        this.gameRepository = gameRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.environment = environment;
        this.robotRepository = robotRepository;
        this.planetRepository = planetRepository;
    }


    /**
     * Throw away all stored games, and fetch the currently active game (if any).
     */
    public Game fetchRemoteGame() {
        gameRepository.deleteAll();
        GameDto[] openGameDtos = gameServiceRESTAdapter.sendGetRequestForAllActiveGames();
        if ( openGameDtos.length > 0 ) {
            Game game = new Game();
            modelMapper.map( openGameDtos[0], game );
            game.checkIfOurPlayerHasJoined(
                    openGameDtos[0].getParticipatingPlayers(), environment.getProperty( "dungeon.playerName" ) );
            gameRepository.save( game );
            log.info( "Open game found: " + game );
            if ( openGameDtos.length > 1 ) log.warn( "More than one open game found!" );
            return game;
        }
        return null;
    }

    /**
     * @return The currently available active (CREATED or RUNNING) game, or null if there is no such game
     */
    public Game queryActiveGame() {
        log.debug( "queryActiveGame() ..." );
        List<Game> foundGames = gameRepository.findAll();
        if ( foundGames.size() > 1 ) {
            log.error( "More than one game found!" );
            for ( Game game : foundGames ) {
                log.error( "Game: " + game.getGameId() + ", " + game.getGameStatus() + ", internal ID: " + game.getId() );
            }
            throw new GameException( "More than one game!" );
        }
        if ( foundGames.size() == 1 ) {
            return foundGames.get( 0 );
        } else {
            return null;
        }
    }


    /**
     * @return The currently available active (CREATED or RUNNING) game. If there is no such game, try to fetch
     * it from the remote server.
     */
    public Game queryAndIfNeededFetchRemoteGame() {
        Game game = queryActiveGame();
        if ( game == null ) {
            game = fetchRemoteGame();
        }
        return game;
    }


    /**
     * We received notice (by event) that a certain game has started.
     * In that case, we simply assume that there is only ONE game currently running, and that it is THIS
     * game.
     */
    public void startGame( UUID gameId ) {
        changeGameStatus( gameId, GameStatus.STARTED );
    }

    public void endGame(UUID gameId) {
        robotRepository.deleteAll();
        planetRepository.deleteAll();
        log.info("Cleared all Data!");
    }

    /**
     * We received notice (by event) that the current game has finished.
     */
    public void finishGame() {
        log.info( "Finish game" );
        Game game = queryActiveGame();
        if ( game == null ) {
            log.error( "No active game found!" );
            return;
        }
        game.setGameStatus( GameStatus.ENDED );
        gameRepository.save( game );
    }


    /**
     * We received notice (by event) that a certain game has finished.
     *
     * @param gameId
     */
    public void changeGameStatus( UUID gameId, GameStatus gameStatus ) {
        log.info( "Change status for game with gameId " + gameId + " to " + gameStatus );
        if ( gameId == null ) throw new GameException( "gameId == null" );

        Game game = queryActiveGame();
        if ( game == null ) {
            log.error( "No game with id " + gameId + " found!" );
            return;
        }
        game.setGameStatus( gameStatus );
        gameRepository.save( game );
    }


    public void roundStarted( Integer roundNumber ) {
        Game game = queryActiveGame();
        if ( game == null ) throw new GameException( "No active game!" );
        game.setCurrentRoundNumber( roundNumber );
        gameRepository.save( game );
    }
}

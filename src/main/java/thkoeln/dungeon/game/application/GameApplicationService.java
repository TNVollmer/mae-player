package thkoeln.dungeon.game.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameException;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.game.domain.GameStatus;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameApplicationService {
    private GameRepository gameRepository;
    private GameServiceRESTAdapter gameServiceRESTAdapter;

    private Logger logger = LoggerFactory.getLogger( GameApplicationService.class );
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public GameApplicationService(GameRepository gameRepository,
                                  GameServiceRESTAdapter gameServiceRESTAdapter ) {
        this.gameRepository = gameRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
    }


    /**
     * Throw away all stored games, and fetch a new one. Only interesting if open.
     */
    public void fetchRemoteGame() {
        gameRepository.deleteAll();
        GameDto[] openGameDtos = gameServiceRESTAdapter.checkForOpenGames();
        if ( openGameDtos.length > 0 ) {
            Game game = new Game();
            modelMapper.map( openGameDtos[0], game );
            gameRepository.save( game );
            logger.info( "Open game found: " + game );
            if ( openGameDtos.length > 1 ) logger.warn( "More than one open game found!" );
        }
    }

    /**
     * @return The currently available open game
     */
    public Optional<Game> queryActiveGame() {
        List<Game> foundGames = gameRepository.findAllByGameStatusBetween( GameStatus.CREATED, GameStatus.RUNNING );
        if ( foundGames.size() > 1 ) throw new GameException( "More than one active game!" );
        if ( foundGames.size() == 1 ) {
            return Optional.of( foundGames.get( 0 ) );
        }
        else {
            return Optional.empty();
        }
    }



    /**
     * We received notice (by event) that a certain game has started.
     * In that case, we simply assume that there is only ONE game currently running, and that it is THIS
     * game.
     */
    public void startGame( UUID gameId ) {
        changeGameStatus( gameId, GameStatus.RUNNING );
    }



    /**
     * We received notice (by event) that a certain game has finished.
     * @param gameId
     */
    public void finishGame( UUID gameId ) {
        changeGameStatus( gameId, GameStatus.FINISHED );
    }



    /**
     * We received notice (by event) that a certain game has finished.
     * @param gameId
     */
    public void changeGameStatus( UUID gameId, GameStatus gameStatus ) {
        logger.info( "Change status for game with gameId " + gameId + " to " + gameStatus );
        if ( gameId == null ) throw new GameException( "gameId == null" );

        Optional<Game> perhapsGame = queryActiveGame();
        if ( !perhapsGame.isPresent() ) {
            logger.error( "No game with id " + gameId + " found!" );
            return;
        }
        Game game = perhapsGame.get();
        game.setGameStatus( gameStatus );
        gameRepository.save( game );
    }
}

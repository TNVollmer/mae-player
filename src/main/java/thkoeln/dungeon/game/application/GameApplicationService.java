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
import thkoeln.dungeon.restadapter.RESTAdapterException;

import java.util.ArrayList;
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
    public void resetGames() {
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




    public Optional<Game> retrieveRunningGame() {
        List<Game> foundGames = gameRepository.findAllByGameStatusEquals( GameStatus.RUNNING );
        if ( foundGames.size() > 1 ) throw new GameException( "More than one running game!" );
        if ( foundGames.size() == 1 ) {
            return Optional.of( foundGames.get( 0 ) );
        }
        else {
            return Optional.empty();
        }
    }


    public Optional<Game> findByGameId( UUID gameId ) {
        List<Game> foundGames = gameRepository.findByGameId( gameId );
        if ( foundGames.size() > 1 ) {
            throw new GameException( "Found more than one game with gameId " + gameId );
        }
        if ( foundGames.size() == 1 ) {
            return Optional.of( foundGames.get( 0 ) );
        }
        else {
            return Optional.empty();
        }
    }



    /**
     * We received notice (by event) that a certain game has been created.
     * @param gameId ID of the new game
     */
    public Game gameExternallyCreated ( UUID gameId ) {
        logger.info( "Processing external event that the game with gameId " + gameId + " has been created" );
        Game game = findAndIfNeededCreateGame( gameId );
        game.resetToNewlyCreated();
        gameRepository.save( game );
        return game;
    }




    /**
     * We received notice (by event) that a certain game has started.
     * In that case, we simply assume that there is only ONE game currently running, and that it is THIS
     * game. All other games I might have here in the player will be set to GAME_FINISHED state.
     * @param gameId ID of the new game
     */
    public Game gameExternallyStarted ( UUID gameId ) {
        logger.info( "Processing external event that the game with gameId " + gameId + " has started" );
        List<Game> allGames = gameRepository.findAll();
        for ( Game game: allGames ) {
            game.setGameStatus( GameStatus.FINISHED);
            gameRepository.save( game );
        }
        Game game = findAndIfNeededCreateGame( gameId );
        game.setGameStatus( GameStatus.RUNNING);
        gameRepository.save( game );
        return game;
    }



    /**
     * We received notice (by event) that a certain game has finished.
     * @param gameId
     */
    public Game gameExternallyFinished( UUID gameId ) {
        logger.info( "Processing external event that the game with gameId " + gameId + " has ended" );
        Game game = findAndIfNeededCreateGame( gameId );
        game.setGameStatus( GameStatus.FINISHED);
        gameRepository.save( game );
        return game;
    }




    private Game findAndIfNeededCreateGame( UUID gameId ) {
        List<Game> fittingGames = gameRepository.findByGameId( gameId );
        Game game = null;
        if ( fittingGames.size() == 0 ) {
            game = Game.newlyCreatedGame( gameId );
        }
        else {
            if ( fittingGames.size() > 1 ) throw new GameException( "More than one game with gameId " + gameId );
            game = fittingGames.get( 0 );
        }
        gameRepository.save( game );
        return game;
    }


    /**
     * To be called by event consumer listening to GameService event
     * @param gameId
     */
    public void newRound( UUID gameId, Integer roundNumber ) {
        logger.info( "Processing 'new round' event for round no. " + roundNumber );
        // todo
    }



}

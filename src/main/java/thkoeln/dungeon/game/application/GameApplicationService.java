package thkoeln.dungeon.game.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventconsumer.game.GameEventConsumerService;
import thkoeln.dungeon.eventconsumer.game.GameStatusEvent;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameException;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.game.domain.GameStatus;
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;

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
                                  GameServiceRESTAdapter gameServiceRESTAdapter,
                                  PlayerApplicationService playerApplicationService ) {
        this.gameRepository = gameRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
    }


    public List<Game> retrieveActiveGames() {
        return gameRepository.findAllByGameStatusEquals( GameStatus.GAME_RUNNING );
    }


    /**
     * Makes sure that our own game state is consistent with what GameService says.
     * We take a very simple approach here. We, as a Player, don't manage any game
     * state - we just assume that GameService does a proper job. So we just store
     * the incoming games. Only in the case that a game should suddenly "disappear",
     * we keep it and mark it as ORPHANED - there may be local references to it.
     */
    public void synchronizeGameState() {
        GameDto[] gameDtos = new GameDto[0];
        try {
            gameDtos = gameServiceRESTAdapter.fetchCurrentGameState();
        }
        catch ( UnexpectedRESTException | RESTConnectionFailureException e ) {
            logger.warn( "Problems with GameService while synchronizing game state - need to try again later.\n" +
                    e.getStackTrace() );
        }

        // We need to treat the new games (those we haven't stored yet) and those we
        // already have in a different way. Therefore let's split the list.
        List<GameDto> unknownGameDtos = new ArrayList<>();
        List<GameDto> knownGameDtos = new ArrayList<>();
        for ( GameDto gameDto: gameDtos ) {
            if ( gameRepository.existsByGameId( gameDto.getGameId() ) ) knownGameDtos.add( gameDto );
            else unknownGameDtos.add( gameDto );
        }

        List<Game> storedGames = gameRepository.findAll();
        for ( Game game: storedGames ) {
            Optional<GameDto> foundDtoOptional = knownGameDtos.stream()
                    .filter( dto -> game.getGameId().equals( dto.getGameId() )).findAny();
            if ( foundDtoOptional.isPresent() ) {
                modelMapper.map( foundDtoOptional.get(), game );
                gameRepository.save( game );
                logger.info( "Updated game " + game );
            }
            else {
                game.makeOrphan();
                gameRepository.save( game );
            }
        }
        for ( GameDto gameDto: unknownGameDtos ) {
            Game game = modelMapper.map( gameDto, Game.class );
            gameRepository.save( game );
            logger.info( "Received game " + game + " for the first time");
        }
        logger.info( "Retrieval of new game state finished" );
    }


    /**
     * "Status changed" event published by GameService, esp. after a game has been created
     */
    public void gameStatusExternallyChanged( UUID gameId, GameStatus gameStatus ) {
        switch (gameStatus) {
            case CREATED:
                gameExternallyCreated(gameId);
                break;
        }
    }


    /**
     * To be called by event consumer listening to GameService event
     * @param gameId ID of the new game
     */
    public void gameExternallyCreated ( UUID gameId ) {
        logger.info( "Processing external event that the game has been created");
        List<Game> fittingGames = gameRepository.findByGameId( gameId );
        Game game = null;
        if ( fittingGames.size() == 0 ) {
            game = Game.newlyCreatedGame( gameId );
            gameRepository.save( game );
        }
        else {
            if ( fittingGames.size() > 1 ) game = mergeGamesIntoOne( fittingGames );
            game.resetToNewlyCreated();
            gameRepository.save( game );
        }
    }


    /**
     * Repair the situation that there are seemingly several games sharing the same gameId. This should not
     * happen. Do this by "merging" the games.
     * @param fittingGames
     */
    public Game mergeGamesIntoOne( List<Game> fittingGames ) {
        if ( fittingGames == null ) throw new GameException( "List of games to be merged must not be null!" );
        if ( fittingGames.size() <= 1 ) throw new GameException( "List of games to be merged must contain at least 2 entries!" );

        // todo - needs to be properly implemented
        return fittingGames.get( 0 );
    }



    /**
     * To be called by event consumer listening to GameService event
     * @param eventId
     */
    public void gameExternallyStarted ( UUID eventId ) {
        logger.info( "Processing external event that the game with id " + eventId + " has started");
        List<Game> foundGames = gameRepository.findAllByGameStatusEquals( GameStatus.GAME_RUNNING );
    }



    /**
     * To be called by event consumer listening to GameService event
     * @param gameId
     */
    public void gameEnded( UUID gameId ) {
        logger.info( "Processing 'game ended' event");
        // todo
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

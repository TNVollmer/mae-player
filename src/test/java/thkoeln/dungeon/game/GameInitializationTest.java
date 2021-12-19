package thkoeln.dungeon.game;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.restadapter.GameDto;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static thkoeln.dungeon.game.domain.GameStatus.*;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
//@ContextConfiguration( classes = DungeonPlayerConfiguration.class )
public class GameInitializationTest {
    private static final UUID GAME_ID_0 = UUID.randomUUID();
    private GameDto gameDto0 = new GameDto(GAME_ID_0, CREATED, 0 );

    private static final UUID GAME_ID_1 = UUID.randomUUID();
    private static final Integer GAME_ROW_COUNT_1 = 42;
    private GameDto gameDto1 = new GameDto(GAME_ID_1, GAME_RUNNING, GAME_ROW_COUNT_1);

    private static final UUID GAME_ID_2 = UUID.randomUUID();
    private static final Integer GAME_ROW_COUNT_2 = 200;
    private GameDto gameDto2 = new GameDto(GAME_ID_2, GAME_FINISHED, GAME_ROW_COUNT_2);

    private static final UUID GAME_ID_3 = UUID.randomUUID();
    private static final Integer GAME_ROW_COUNT_3 = 0;
    private GameDto gameDto3 = new GameDto(GAME_ID_3, CREATED, GAME_ROW_COUNT_3);

    private GameDto[] allRemoteGames;

    @Value("${GAME_SERVICE}")
    private String gameServiceURIString;
    private URI gameServiceGamesURI;

    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameApplicationService gameApplicationService;


    @Before
    public void setUp() throws Exception {
        gameRepository.deleteAll();
        gameServiceGamesURI = new URI( gameServiceURIString + "/games" );
        mockServer = MockRestServiceServer.createServer(restTemplate);

        allRemoteGames = new GameDto[3];
        allRemoteGames[0] = gameDto0;
        allRemoteGames[1] = gameDto1;
        allRemoteGames[2] = gameDto2;
    }


    @Test
    public void properlySynchronizedGameState_afterFirstCall() throws Exception {
        // given
        mockCallToGameService_initialCall();

        // when
        gameApplicationService.synchronizeGameState();

        // then
        mockServer.verify();
        List<Game> games = gameRepository.findAll();
        assertEquals( 3, games.size() );
        games = gameApplicationService.retrieveActiveGames();
        assertEquals( 1, games.size() );
        assertEquals( gameDto1.getGameId(), games.get( 0 ).getGameId() );
    }


    @Test
    public void properlySynchronizedGameState_afterSecondCall() throws Exception {
        // given
        mockCallToGameService_initialCall();
        gameApplicationService.synchronizeGameState();
        mockCallToGameService_secondCall();

        // when
        gameApplicationService.synchronizeGameState();

        // then
        mockServer.verify();
        List<Game> games = gameRepository.findAll();
        assertEquals( 4, games.size() );
        games = gameApplicationService.retrieveActiveGames();
        assertEquals( 1, games.size() );
        assertEquals(gameDto0.getGameId(), games.get( 0 ).getGameId() );
        Game game = gameRepository.findById(GAME_ID_1).get();
        assertEquals( GAME_FINISHED, game.getGameStatus() );
        game = gameRepository.findById(GAME_ID_2).get();
        assertEquals( ORPHANED, game.getGameStatus() );
        game = gameRepository.findById(GAME_ID_3).get();
        assertEquals( CREATED, game.getGameStatus() );
    }


    private void mockCallToGameService_initialCall() throws Exception {
        mockServer.expect( ExpectedCount.once(),
                        requestTo( gameServiceGamesURI ))
                .andExpect( method( GET ))
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType( MediaType.APPLICATION_JSON )
                        .body( mapper.writeValueAsString(allRemoteGames) ) );
    }

    private void mockCallToGameService_secondCall() throws Exception {
        allRemoteGames[0].setGameStatus( GAME_RUNNING );
        allRemoteGames[1].setGameStatus( GAME_FINISHED );
        allRemoteGames[2] = gameDto3;

        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect( ExpectedCount.once(),
                        requestTo( gameServiceGamesURI ))
                .andExpect( method( GET ))
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType( MediaType.APPLICATION_JSON )
                        .body( mapper.writeValueAsString(allRemoteGames) ) );
    }


}

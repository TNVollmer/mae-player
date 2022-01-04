package thkoeln.dungeon.player.application;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.restadapter.PlayerRegistryDto;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static thkoeln.dungeon.game.domain.GameStatus.*;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class PlayerBearerTokenTest {
    static {
        System.setProperty("dungeon.mode", "MULTI");
    }

    @Value("${GAME_SERVICE}")
    private String gameServiceURIString;
    private URI playersEndpointURI;
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private Environment env;

    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerApplicationService playerApplicationService;


    @Before
    public void setUp() throws Exception {
        playerRepository.deleteAll();
        gameRepository.deleteAll();
        playersEndpointURI = new URI( gameServiceURIString + "/players" );
        Game game = new Game();
        game.setGameId( UUID.randomUUID() );
        game.setGameStatus( GAME_RUNNING );
        game.setCurrentRoundCount( 22 );
        gameRepository.save( game );
    }


    @Test
    public void noExceptionWhenConnectionMissing() {
        playerApplicationService.createPlayers();
        assert( true );
    }

    @Test
    public void testCreatePlayers() {
        // given
        playerApplicationService.createPlayers();

        // when
        List<Player> allPlayers = playerRepository.findAll();

        // then
        assertEquals( Integer.valueOf( env.getProperty("dungeon.multiPlayer.number") ), allPlayers.size() );
        for ( Player player: allPlayers ) {
            assertNotNull( player.getEmail(), "player email" );
            assertNotNull( player.getName(), "player name"  );
            assertFalse( player.isReadyToPlay(), "should not be ready to play" );
        }
    }


    @Test
    public void testRegisterPlayers() throws Exception {
        // given
        playerApplicationService.createPlayers();

        // when
        List<Player> allPlayers = playerRepository.findAll();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        for ( Player player: allPlayers ) mockCallToPlayersEndpoint( player );

        // when
        playerApplicationService.obtainBearerTokensForPlayers();

        // then
        mockServer.verify();
        allPlayers = playerRepository.findAll();
        assertEquals( Integer.valueOf( env.getProperty("dungeon.multiPlayer.number") ), allPlayers.size() );
        for ( Player player: allPlayers ) {
            assertNotNull( player.getEmail(), "player email" );
            assertNotNull( player.getName(), "player name"  );
            assert( player.isReadyToPlay() );
        }
    }


    @Test
    public void testDoublyRegisterPlayers() throws Exception {
        // given
        playerApplicationService.createPlayers();

        // when
        List<Player> allPlayers = playerRepository.findAll();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        for ( Player player: allPlayers ) mockCallToPlayersEndpoint( player );

        // when
        playerApplicationService.obtainBearerTokensForPlayers();
        playerApplicationService.obtainBearerTokensForPlayers();

        // then
        mockServer.verify();
        allPlayers = playerRepository.findAll();
        assertEquals( Integer.valueOf( env.getProperty("dungeon.multiPlayer.number") ), allPlayers.size() );
        for ( Player player: allPlayers ) {
            assertNotNull( player.getEmail(), "player email" );
            assertNotNull( player.getName(), "player name"  );
            assert( player.isReadyToPlay() );
        }
    }



    private void mockCallToPlayersEndpoint( Player player ) throws Exception {
        PlayerRegistryDto playerRegistryDto = modelMapper.map( player, PlayerRegistryDto.class );
        PlayerRegistryDto responseDto = playerRegistryDto.clone();
        responseDto.setBearerToken( UUID.randomUUID() );
        mockServer.expect( ExpectedCount.manyTimes(), requestTo( playersEndpointURI ))
                .andExpect( method( POST ))
                .andExpect(content().json(mapper.writeValueAsString( playerRegistryDto )))
                .andRespond( withSuccess( mapper.writeValueAsString( responseDto ), MediaType.APPLICATION_JSON ) );
    }




}

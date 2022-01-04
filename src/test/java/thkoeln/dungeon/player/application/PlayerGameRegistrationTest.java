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
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class PlayerGameRegistrationTest {
    @Value("${GAME_SERVICE}")
    private String gameServiceURIString;
    private URI playersEndpointURI;
    private ModelMapper modelMapper = new ModelMapper();
    private Game game;
    private Player player, playerWithoutToken;

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
        game = Game.newlyCreatedGame( UUID.randomUUID() );
        gameRepository.save( game );
        player = new Player();
        playerWithoutToken = new Player();
        playerRepository.save(player);
        playerRepository.save(playerWithoutToken);
    }


    @Test
    public void testRegisterPlayerWithToken() throws Exception {
        // given
        mockBearerTokenEndpointFor( player );
        playerApplicationService.obtainBearerTokenForOnePlayer( player );
        assert ( player.isReadyToPlay() );

        // when
        mockRegistrationEndpointFor(player);
        playerApplicationService.registerOnePlayerForGame( player, game );

        // then
        List<Player> readyPlayers = playerRepository.findByGameParticipations_Game( game );
        assertEquals( 1, readyPlayers.size() );
        assert( readyPlayers.get( 0 ).isParticipantInGame( game ) );
    }




    private void mockBearerTokenEndpointFor( Player player ) throws Exception {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        PlayerRegistryDto playerRegistryDto = modelMapper.map( player, PlayerRegistryDto.class );
        PlayerRegistryDto responseDto = playerRegistryDto.clone();
        responseDto.setBearerToken( UUID.randomUUID() );
        mockServer.expect( ExpectedCount.manyTimes(), requestTo( playersEndpointURI ))
                .andExpect( method( POST ))
                .andExpect(content().json(mapper.writeValueAsString( playerRegistryDto )))
                .andRespond( withSuccess( mapper.writeValueAsString( responseDto ), MediaType.APPLICATION_JSON ) );
    }



    private void mockRegistrationEndpointFor( Player player ) throws Exception {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        URI uri = new URI( gameServiceURIString + "/games/" + game.getGameId() + "/players/" + player.getBearerToken() );
        mockServer.expect( ExpectedCount.manyTimes(), requestTo( uri ))
                .andExpect( method( PUT ))
                .andRespond( withSuccess() );
    }


}

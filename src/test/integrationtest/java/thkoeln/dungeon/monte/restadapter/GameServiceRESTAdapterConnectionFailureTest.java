package thkoeln.dungeon.monte.restadapter;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.monte.DungeonPlayerConfiguration;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class GameServiceRESTAdapterConnectionFailureTest {
    @Value("${GAME_SERVICE:http://localhost:8080}")
    private String gameServiceURIString;
    private ModelMapper modelMapper = new ModelMapper();
    private PlayerRegistryDto playerRegistryDto = new PlayerRegistryDto();

    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private GameServiceRESTAdapter gameServiceRESTAdapter;


    @Before
    public void setUp() throws Exception {
        playerRegistryDto.setName( "abcd" );
        playerRegistryDto.setEmail( "abcd@def.de");
    }


    @Test
    public void testConnectionException_throws_RESTConnectionFailureException() throws Exception {
        // given
        URI uri = new URI( gameServiceURIString + "/games" );
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect( ExpectedCount.manyTimes(),
                        MockRestRequestMatchers.requestTo( uri ))
                .andExpect( MockRestRequestMatchers.method( HttpMethod.GET ))
                .andRespond( MockRestResponseCreators.withStatus( HttpStatus.NOT_FOUND ) );

        // when/then
        assertThrows( RESTAdapterException.class, () -> {
            gameServiceRESTAdapter.sendGetRequestForAllActiveGames();
        });
    }



    @Test
    public void testCheckForOpenGames_delivers_empty_collection() throws Exception {
        // given: mock with no body ...
        URI uri = new URI( gameServiceURIString + "/games" );
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect( ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo( uri ))
                .andExpect( MockRestRequestMatchers.method( HttpMethod.GET ))
                .andRespond( MockRestResponseCreators.withStatus( HttpStatus.OK ) );

        // when/then
        GameDto[] gameDtos = gameServiceRESTAdapter.sendGetRequestForAllActiveGames();
        assertEquals( 0, gameDtos.length );
    }


    @Test
    public void test_sendPostRequestForPlayerId_with_connection_failure() throws Exception {
        // given
        URI uri = new URI( gameServiceURIString + "/players" );
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect( ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo( uri ))
                .andExpect( MockRestRequestMatchers.method( HttpMethod.POST ))
                .andExpect( MockRestRequestMatchers.content().json(mapper.writeValueAsString( playerRegistryDto )) )
                .andRespond( MockRestResponseCreators.withStatus( HttpStatus.NOT_FOUND ) );

        // when/then
        assertThrows( RESTAdapterException.class, () -> {
            gameServiceRESTAdapter.sendPostRequestForPlayerId( playerRegistryDto.getName(), playerRegistryDto.getEmail() );
        });
    }



    @Test
    public void testRegisterPlayerForGame_throws_RESTConnectionFailureException() throws Exception {
        // given
        UUID gameId = UUID.randomUUID();
        UUID playerToken = UUID.randomUUID();
        URI uri = new URI( gameServiceURIString + "/games/" + gameId + "/players/" + playerToken );
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect( ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo( uri ))
                .andExpect( MockRestRequestMatchers.method( HttpMethod.PUT ))
                .andRespond( MockRestResponseCreators.withStatus( HttpStatus.NOT_FOUND ) );

        // when/then
        assertThrows( RESTAdapterException.class, () -> {
            gameServiceRESTAdapter.sendPutRequestToLetPlayerJoinGame( gameId, playerToken );
        });
    }




    @Test
    public void testRegisterPlayerForGame_when_Error() throws Exception {
        // given
        UUID gameId = UUID.randomUUID();
        UUID playerToken = UUID.randomUUID();
        URI uri = new URI( gameServiceURIString + "/games/" + gameId + "/players/" + playerToken );
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect( ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo( uri ))
                .andExpect( MockRestRequestMatchers.method( HttpMethod.PUT ))
                .andRespond( MockRestResponseCreators.withStatus( HttpStatus.BAD_REQUEST ) );

        // when/then
        assertThrows( RESTAdapterException.class, () -> {
            gameServiceRESTAdapter.sendPutRequestToLetPlayerJoinGame( gameId, playerToken );
        });
    }


    @Test
    public void testRegisterPlayerForGame_throws_HttpClientErrorException_when_BAD_REQUEST() throws Exception {
        // given
        UUID gameId = UUID.randomUUID();
        UUID playerToken = UUID.randomUUID();
        URI uri = new URI( gameServiceURIString + "/games/" + gameId + "/players/" + playerToken );
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect( ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo( uri ))
                .andExpect( MockRestRequestMatchers.method( HttpMethod.PUT ))
                .andRespond( MockRestResponseCreators.withStatus( HttpStatus.BAD_REQUEST ) );

        // when/then
        assertThrows( RESTAdapterException.class, () -> {
            gameServiceRESTAdapter.sendPutRequestToLetPlayerJoinGame( gameId, playerToken );
        });
    }


}

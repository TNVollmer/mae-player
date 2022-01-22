package thkoeln.dungeon.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.restadapter.PlayerRegistryDto;
import thkoeln.dungeon.restadapter.TransactionIdResponseDto;

import java.net.URI;
import java.util.UUID;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

public class AbstractRESTEndpointMockingTest {
    @Value("${GAME_SERVICE:http://localhost:8080}")
    protected String gameServiceURIString;
    protected URI playersEndpointURI;
    protected Game game;
    protected UUID transactionId = UUID.randomUUID();

    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();
    private ModelMapper modelMapper = new ModelMapper();

    protected void setUp() throws Exception {
        playersEndpointURI = new URI( gameServiceURIString + "/players" );
        resetMockServer();
    }

    protected void resetMockServer() {
        mockServer = MockRestServiceServer.bindTo( restTemplate ).ignoreExpectOrder( true ).build();
    }

    protected void mockBearerTokenEndpointFor( Player player ) throws Exception {
        PlayerRegistryDto playerRegistryDto = modelMapper.map(player, PlayerRegistryDto.class);
        PlayerRegistryDto responseDto = playerRegistryDto.clone();
        responseDto.setBearerToken(UUID.randomUUID());
        mockServer.expect( ExpectedCount.manyTimes(), requestTo( playersEndpointURI ) )
                .andExpect( method(POST) )
                .andExpect( content().json(mapper.writeValueAsString( playerRegistryDto )))
                .andRespond( withSuccess(mapper.writeValueAsString(responseDto), MediaType.APPLICATION_JSON) );
    }


    protected void mockRegistrationEndpointFor( Player player, UUID gameId ) throws Exception {
        URI uri = new URI(gameServiceURIString + "/games/" + gameId + "/players/" + player.getBearerToken());
        TransactionIdResponseDto transactionIdResponseDto =
                new TransactionIdResponseDto( transactionId );
        mockServer.expect( ExpectedCount.manyTimes(), requestTo(uri) )
                .andExpect( method(PUT) )
                .andRespond( withSuccess(mapper.writeValueAsString( transactionIdResponseDto ), MediaType.APPLICATION_JSON) );
    }



}

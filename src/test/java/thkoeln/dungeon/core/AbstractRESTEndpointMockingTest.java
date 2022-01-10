package thkoeln.dungeon.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.restadapter.PlayerRegistryDto;

import java.net.URI;
import java.util.UUID;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class AbstractRESTEndpointMockingTest {
    @Value("${GAME_SERVICE}")
    protected String gameServiceURIString;
    protected URI playersEndpointURI;
    protected Game game;

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
        //mockServer = MockRestServiceServer.createServer(restTemplate);
        PlayerRegistryDto playerRegistryDto = modelMapper.map(player, PlayerRegistryDto.class);
        PlayerRegistryDto responseDto = playerRegistryDto.clone();
        responseDto.setBearerToken(UUID.randomUUID());
        mockServer.expect( ExpectedCount.manyTimes(), requestTo(playersEndpointURI) )
                .andExpect(method(POST))
                .andExpect(content().json(mapper.writeValueAsString(playerRegistryDto)))
                .andRespond(withSuccess(mapper.writeValueAsString(responseDto), MediaType.APPLICATION_JSON));
    }


    protected void mockRegistrationEndpointFor( Player player, UUID gameId ) throws Exception {
        //mockServer = MockRestServiceServer.createServer(restTemplate);
        URI uri = new URI(gameServiceURIString + "/games/" + gameId + "/players/" + player.getBearerToken());
        mockServer.expect( ExpectedCount.manyTimes(), requestTo(uri) )
                .andExpect(method(PUT))
                .andRespond(withSuccess());
    }



}

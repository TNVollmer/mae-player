package thkoeln.dungeon.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class AbstractDungeonMockingTest {
    @Value("${GAME_SERVICE:http://localhost:8080}")
    protected String gameServiceURIString;
    protected URI playersGetURI;
    protected URI playersPostURI;
    @Value("${dungeon.playerName}")
    protected String playerName;
    @Value("${dungeon.playerEmail}")
    protected String playerEmail;

    protected URI gamesURI;
    protected Game game;

    @Autowired
    protected RestTemplate restTemplate;
    protected MockRestServiceServer mockServer;
    protected ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    protected ModelMapper modelMapper = new ModelMapper();

    protected final UUID genericEventId = UUID.randomUUID();
    protected final String genericEventIdStr = genericEventId.toString();
    protected final UUID genericTransactionId = UUID.randomUUID();
    protected final String genericTransactionIdStr = genericTransactionId.toString();
    protected final UUID playerId = UUID.randomUUID();
    protected PlayerRegistryDto playerRegistryDto;

    protected void setUp() throws Exception {
        String getExtension = "/players?name=" + playerName + "&mail=" + playerEmail;
        playersGetURI = new URI( gameServiceURIString + getExtension );
        playersPostURI = new URI( gameServiceURIString + "/players" );
        resetMockServer();
        playerRegistryDto = new PlayerRegistryDto();
    }

    protected void resetMockServer() {
        mockServer = MockRestServiceServer.bindTo( restTemplate ).ignoreExpectOrder( true ).build();
    }




    protected void mockPlayerPost() throws Exception {
        PlayerRegistryDto responseDto = playerRegistryDto.clone();
        responseDto.setPlayerId( playerId );
        mockServer.expect( ExpectedCount.once(), requestTo( playersPostURI ) )
                .andExpect( method(POST) )
                .andRespond( withSuccess(objectMapper.writeValueAsString( responseDto ), MediaType.APPLICATION_JSON) );
    }


    protected void mockPlayerGetNotFound() throws Exception {
        mockServer.expect( ExpectedCount.once(), requestTo(playersGetURI) )
                .andExpect( method(GET) )
                .andRespond( withStatus( HttpStatus.NOT_FOUND ) );
    }


    protected void mockPlayerGetFound() throws Exception {
        PlayerRegistryDto responseDto = playerRegistryDto.clone();
        responseDto.setPlayerId( playerId );
        mockServer.expect( ExpectedCount.manyTimes(), requestTo(playersGetURI) )
                .andExpect( method(GET) )
                .andRespond( withSuccess(objectMapper.writeValueAsString(responseDto), MediaType.APPLICATION_JSON) );
    }




    protected void mockRegistrationEndpointFor( Player player, UUID gameId ) throws Exception {
        URI uri = new URI(gameServiceURIString + "/games/" + gameId + "/players/" + player.getPlayerId() );
        TransactionIdResponseDto transactionIdResponseDto =
                new TransactionIdResponseDto(genericTransactionId);
        mockServer.expect( ExpectedCount.max( 999 ), requestTo(uri) )
                .andExpect( method(PUT) )
                .andRespond( withSuccess(objectMapper.writeValueAsString( transactionIdResponseDto ), MediaType.APPLICATION_JSON) );
    }



}

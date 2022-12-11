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
import thkoeln.dungeon.game.domain.GameStatus;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.restadapter.PlayerJoinDto;
import thkoeln.dungeon.restadapter.PlayerRegistryDto;

import java.net.URI;
import java.util.UUID;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
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
    protected UUID openGameId;
    protected GameDto[] gameDtosWithCreatedGame;
    protected GameDto[] gameDtosWithRunningGame;

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
    protected PlayerJoinDto playerJoinDto;

    protected void setUp() throws Exception {
        String getExtension = "/players?name=" + playerName + "&mail=" + playerEmail;
        playersGetURI = new URI( gameServiceURIString + getExtension );
        playersPostURI = new URI( gameServiceURIString + "/players" );
        playerRegistryDto = new PlayerRegistryDto();

        playerJoinDto = new PlayerJoinDto();
        playerJoinDto.setPlayerQueue( "my_new_player_queue" );
        playerJoinDto.setGameExchange( "whatever_game_exchange_may_be" );

        gamesURI = new URI( gameServiceURIString + "/games" );
        createMockGameDtos();
        resetMockServer();
    }

    protected void resetMockServer() {
        mockServer = MockRestServiceServer.bindTo( restTemplate ).ignoreExpectOrder( true ).build();
    }

    protected void createMockGameDtos() {
        gameDtosWithRunningGame = new GameDto[2];
        gameDtosWithRunningGame[0] = new GameDto();
        gameDtosWithRunningGame[0].setGameStatus( GameStatus.FINISHED );
        gameDtosWithRunningGame[0].setGameId( UUID.randomUUID() );
        gameDtosWithRunningGame[0].setCurrentRoundNumber( 100 );
        gameDtosWithRunningGame[1] = new GameDto();
        gameDtosWithRunningGame[1].setGameStatus( GameStatus.RUNNING );
        gameDtosWithRunningGame[1].setGameId( UUID.randomUUID() );
        gameDtosWithRunningGame[1].setCurrentRoundNumber( 34 );

        gameDtosWithCreatedGame = new GameDto[2];
        gameDtosWithCreatedGame[0] = gameDtosWithRunningGame[0];
        gameDtosWithCreatedGame[1] = new GameDto();
        gameDtosWithCreatedGame[1].setGameStatus( GameStatus.CREATED );
        gameDtosWithCreatedGame[1].setGameId( UUID.randomUUID() );
        gameDtosWithCreatedGame[1].setCurrentRoundNumber( 0 );
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
        mockServer.expect( ExpectedCount.once(), requestTo(playersGetURI) )
                .andExpect( method(GET) )
                .andRespond( withSuccess(objectMapper.writeValueAsString(responseDto), MediaType.APPLICATION_JSON) );
    }


    protected void mockGamesGetWithRunning() throws Exception {
        openGameId = null;
        mockServer.expect( ExpectedCount.once(), requestTo( gamesURI ) )
                .andExpect( method(GET) )
                .andRespond( withSuccess(objectMapper.writeValueAsString(gameDtosWithRunningGame), MediaType.APPLICATION_JSON) );
    }



    protected void mockGamesGetWithCreated() throws Exception {
        openGameId = gameDtosWithCreatedGame[1].getGameId();
        mockServer.expect( ExpectedCount.once(), requestTo( gamesURI ) )
                .andExpect( method(GET) )
                .andRespond( withSuccess(objectMapper.writeValueAsString(gameDtosWithCreatedGame), MediaType.APPLICATION_JSON) );
    }


    protected void mockRegistrationEndpointFor( UUID gameId, UUID playerId ) throws Exception {
        URI uri = new URI(gameServiceURIString + "/games/" + gameId + "/players/" + playerId );
        mockServer.expect( ExpectedCount.once(), requestTo(uri) )
                .andExpect( method(PUT) )
                .andRespond( withSuccess(objectMapper.writeValueAsString( playerJoinDto ), MediaType.APPLICATION_JSON) );
    }



}

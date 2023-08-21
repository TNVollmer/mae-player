package thkoeln.dungeon.player.dev;


import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.restadapter.CommandAnswerDto;
import thkoeln.dungeon.player.core.restadapter.GameDto;
import thkoeln.dungeon.player.core.restadapter.PlayerRegistryDto;
import thkoeln.dungeon.player.core.restadapter.RESTAdapterException;
import thkoeln.dungeon.player.dev.dto.CreateGameRequestDto;
import thkoeln.dungeon.player.dev.dto.CreateGameResponseDto;
import thkoeln.dungeon.player.dev.dto.SetRoundDurationRequestDto;


@Component
public class GameAdminClient {
    // It is unbelievable how bad the RestTemplate APIs are
    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger( GameAdminClient.class );
    @Value("${dungeon.game.host}")
    private String gameServiceUrlString;
    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpClient httpClient = HttpClient.newHttpClient();

    @Autowired
    public GameAdminClient( RestTemplate restTemplate ) {
        this.restTemplate = restTemplate;
    }

    public List<GameDto> getAllGames() {
        var result = restTemplate.exchange(gameServiceUrlString + "/games", GET, null,
            new ParameterizedTypeReference<List<GameDto>>() {});
        if (result.getStatusCode() != HttpStatusCode.valueOf(200)) {
            throw new RESTAdapterException("Could not fetch games");
        }
        return result.getBody();
    }

    public CreateGameResponseDto createGame(int maxRounds, int maxPlayers) {
        var body = new CreateGameRequestDto(maxRounds, maxPlayers);
        return restTemplate.postForObject(gameServiceUrlString + "/games", buildJsonBody(body), CreateGameResponseDto.class);
    }

    public void endGame(UUID gameId) {
        var result = restTemplate.exchange(gameServiceUrlString + "/games/" + gameId + "/gameCommands/end", POST,
            (HttpEntity<?>) null, String.class);
        if(result.getStatusCode() != HttpStatusCode.valueOf(201)) throw new RESTAdapterException("Could not end game");
    }

    public void startGame(UUID gameId) {
        var result = restTemplate.exchange(gameServiceUrlString + "/games/" + gameId + "/gameCommands/start", POST,
            (HttpEntity<?>) null, String.class);
        if(result.getStatusCode() != HttpStatusCode.valueOf(201)) throw new RESTAdapterException("Could not start game");
    }

    @SneakyThrows
    public void setRoundDuration(UUID gameId, int duration) {
        var str = objectMapper.writeValueAsString(new SetRoundDurationRequestDto(duration));
        // RestTemplate uses deprecated HttpURLConnection, which doesn't support PATCH.
        // So we fallback to the Java 11 API here.
        var request = HttpRequest.newBuilder(URI.create(gameServiceUrlString + "/games/" + gameId + "/duration"))
            .method("PATCH", HttpRequest.BodyPublishers.ofString(str))
            .header("Content-Type", "application/json")
            .header("Accept",  "application/json")
            .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != 200) throw new RuntimeException("Couldn't change round duration");
    }

    @SneakyThrows
    private <T> HttpEntity<String> buildJsonBody(T obj) {
        String json = objectMapper.writeValueAsString( obj );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );
        headers.setAccept( List.of(MediaType.APPLICATION_JSON) );
        return new HttpEntity<>(json, headers);
    }



    public PlayerRegistryDto sendGetRequestForPlayerId( String playerName, String email ) {
        String urlString = gameServiceUrlString + "/players?name=" + playerName + "&mail=" + email;
        PlayerRegistryDto returnedPlayerRegistryDto;
        try {
            returnedPlayerRegistryDto =
                    restTemplate.execute( urlString, GET, requestCallback(), playerRegistryResponseExtractor() );
        }
        catch ( RestClientResponseException e ) {
            if ( e.getRawStatusCode() == 404 ) {
                // actually, the proper answer would be an empty array, not 404.
                logger.info("No player exists for " + playerName + " and " + email);
                return null;
            }
            else {
                logger.error("Return code " + e.getRawStatusCode() + " for request " + urlString);
                throw new RESTAdapterException( urlString, e );
            }
        }
        catch ( RestClientException e ) {
            logger.error( "Problem with the GET request '" + urlString + "', msg: " + e.getMessage() );
            throw new RESTAdapterException( urlString, e );
        }
        UUID playerId = returnedPlayerRegistryDto.getPlayerId();
        logger.info( "Player is already registered, with playerId: " + playerId );
        return returnedPlayerRegistryDto;
    }



    public PlayerRegistryDto sendPostRequestForPlayerId( String playerName, String email ) {
        PlayerRegistryDto requestDto = new PlayerRegistryDto();
        requestDto.setName( playerName );
        requestDto.setEmail( email );
        String urlString = gameServiceUrlString + "/players";
        PlayerRegistryDto returnedPlayerRegistryDto = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString( requestDto );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType( MediaType.APPLICATION_JSON );
            HttpEntity<String> request = new HttpEntity<String>( json, headers );
            returnedPlayerRegistryDto =
                    restTemplate.postForObject( urlString, request, PlayerRegistryDto.class );
        }
        catch ( JsonProcessingException e ) {
            logger.error( "Unexpected error converting requestDto to JSON: " + requestDto );
            throw new RESTAdapterException( "Unexpected error converting requestDto to JSON: " + requestDto );
        }
        catch ( RestClientException e ) {
            logger.error( "Problem with connection to server, cannot register player! Exception: " + e.getMessage() );
            throw new RESTAdapterException( urlString, e );
        }
        UUID playerId = returnedPlayerRegistryDto.getPlayerId();
        logger.info( "Registered player via REST, got playerId: " + playerId );
        return returnedPlayerRegistryDto;
    }


    public UUID sendPostRequestForCommand( Command command ) {
        logger.info( "Try to send command  " + command );
        String urlString = gameServiceUrlString + "/commands";
        CommandAnswerDto commandAnswerDto;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString( command );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType( MediaType.APPLICATION_JSON );
            HttpEntity<String> request = new HttpEntity<String>( json, headers );
            commandAnswerDto =
                    restTemplate.postForObject( urlString, request, CommandAnswerDto.class );
        }
        catch ( JsonProcessingException e ) {
            logger.error( "Unexpected error converting command to JSON: " + command );
            throw new RESTAdapterException( "Unexpected error converting requestDto to JSON: " + command );
        }
        catch ( RestClientException e ) {
            logger.error( "Problem sending command! Exception: " + e.getMessage() );
            throw new RESTAdapterException( urlString, e );
        }
        UUID transactionId = commandAnswerDto.getTransactionId();
        logger.info( "Successfully sent command, got transaction ID: " + transactionId );
        return transactionId;
    }



    /**
     * Register a specific player for a specific game via call to GameService endpoint.
     * Caveat: GameService returns somewhat weird error codes (non-standard).
     * @param gameId of the game
     * @param playerId of the player
     */
    public void sendPutRequestToLetPlayerJoinGame( UUID gameId, UUID playerId ) {
        String urlString = gameServiceUrlString + "/games/" + gameId + "/players/" + playerId;
        logger.info( "Try to sendPutRequestToLetPlayerJoinGame at: " + urlString );
        try {
            restTemplate.execute( urlString, PUT, requestCallback(), null);
        }
        catch ( RestClientException e ) {
            logger.error( "Exception encountered in sendPutRequestToLetPlayerJoinGame" );
            if ( e.getMessage() != null && e.getMessage().contains( "Player is already participating" ) ) {
                // this is a very specific design flaw in /games/id/players/pid - it throws a 400 if player has
                // already joined.
                return;
            }
            throw new RESTAdapterException( urlString, e );
        }
    }


    /**
     * Adapted from Baeldung example: https://www.baeldung.com/rest-template
     */
    private RequestCallback requestCallback() {
        return clientHttpRequest -> clientHttpRequest.getHeaders().setContentType( MediaType.APPLICATION_JSON );
    }

    private ResponseExtractor<PlayerRegistryDto> playerRegistryResponseExtractor() {
        return response -> objectMapper.readValue( response.getBody(), PlayerRegistryDto.class );
    }
}

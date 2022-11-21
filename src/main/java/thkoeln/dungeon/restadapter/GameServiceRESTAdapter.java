package thkoeln.dungeon.restadapter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import thkoeln.dungeon.DungeonPlayerRuntimeException;
import thkoeln.dungeon.game.domain.GameStatus;

import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import static org.springframework.http.HttpMethod.*;

/**
 * Adapter for sending Game and Player life cycle calls to GameService
 */
@Component
public class GameServiceRESTAdapter {
    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger( GameServiceRESTAdapter.class );
    @Value("${GAME_SERVICE:http://localhost:8080}")
    private String gameServiceUrlString;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public GameServiceRESTAdapter( RestTemplate restTemplate ) {
        this.restTemplate = restTemplate;
    }


    public GameDto[] checkForOpenGames() {
        GameDto[] allGames;
        GameDto[] openGames;
        String urlString = gameServiceUrlString + "/games";
        try {
            allGames = restTemplate.getForObject( urlString, GameDto[].class );
            if ( allGames == null ) {
                logger.error( "Received a null GameDto array from " + urlString );
                return new GameDto[0];
            }
            logger.info( "Got " + allGames.length + " game(s) via REST ...");
            openGames = Arrays.stream(allGames).filter(
                    gameDto -> gameDto.getGameStatus().equals( GameStatus.CREATED )).toArray( GameDto[]::new );
        }
        catch ( RestClientException e ) {
            logger.error( "Error when contacting " + urlString + ", message: " + e.getMessage() );
            return new GameDto[0];
        }
        return openGames;
    }



    public UUID obtainPlayerIdForPlayer( String playerName, String email ) {
        UUID playerId = getRequestForPlayerId( playerName, email );
        if ( playerId != null ) return playerId;

        // player wasn't there already
        PlayerRegistryDto requestDto = new PlayerRegistryDto();
        requestDto.setName( playerName );
        requestDto.setEmail( email );
        playerId = postRequestForPlayerId( requestDto );
        return playerId;
    }


    private UUID getRequestForPlayerId( String playerName, String email ) {
        String urlString = gameServiceUrlString + "/players?name=" + playerName + "&mail=" + email;
        PlayerRegistryDto returnedPlayerRegistryDto = null;
        try {
            returnedPlayerRegistryDto =
                    restTemplate.execute( urlString, GET, requestCallback(), playerRegistryResponseExtractor() );
        }
        catch ( RestClientException e ) {
            logger.error( "Problem with the GET request '" + urlString + "', msg: " + e.getMessage() );
            return null;
        }
        UUID playerId = returnedPlayerRegistryDto.getPlayerId();
        logger.info( "Player is already registered, with playerId: " + playerId );
        return playerId;
    }



    private UUID postRequestForPlayerId( PlayerRegistryDto requestDto ) {
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
            throw new DungeonPlayerRuntimeException(
                    "Unexpected error converting requestDto to JSON: " + requestDto );
        }
        catch ( RestClientException e ) {
            throw new RESTAdapterException( urlString, e.getMessage(), null );
        }
        UUID playerId = returnedPlayerRegistryDto.getPlayerId();
        logger.info( "Registered player via REST, got playerId: " + playerId );
        return playerId;
    }


    /**
     * Register a specific player for a specific game via call to GameService endpoint.
     * Caveat: GameService returns somewhat weird error codes (non-standard).
     * @param gameId of the game
     * @param bearerToken of the player
     * @return transactionId if successful
     */
    public UUID registerPlayerForGame( UUID gameId, UUID bearerToken ) {
        String urlString = gameServiceUrlString + "/games/" + gameId + "/players/" + bearerToken;
        try {
            TransactionIdResponseDto transactionIdResponseDto =
                    restTemplate.execute( urlString, PUT, requestCallback(), registryForGameResponseExtractor() );
            return transactionIdResponseDto.getTransactionId();
        }
        catch ( HttpClientErrorException e ) {
            if ( e.getStatusCode().equals( HttpStatus.NOT_ACCEPTABLE ) ) {
                // this is a business logic problem - so let the application service handle this
                throw new RESTAdapterException( urlString, "Player with bearer token " + bearerToken +
                        " already registered in game with id " + gameId, e.getStatusCode() );
            }
            else if ( e.getStatusCode().equals( HttpStatus.BAD_REQUEST ) ) {
                throw new RESTAdapterException( urlString, "For player with bearer token " + bearerToken +
                        " and game with id " + gameId + " the player registration went wrong; original error msg: "
                        + e.getMessage(), e.getStatusCode() );
            }
            else {
                throw new RESTAdapterException( urlString, e.getMessage(), e.getStatusCode() );
            }
        }
        catch ( RestClientException e ) {
            throw new RESTAdapterException( urlString, e.getMessage(), null );
        }
    }


    /**
     * Adapted from Baeldung example: https://www.baeldung.com/rest-template
     */
    private RequestCallback requestCallback() {
        return clientHttpRequest -> {
            clientHttpRequest.getHeaders().setContentType( MediaType.APPLICATION_JSON );
        };
    }

    private ResponseExtractor<PlayerRegistryDto> playerRegistryResponseExtractor() {
        return response -> {
            return objectMapper.readValue( response.getBody(), PlayerRegistryDto.class );
        };
    }

    private ResponseExtractor<TransactionIdResponseDto> registryForGameResponseExtractor() {
        return response -> {
            return objectMapper.readValue( response.getBody(), TransactionIdResponseDto.class );
        };
    }
}

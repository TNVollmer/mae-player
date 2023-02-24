package thkoeln.dungeon.monte.core.restadapter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;

import java.util.Arrays;
import java.util.UUID;

import static org.springframework.http.HttpMethod.*;

/**
 * Adapter for sending Game and Player life cycle calls to GameService
 */
@Component
public class GameServiceRESTAdapter {
    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger( GameServiceRESTAdapter.class );
    @Value("${dungeon.game.host}")
    private String gameServiceUrlString;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public GameServiceRESTAdapter( RestTemplate restTemplate ) {
        this.restTemplate = restTemplate;
    }


    /**
     * @return An array of all games (as DTOs) that are either CREATED or RUNNING
     */
    public GameDto[] sendGetRequestForAllActiveGames() {
        GameDto[] allGames;
        GameDto[] openGames;
        String urlString = gameServiceUrlString + "/games";
        logger.debug( "GET request from " + gameServiceUrlString );
        try {
            allGames = restTemplate.getForObject( urlString, GameDto[].class );
            if ( allGames == null || allGames.length == 0 ) {
                logger.warn( "Received a null GameDto array from " + urlString );
                return new GameDto[0];
            }
            logger.info( "Got " + allGames.length + " game(s) via REST ...");
            openGames = Arrays.stream(allGames).filter( gameDto -> gameDto.getGameStatus().isActive() )
                    .toArray( GameDto[]::new );
        }
        catch ( RestClientException e ) {
            logger.error( "Error when contacting " + urlString + ", message: " + e.getMessage() );
            throw new RESTAdapterException( urlString, e );
        }
        return openGames;
    }




    public UUID sendGetRequestForPlayerId( String playerName, String email ) {
        String urlString = gameServiceUrlString + "/players?name=" + playerName + "&mail=" + email;
        PlayerRegistryDto returnedPlayerRegistryDto = null;
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
        return playerId;
    }



    public UUID sendPostRequestForPlayerId( String playerName, String email ) {
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
        return playerId;
    }


    public UUID sendPostRequestForCommand( Command command ) {
        logger.info( "Try to send command  " + command );
        String urlString = gameServiceUrlString + "/commands";
        CommandAnswerDto commandAnswerDto = null;
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
     * @return transactionId if successful
     */
    public String sendPutRequestToLetPlayerJoinGame( UUID gameId, UUID playerId ) {
        String urlString = gameServiceUrlString + "/games/" + gameId + "/players/" + playerId;
        logger.info( "Try to sendPutRequestToLetPlayerJoinGame at: " + urlString );
        try {
            PlayerJoinDto playerJoinDto =
                    restTemplate.execute( urlString, PUT, requestCallback(), playerJoinResponseExtractor() );
            return playerJoinDto.getPlayerQueue();
        }
        catch ( RestClientException e ) {
            logger.error( "Exception encountered in sendPutRequestToLetPlayerJoinGame" );
            if ( e.getMessage() != null && e.getMessage().contains( "Player is already participating" ) ) {
                // this is a very specific design flaw in /games/id/players/pid - it throws a 400 if player has
                // already joined. As a workaround, we improvise the queue name ...
                String playerQueue = "player-" + playerId.toString();
                logger.info( "... but player is already participating. So we assume this player queue: " + playerQueue );
                return playerQueue;
            }
            throw new RESTAdapterException( urlString, e );
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

    private ResponseExtractor<PlayerJoinDto> playerJoinResponseExtractor() {
        return response -> {
            return objectMapper.readValue( response.getBody(), PlayerJoinDto.class );
        };
    }
}

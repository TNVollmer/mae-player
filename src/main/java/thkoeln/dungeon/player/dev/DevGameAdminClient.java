package thkoeln.dungeon.player.dev;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import thkoeln.dungeon.player.DungeonPlayerRuntimeException;
import thkoeln.dungeon.player.core.restadapter.GameDto;
import thkoeln.dungeon.player.core.restadapter.PlayerRegistryDto;
import thkoeln.dungeon.player.core.restadapter.RESTAdapterException;
import thkoeln.dungeon.player.dev.dto.CreateGameRequestDto;
import thkoeln.dungeon.player.dev.dto.CreateGameResponseDto;
import thkoeln.dungeon.player.dev.dto.SetRoundDurationRequestDto;
import thkoeln.dungeon.player.game.domain.GameStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;


@Component
@Slf4j
public class DevGameAdminClient {
    public static final String DEV_PREFIX = "--- DEV MODE: ";
    public static final int NUMBER_OF_ROUNDS = 10_000;
    public static final int NUMBER_OF_PLAYERS = 10;
    public static final int ROUND_DURATION = 10_000;

    // It is unbelievable how bad the RestTemplate APIs are
    private RestTemplate restTemplate;

    @Value("${dungeon.game.host}")
    private String gameServiceUrlString;
    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpClient httpClient = HttpClient.newHttpClient();


    @Autowired
    public DevGameAdminClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public void createGameInDevMode() {
        log.info(DEV_PREFIX + "createGameInDevMode() ...");
        var gameDtos = getAllGames();

        // End all existing games
        for (GameDto game : gameDtos) {
            if (game.getGameStatus() == GameStatus.CREATED) {
                startGame(game.getGameId());
            }
            endGame(game.getGameId());
        }
        createGame(NUMBER_OF_ROUNDS, NUMBER_OF_PLAYERS);
        log.info(DEV_PREFIX + "Clean game created.");
    }


    public void startGameInDevMode() {
        log.info(DEV_PREFIX + "startGameInDevMode()");
        var gameDtos = getAllGames();
        if (gameDtos.size() != 1)
            throw new DungeonPlayerRuntimeException(DEV_PREFIX + "Invalid number of games found. That should not happen");
        var game = gameDtos.get(0);
        startGame(game.getGameId());
        setRoundDuration(game.getGameId(), ROUND_DURATION);
        log.info(DEV_PREFIX + "Game started.");
    }


    public void endAllGames() {
        log.info(DEV_PREFIX + "endAllGames()");
        var gameDtos = getAllGames();
        for (GameDto gameDto : gameDtos) {
            if (gameDto.getGameStatus().isActive()) {
                endGame(gameDto.getGameId());
            }
        }
    }


    private List<GameDto> getAllGames() {
        log.info(DEV_PREFIX + "getAllGames()");
        var result = restTemplate.exchange(gameServiceUrlString + "/games", GET, null,
                new ParameterizedTypeReference<List<GameDto>>() {
                });
        if (result.getStatusCode() != HttpStatusCode.valueOf(200)) {
            throw new RESTAdapterException(DEV_PREFIX + "Could not fetch games");
        }
        log.info(DEV_PREFIX + "getAllGames() returned " + result.getBody().size() + " games");
        return result.getBody();
    }

    private CreateGameResponseDto createGame(int maxRounds, int maxPlayers) {
        log.info(DEV_PREFIX + "createGame()");
        var body = new CreateGameRequestDto(maxRounds, maxPlayers);
        var createGameResponseDto = restTemplate.postForObject(
                gameServiceUrlString + "/games", buildJsonBody(body), CreateGameResponseDto.class);
        log.info(DEV_PREFIX + "createGame() returned " + String.valueOf(createGameResponseDto));
        return createGameResponseDto;
    }

    private void endGame(UUID gameId) {
        log.info(DEV_PREFIX + "endGame() " + gameId);
        var result = restTemplate.exchange(gameServiceUrlString + "/games/" + gameId + "/gameCommands/end", POST,
                (HttpEntity<?>) null, String.class);
        if (result.getStatusCode() != HttpStatusCode.valueOf(201))
            throw new RESTAdapterException("Could not end game");
    }

    private void startGame(UUID gameId) {
        log.info(DEV_PREFIX + "startGame()");
        var result = restTemplate.exchange(gameServiceUrlString + "/games/" + gameId + "/gameCommands/start", POST,
                (HttpEntity<?>) null, String.class);
        if (result.getStatusCode() != HttpStatusCode.valueOf(201))
            throw new RESTAdapterException("Could not start game");
    }

    @SneakyThrows
    private void setRoundDuration(UUID gameId, int duration) {
        log.info(DEV_PREFIX + "setRoundDuration()");
        var str = objectMapper.writeValueAsString(new SetRoundDurationRequestDto(duration));
        // RestTemplate uses deprecated HttpURLConnection, which doesn't support PATCH.
        // So we fallback to the Java 11 API here.
        var request = HttpRequest.newBuilder(URI.create(gameServiceUrlString + "/games/" + gameId + "/duration"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(str))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException("Couldn't change round duration");
    }

    @SneakyThrows
    private <T> HttpEntity<String> buildJsonBody(T obj) {
        String json = objectMapper.writeValueAsString(obj);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(json, headers);
    }


    private PlayerRegistryDto sendGetRequestForPlayerId(String playerName, String email) {
        log.info(DEV_PREFIX + "sendGetRequestForPlayerId()");
        String urlString = gameServiceUrlString + "/players?name=" + playerName + "&mail=" + email;
        PlayerRegistryDto returnedPlayerRegistryDto;
        try {
            returnedPlayerRegistryDto =
                    restTemplate.execute(urlString, GET, requestCallback(), playerRegistryResponseExtractor());
        } catch (RestClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                // actually, the proper answer would be an empty array, not 404.
                log.info(DEV_PREFIX + "No player exists for " + playerName + " and " + email);
                return null;
            } else {
                log.error(DEV_PREFIX + "Return code " + e.getRawStatusCode() + " for request " + urlString);
                throw new RESTAdapterException(urlString, e);
            }
        } catch (RestClientException e) {
            log.error(DEV_PREFIX + "Problem with the GET request '" + urlString + "', msg: " + e.getMessage());
            throw new RESTAdapterException(urlString, e);
        }
        UUID playerId = returnedPlayerRegistryDto.getPlayerId();
        log.info(DEV_PREFIX + "Player is already registered, with playerId: " + playerId);
        return returnedPlayerRegistryDto;
    }


    /**
     * Adapted from Baeldung example: https://www.baeldung.com/rest-template
     */
    private RequestCallback requestCallback() {
        return clientHttpRequest -> clientHttpRequest.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    }

    private ResponseExtractor<PlayerRegistryDto> playerRegistryResponseExtractor() {
        return response -> objectMapper.readValue(response.getBody(), PlayerRegistryDto.class);
    }
}

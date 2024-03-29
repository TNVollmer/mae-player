package thkoeln.dungeon.player.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.player.core.events.AbstractEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.GameStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.player.core.restadapter.PlayerRegistryDto;
import thkoeln.dungeon.player.game.domain.Game;
import thkoeln.dungeon.player.game.domain.GameRepository;
import thkoeln.dungeon.player.game.domain.GameStatus;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles( "test" )
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class EventHandlingTests {
    private static final Logger logger = LoggerFactory.getLogger( EventHandlingTests.class );
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RabbitAdmin rabbitAdmin;

    private final String mockHost;

    private final DomainFacade domainFacade;

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public EventHandlingTests(
            @Value("${dungeon.mock.host}") String mockHost,
            DomainFacade domainFacade, GameRepository gameRepository, PlayerRepository playerRepository) {
        this.mockHost = mockHost;
        this.domainFacade = domainFacade;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    @BeforeAll
    public void setupPlayerRegistration() throws JsonProcessingException {
        Player player = Player.ownPlayer("name", "email");
        playerRepository.save(player);

        PlayerRegistryDto requestDto = new PlayerRegistryDto();
        requestDto.setName(player.getName());
        requestDto.setEmail(player.getEmail());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        logger.info("Requested player: " + jsonRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> postResponse = restTemplate.postForEntity(mockHost + "/players",
                new HttpEntity<>(jsonRequest, headers), String.class);

        logger.info("Http response: " + postResponse.getBody());

        PlayerRegistryDto responseDto = objectMapper.readValue(postResponse.getBody(), PlayerRegistryDto.class);

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertEquals(objectMapper.writeValueAsString(requestDto), postResponse.getBody());

        player.assignPlayerId(responseDto.getPlayerId());
        player.setPlayerExchange(responseDto.getPlayerExchange());
        player.setPlayerQueue(responseDto.getPlayerQueue());
        playerRepository.save(player);
    }

    @BeforeEach
    public void cleanup() {
        gameRepository.deleteAll();
    }

    @Test
    public void testGameStatusEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        gameRepository.save(game);

        UUID domainId = game.getId();

        GameStatusEvent gameStatusEvent = new GameStatusEvent();
        gameStatusEvent.setGameId(gameId);
        gameStatusEvent.setGameworldId(UUID.randomUUID());
        gameStatusEvent.setStatus(GameStatus.STARTED);

        this.requestEventFromMockService(gameStatusEvent, "/game/events/GameStatus");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        game = gameRepository.findById(domainId).orElse(null);

        assertNotNull(game);
        assertEquals(GameStatus.STARTED, game.getGameStatus());
        assertTrue(game.getCurrentRoundNumber() >= 1);
    }

    @Test
    public void testRoundStatusEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        RoundStatusEvent roundStatusEvent = new RoundStatusEvent();
        roundStatusEvent.setGameId(gameId);
        roundStatusEvent.setRoundId(UUID.randomUUID());
        roundStatusEvent.setRoundNumber(2);
        roundStatusEvent.setRoundStatus(RoundStatusType.STARTED);

        this.requestEventFromMockService(roundStatusEvent, "/game/events/RoundStatus");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        game = gameRepository.findById(domainId).orElse(null);

        assertNotNull(game);
        assertEquals(RoundStatusType.STARTED, domainFacade.getRoundStatusForCurrentRound(game));
        assertEquals(2, game.getCurrentRoundNumber());
    }

    private void requestEventFromMockService(AbstractEvent event, String url) throws JsonProcessingException {
        String jsonRequest = objectMapper.writeValueAsString(event);
        logger.info("Requested event: " + jsonRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> postResponse = restTemplate.postForEntity(mockHost + url,
                new HttpEntity<>(jsonRequest, headers), String.class);

        logger.info("Http response: " + postResponse.getBody());

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertEquals(objectMapper.writeValueAsString(event), postResponse.getBody());
    }

}

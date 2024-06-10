package thkoeln.dungeon.player.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.events.EventType;
import thkoeln.dungeon.player.core.events.concreteevents.game.GameStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.player.dev.DevGameAdminClient;
import thkoeln.dungeon.player.game.application.GameApplicationService;
import thkoeln.dungeon.player.game.domain.Game;
import thkoeln.dungeon.player.game.domain.GameRepository;
import thkoeln.dungeon.player.game.domain.GameStatus;
import thkoeln.dungeon.player.mock.domain.DomainFacade;
import thkoeln.dungeon.player.mock.dto.*;
import thkoeln.dungeon.player.mock.util.TestHelper;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles( "test" )
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class MapExplorationTestScenarioTests {
    private static final Logger logger = LoggerFactory.getLogger(MapExplorationTestScenarioTests.class);
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private UUID gameId = null;
    private UUID playerId = null;

    private final Map<UUID, Object> moveLog = new HashMap<>();

    private final String gameStatusEventQueue = "only_game_status_events";
    private final String roundStatusEventQueue = "only_round_status_events";

    private final String allEventsQueue = "all_events";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RabbitAdmin rabbitAdmin;

    private final String mockHost;

    private final DomainFacade domainFacade;

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    private final PlayerApplicationService playerApplicationService;
    private final GameApplicationService gameApplicationService;

    private final DevGameAdminClient devGameAdminClient;

    private final TestHelper testHelper;

    @Autowired
    public MapExplorationTestScenarioTests(
            @Value("${dungeon.mock.host}") String mockHost,
            DomainFacade domainFacade, GameRepository gameRepository, PlayerRepository playerRepository, PlayerApplicationService playerApplicationService, GameApplicationService gameApplicationService, DevGameAdminClient devGameAdminClient, TestHelper testHelper) {
        this.mockHost = mockHost;
        this.domainFacade = domainFacade;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.playerApplicationService = playerApplicationService;
        this.gameApplicationService = gameApplicationService;
        this.devGameAdminClient = devGameAdminClient;
        this.testHelper = testHelper;
    }

    @BeforeAll
    @AfterAll
    public void reset() {
        domainFacade.resetDomainFacade().resetEverything();
        logger.info("Resetting application...");
    }

    @Test
    @Order(1)
    public void testPlayerRegistrationAndCustomQueueCreation() {
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
        playerId = player.getPlayerId();
        playerApplicationService.registerPlayer();
        logger.info("Player created and registered");

        this.rabbitAdmin.purgeQueue(player.getPlayerQueue());
        logger.info("Player queue ready");

        testHelper.createNewEventQueueWithEventTypeBinding(roundStatusEventQueue, player.getPlayerExchange(), EventType.ROUND_STATUS);
        testHelper.createNewEventQueueWithEventTypeBinding(gameStatusEventQueue, player.getPlayerExchange(), EventType.GAME_STATUS);

        Queue allQueue = QueueBuilder
                .durable(allEventsQueue)
                .build();

        testHelper.createNewEventQueueWithBinding(
                allQueue,
                BindingBuilder
                        .bind(allQueue)
                        .to((Exchange) ExchangeBuilder
                                .topicExchange(player.getPlayerExchange())
                                .build())
                        .with("#")
                        .noargs()
        );

        logger.info("Custom queues created and ready");

        assertTrue(player.isRegistered());
    }

    @Test
    @Order(2)
    public void testGameCreation() throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        devGameAdminClient.createGameInDevMode();
        Game game = gameApplicationService.queryAndIfNeededFetchRemoteGame();
        gameId = game.getGameId();
        logger.info("Game created");

        assertEquals(GameStatus.CREATED, game.getGameStatus());
    }

    @Test
    @Order(3)
    public void testJoiningGameAsPlayer() throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        boolean playerJoinOpenGame = playerApplicationService.letPlayerJoinOpenGame();
        logger.info("Player joined the game");

        assertTrue(playerJoinOpenGame);
    }

    @Test
    @Order(4)
    public void testTestScenarioConfiguration() throws InterruptedException, JsonProcessingException {
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        MapExplorationTestScenarioSettings settings = new MapExplorationTestScenarioSettings(
                5,
                true,
                5
        );
        ConfigureTestScenarioDto configureDto = new ConfigureTestScenarioDto(TestScenario.MAP_EXPLORATION, settings);

        String jsonRequest = objectMapper.writeValueAsString(configureDto);
        logger.info("Requested test scenario: " + jsonRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> postResponse = restTemplate.postForEntity(
                mockHost + "/games/{gameId}/configureTestScenario"
                        .replace("{gameId}", this.gameId.toString()),
                new HttpEntity<>(jsonRequest, headers), String.class);

        logger.info("Http response: " + postResponse.getBody());
        logger.info("Test scenario is configured");

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
    }

    @Test
    @Order(5)
    public void testStartingGame() throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        devGameAdminClient.startGameInDevMode();
        logger.info("Game started");
    }

    @Test
    @Order(6)
    public void testSendingCommandAsPlayerDuringCommandInputPhase() throws InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        boolean sendOut = false;
        RoundStatusEvent roundStatusEvent = null;

        while (!sendOut) {
            roundStatusEvent = (RoundStatusEvent) testHelper.consumeNextEventOfTypeInEventQueue(roundStatusEventQueue, RoundStatusEvent.class);

            if (roundStatusEvent != null && roundStatusEvent.getRoundNumber() >= 3 && roundStatusEvent.getRoundStatus() == RoundStatusType.STARTED) {
                List<Object> robots = domainFacade.robotDomainFacade().getAllRobots();

                robots.forEach(it -> {
                    var targetPlanet = domainFacade.planetDomainFacade().getRandomNeighbourOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(it));
                    moveLog.put(domainFacade.robotDomainFacade().getRobotIdOfRobot(it), targetPlanet);

                    Command command = Command.createMove(domainFacade.robotDomainFacade().getRobotIdOfRobot(it), domainFacade.planetDomainFacade().getPlanetIdOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(it)), gameId, playerId);
                    try {
                        testHelper.sendCommand(command);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        fail();
                    }
                });

                sendOut = true;
            } else {
                Thread.sleep(Duration.ofSeconds(5).toMillis());
            }
        }
    }

    @Test
    @Order(7)
    public void testGamePlayingOutCorrectly() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, InterruptedException {
        // the first round is skipped during game execution, which means the game begins in round 2
        // why? because that is how the code of the game service is...

        boolean gameEnded = false;
        GameStatusEvent gameStatusEvent = null;

        while (!gameEnded) {
            gameStatusEvent = (GameStatusEvent) testHelper.consumeNextEventOfTypeInEventQueue(gameStatusEventQueue, GameStatusEvent.class);

            if (gameStatusEvent != null && gameStatusEvent.getStatus() == GameStatus.ENDED) {
                gameEnded = true;
            } else {
                Thread.sleep(Duration.ofSeconds(5).toMillis());
            }
        }

        Game game = this.gameRepository.findById(this.gameId).orElseThrow();
        Player player = this.playerRepository.findById(this.playerId).orElseThrow();

        List<Object> robots = domainFacade.robotDomainFacade().getAllRobots();

        assertNotNull(game);
        assertNotNull(player);

        assertEquals(5, robots.size());

        assertEquals(GameStatus.ENDED, game.getGameStatus());
        assertEquals(7, game.getCurrentRoundNumber());

        robots.forEach(it -> {
            logger.info("Landed on planet {} for robot {}", domainFacade.planetDomainFacade().getPlanetIdOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(it)), domainFacade.robotDomainFacade().getRobotIdOfRobot(it));

            assertEquals(domainFacade.planetDomainFacade().getXCoordOfPlanet(moveLog.get(domainFacade.robotDomainFacade().getRobotIdOfRobot(it))), domainFacade.planetDomainFacade().getXCoordOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(it)));
            assertEquals(domainFacade.planetDomainFacade().getYCoordOfPlanet(moveLog.get(domainFacade.robotDomainFacade().getRobotIdOfRobot(it))), domainFacade.planetDomainFacade().getYCoordOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(it)));
        });

        assertTrue(robots.stream().allMatch(it -> domainFacade.robotDomainFacade().getEnergyOfRobot(it) < 20));
    }

    @Test
    @Order(8)
    public void testReceivingAllConsumableEvents() throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        Player player = this.playerRepository.findById(this.playerId).orElseThrow();
        assertNotNull(player);

        Map<String, List<String>> allEventsConsumedThroughoutTheGame = new HashMap<>();

        testHelper.setupEventMap(allEventsConsumedThroughoutTheGame);
        testHelper.consumeAllMessagesInQueue(allEventsQueue, allEventsConsumedThroughoutTheGame);

        logger.info("{}Forwarded events to player throughout game: ", System.lineSeparator());
        allEventsConsumedThroughoutTheGame.forEach((eventType, events) -> {
            logger.info("{}{}Events for: " + eventType + " -> ", System.lineSeparator(), System.lineSeparator());
            events.forEach(it -> {
                logger.info("{}" + it, System.lineSeparator());
            });
        });
        logger.info("Count: " + allEventsConsumedThroughoutTheGame.values().stream().map(List::size).reduce(0, Math::addExact));

        assertEquals(3, allEventsConsumedThroughoutTheGame.get(EventType.GAME_STATUS.getStringValue()).size());
        assertEquals(18, allEventsConsumedThroughoutTheGame.get(EventType.ROUND_STATUS.getStringValue()).size());

        assertEquals(10, allEventsConsumedThroughoutTheGame.get(EventType.PLANET_DISCOVERED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.RESOURCE_MINED.getStringValue()).size());

        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_ATTACKED.getStringValue()).size());
        assertEquals(5, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_MOVED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_REGENERATED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_RESOURCE_MINED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_RESOURCE_REMOVED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_RESTORED_ATTRIBUTES.getStringValue()).size());
        assertEquals(5, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_SPAWNED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_REVEALED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_UPGRADED.getStringValue()).size());

        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.BANK_ACCOUNT_CLEARED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.BANK_INITIALIZED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.BANK_ACCOUNT_TRANSACTION_BOOKED.getStringValue()).size());

        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.TRADABLE_BOUGHT.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.TRADABLE_PRICES.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.TRADABLE_SOLD.getStringValue()).size());

        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ERROR.getStringValue()).size());

        assertEquals(41, allEventsConsumedThroughoutTheGame.values().stream().map(List::size).reduce(0, Math::addExact));
    }

}

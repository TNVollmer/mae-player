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
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles( "test" )
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class FightTestScenarioTests {
    private static final Logger logger = LoggerFactory.getLogger(FightTestScenarioTests.class);
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private UUID gameId = null;
    private UUID playerId = null;

    private UUID friendlyRobotId = null;
    private UUID enemyRobotId = null;

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
    public FightTestScenarioTests(
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
    }

    @Test
    @Order(1)
    public void testPlayerRegistrationAndCustomQueueCreation() {
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
        playerId = player.getPlayerId();
        playerApplicationService.registerPlayer();
        logger.info("Player created and registered");

        this.rabbitAdmin.purgeQueue(player.getPlayerQueue());

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

        assertTrue(playerJoinOpenGame);
    }

    @Test
    @Order(4)
    public void testTestScenarioConfiguration() throws InterruptedException, JsonProcessingException {
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        int playerBalance = 400;
        Player player = this.playerRepository.findById(this.playerId).orElseThrow();
        this.domainFacade.playerDomainFacade().setBalanceForPlayer(player, playerBalance);
        playerRepository.save(player);

        UUID enemyId = UUID.randomUUID();
        String enemyName = "enemy-name";
        String enemyEmail = "enemy-mail";
        int enemyBalance = 400;

        var planet1 = domainFacade.planetDomainFacade().createNewPlanet();
        UUID planet1Id = UUID.randomUUID();
        int planet1XCoord = 0;
        int planet1YCoord = 0;
        int planet1MovementDifficulty = 2;
        domainFacade.planetDomainFacade().setPlanetIdForPlanet(planet1, planet1Id);
        domainFacade.planetDomainFacade().setCoordinatesForPlanet(planet1, planet1XCoord, planet1YCoord);
        domainFacade.planetDomainFacade().setMovementDifficultyForPlanet(planet1, planet1MovementDifficulty);
        domainFacade.planetDomainFacade().setResourceTypeForPlanet(planet1, MineableResourceType.COAL);
        domainFacade.planetDomainFacade().setCurrentResourceAmountForPlanet(planet1, 10000);
        domainFacade.planetDomainFacade().setMaxResourceAmountForPlanet(planet1, 10000);
        domainFacade.planetDomainFacade().savePlanet(planet1);

        var planet2 = domainFacade.planetDomainFacade().createNewPlanet();
        UUID planet2Id = UUID.randomUUID();
        int planet2XCoord = 0;
        int planet2YCoord = 1;
        int planet2MovementDifficulty = 1;
        domainFacade.planetDomainFacade().setPlanetIdForPlanet(planet2, planet2Id);
        domainFacade.planetDomainFacade().setCoordinatesForPlanet(planet2, planet2XCoord, planet2YCoord);
        domainFacade.planetDomainFacade().setMovementDifficultyForPlanet(planet2, planet2MovementDifficulty);
        domainFacade.planetDomainFacade().setResourceTypeForPlanet(planet2, MineableResourceType.COAL);
        domainFacade.planetDomainFacade().setCurrentResourceAmountForPlanet(planet2, 10000);
        domainFacade.planetDomainFacade().setMaxResourceAmountForPlanet(planet2, 10000);
        domainFacade.planetDomainFacade().savePlanet(planet2);

        var planet3 = domainFacade.planetDomainFacade().createNewPlanet();
        UUID planet3Id = UUID.randomUUID();
        int planet3XCoord = 1;
        int planet3YCoord = 0;
        int planet3MovementDifficulty = 1;
        domainFacade.planetDomainFacade().setPlanetIdForPlanet(planet3, planet3Id);
        domainFacade.planetDomainFacade().setCoordinatesForPlanet(planet3, planet3XCoord, planet3YCoord);
        domainFacade.planetDomainFacade().setMovementDifficultyForPlanet(planet3, planet3MovementDifficulty);
        domainFacade.planetDomainFacade().setResourceTypeForPlanet(planet3, MineableResourceType.COAL);
        domainFacade.planetDomainFacade().setCurrentResourceAmountForPlanet(planet3, 10000);
        domainFacade.planetDomainFacade().setMaxResourceAmountForPlanet(planet3, 10000);
        domainFacade.planetDomainFacade().savePlanet(planet3);

        var friendlyRobot = domainFacade.robotDomainFacade().createNewRobot();
        friendlyRobotId = UUID.randomUUID();
        int friendlyRobotHealthLevel = 1;
        int friendlyRobotEnergyLevel = 1;
        int friendlyRobotEnergyRegenLevel = 1;
        int friendlyRobotDamageLevel = 1;
        int friendlyRobotMiningLevel = 1;
        int friendlyRobotMiningSpeedLevel = 2;
        int friendlyRobotHealth = 25;
        int friendlyRobotEnergy = 30;
        domainFacade.robotDomainFacade().setRobotIdForRobot(friendlyRobot, friendlyRobotId);
        domainFacade.robotDomainFacade().setHealthLevelForRobot(friendlyRobot, friendlyRobotHealthLevel);
        domainFacade.robotDomainFacade().setEnergyLevelForRobot(friendlyRobot, friendlyRobotEnergyLevel);
        domainFacade.robotDomainFacade().setEnergyRegenLevelForRobot(friendlyRobot, friendlyRobotEnergyRegenLevel);
        domainFacade.robotDomainFacade().setDamageLevelForRobot(friendlyRobot, friendlyRobotDamageLevel);
        domainFacade.robotDomainFacade().setMiningLevelForRobot(friendlyRobot, friendlyRobotMiningLevel);
        domainFacade.robotDomainFacade().setMiningSpeedLevelForRobot(friendlyRobot, friendlyRobotMiningSpeedLevel);
        domainFacade.robotDomainFacade().setHealthForRobot(friendlyRobot, friendlyRobotHealth);
        domainFacade.robotDomainFacade().setEnergyForRobot(friendlyRobot, friendlyRobotEnergy);
        domainFacade.robotDomainFacade().setPlanetLocationForRobot(friendlyRobot, planet1);
        domainFacade.robotDomainFacade().saveRobot(friendlyRobot);

        var enemyRobot = domainFacade.robotDomainFacade().createNewRobot();
        enemyRobotId = UUID.randomUUID();
        int enemyRobotHealthLevel = 1;
        int enemyRobotEnergyLevel = 1;
        int enemyRobotEnergyRegenLevel = 1;
        int enemyRobotDamageLevel = 2;
        int enemyRobotMiningLevel = 1;
        int enemyRobotMiningSpeedLevel = 2;
        int enemyRobotHealth = 25;
        int enemyRobotEnergy = 30;
        domainFacade.robotDomainFacade().setRobotIdForRobot(enemyRobot, enemyRobotId);
        domainFacade.robotDomainFacade().setHealthLevelForRobot(enemyRobot, enemyRobotHealthLevel);
        domainFacade.robotDomainFacade().setEnergyLevelForRobot(enemyRobot, enemyRobotEnergyLevel);
        domainFacade.robotDomainFacade().setEnergyRegenLevelForRobot(enemyRobot, enemyRobotEnergyRegenLevel);
        domainFacade.robotDomainFacade().setDamageLevelForRobot(enemyRobot, enemyRobotDamageLevel);
        domainFacade.robotDomainFacade().setMiningLevelForRobot(enemyRobot, enemyRobotMiningLevel);
        domainFacade.robotDomainFacade().setMiningSpeedLevelForRobot(enemyRobot, enemyRobotMiningSpeedLevel);
        domainFacade.robotDomainFacade().setHealthForRobot(enemyRobot, enemyRobotHealth);
        domainFacade.robotDomainFacade().setEnergyForRobot(enemyRobot, enemyRobotEnergy);
        domainFacade.robotDomainFacade().setPlanetLocationForRobot(enemyRobot, planet2);
        // domainFacade.saveRobot(enemyRobot);
        // the enemy robot is not saved here, as it is expected to be created and persisted by the player service while
        // handling the robots revealed events

        PlayerConfigDto friendlyPlayerConfigDto = new PlayerConfigDto(player.getPlayerId(), player.getName(), player.getEmail(), (double) playerBalance);
        PlayerConfigDto enemyPlayerConfigDto = new PlayerConfigDto(enemyId, enemyName, enemyEmail, (double) enemyBalance);

        PlanetConfigDto planet1Dto = new PlanetConfigDto(planet1Id, planet1XCoord, planet1YCoord, planet1MovementDifficulty);
        PlanetConfigDto planet2Dto = new PlanetConfigDto(planet2Id, planet2XCoord, planet2YCoord, planet2MovementDifficulty);
        PlanetConfigDto planet3Dto = new PlanetConfigDto(planet3Id, planet3XCoord, planet3YCoord, planet3MovementDifficulty);

        FriendlyRobotDto friendlyRobotDto = new FriendlyRobotDto(
                friendlyRobotId,
                planet1Dto,
                friendlyRobotHealthLevel,
                friendlyRobotEnergyLevel,
                friendlyRobotEnergyRegenLevel,
                friendlyRobotDamageLevel,
                friendlyRobotMiningLevel,
                friendlyRobotMiningSpeedLevel,
                friendlyRobotHealth,
                friendlyRobotEnergy
        );
        EnemyRobotDto enemyRobotDto = new EnemyRobotDto(
                enemyRobotId,
                planet2Dto,
                enemyRobotHealthLevel,
                enemyRobotEnergyLevel,
                enemyRobotEnergyRegenLevel,
                enemyRobotDamageLevel,
                enemyRobotMiningLevel,
                enemyRobotMiningSpeedLevel,
                enemyRobotHealth,
                enemyRobotEnergy,
                List.of(NextOrder.MOVE_NORTH,
                        NextOrder.BUY_ENERGY,
                        NextOrder.BUY_HEALTH,
                        NextOrder.ATTACK,
                        NextOrder.REGENERATE,
                        NextOrder.ATTACK
                )
        );

        FightTestScenarioSettings settings = new FightTestScenarioSettings(
                2,  // 2 * 2 = a grid size of 4, with, in this case, 3 planets and 1 inaccessible empty space
                List.of(
                        planet1Dto,
                        planet2Dto,
                        planet3Dto
                ),
                friendlyPlayerConfigDto,
                enemyPlayerConfigDto,
                List.of(friendlyRobotDto),
                List.of(enemyRobotDto)
        );
        ConfigureTestScenarioDto configureDto = new ConfigureTestScenarioDto(TestScenario.FIGHT, settings);

        String jsonRequest = objectMapper.writeValueAsString(configureDto);
        logger.info("Requested test scenario: " + jsonRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> postResponse = restTemplate.postForEntity(
                mockHost + "/games/{gameId}/configureTestScenario"
                        .replace("{gameId}", this.gameId.toString()),
                new HttpEntity<>(jsonRequest, headers), String.class);

        logger.info("Http response: " + postResponse.getBody());

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
    }

    @Test
    @Order(5)
    public void testStartingGame() throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        devGameAdminClient.startGameInDevMode();
    }

    @Test
    @Order(6)
    public void testSendingCommandAsPlayerDuringCommandInputPhase() throws InterruptedException, JsonProcessingException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        boolean sendOut = false;
        RoundStatusEvent roundStatusEvent = null;

        while (!sendOut) {
            roundStatusEvent = (RoundStatusEvent) testHelper.consumeNextEventOfTypeInEventQueue(roundStatusEventQueue, RoundStatusEvent.class);

            if (roundStatusEvent != null && roundStatusEvent.getRoundNumber() >= 3 && roundStatusEvent.getRoundStatus() == RoundStatusType.STARTED) {
                Command command = Command.createFight(friendlyRobotId, gameId, playerId, enemyRobotId);
                testHelper.sendCommand(command);

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

        var friendlyRobot = this.domainFacade.robotDomainFacade().getRobotByRobotId(friendlyRobotId);
        var enemyRobot = this.domainFacade.robotDomainFacade().getRobotByRobotId(enemyRobotId);

        assertNotNull(game);
        assertNotNull(player);

        assertNotNull(friendlyRobot);
        assertNotNull(enemyRobot);

        assertEquals(GameStatus.ENDED, game.getGameStatus());
        assertEquals(7, game.getCurrentRoundNumber());

        assertEquals(0, domainFacade.planetDomainFacade().getXCoordOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(friendlyRobot)));
        assertEquals(0, domainFacade.planetDomainFacade().getYCoordOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(friendlyRobot)));

        assertEquals(0, domainFacade.planetDomainFacade().getXCoordOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(enemyRobot)));
        assertEquals(0, domainFacade.planetDomainFacade().getYCoordOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(enemyRobot)));

        assertEquals(15, domainFacade.robotDomainFacade().getHealthOfRobot(friendlyRobot));
        assertEquals(28, domainFacade.robotDomainFacade().getEnergyOfRobot(friendlyRobot));

        assertEquals(25, domainFacade.robotDomainFacade().getHealthOfRobot(enemyRobot));
        assertEquals(27, domainFacade.robotDomainFacade().getEnergyOfRobot(enemyRobot));
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

        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.PLANET_DISCOVERED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.RESOURCE_MINED.getStringValue()).size());

        assertEquals(3, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_ATTACKED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_MOVED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_REGENERATED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_RESOURCE_MINED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_RESOURCE_REMOVED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_RESTORED_ATTRIBUTES.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_SPAWNED.getStringValue()).size());
        assertEquals(6, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_REVEALED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ROBOT_UPGRADED.getStringValue()).size());

        assertEquals(1, allEventsConsumedThroughoutTheGame.get(EventType.BANK_ACCOUNT_CLEARED.getStringValue()).size());
        assertEquals(1, allEventsConsumedThroughoutTheGame.get(EventType.BANK_INITIALIZED.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.BANK_ACCOUNT_TRANSACTION_BOOKED.getStringValue()).size());

        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.TRADABLE_BOUGHT.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.TRADABLE_PRICES.getStringValue()).size());
        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.TRADABLE_SOLD.getStringValue()).size());

        assertEquals(0, allEventsConsumedThroughoutTheGame.get(EventType.ERROR.getStringValue()).size());

        assertEquals(32, allEventsConsumedThroughoutTheGame.values().stream().map(List::size).reduce(0, Math::addExact));
    }

}

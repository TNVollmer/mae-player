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
import thkoeln.dungeon.player.core.events.AbstractEvent;
import thkoeln.dungeon.player.core.events.EventHeader;
import thkoeln.dungeon.player.core.events.EventType;
import thkoeln.dungeon.player.core.events.concreteevents.game.GameStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.player.dev.DevGameAdminClient;
import thkoeln.dungeon.player.game.application.GameApplicationService;
import thkoeln.dungeon.player.game.domain.Game;
import thkoeln.dungeon.player.game.domain.GameRepository;
import thkoeln.dungeon.player.game.domain.GameStatus;
import thkoeln.dungeon.player.mock.dto.*;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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
    private UUID enemyId = null;

    private UUID friendlyRobotId = null;
    private UUID enemyRobotId = null;

    private final String gameStatusEventQueue = "only_game_status_events";
    private final String roundStatusEventQueue = "only_round_status_events";

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

    @Autowired
    public FightTestScenarioTests(
            @Value("${dungeon.mock.host}") String mockHost,
            DomainFacade domainFacade, GameRepository gameRepository, PlayerRepository playerRepository, PlayerApplicationService playerApplicationService, GameApplicationService gameApplicationService, DevGameAdminClient devGameAdminClient) {
        this.mockHost = mockHost;
        this.domainFacade = domainFacade;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.playerApplicationService = playerApplicationService;
        this.gameApplicationService = gameApplicationService;
        this.devGameAdminClient = devGameAdminClient;
    }

    @BeforeAll
    @AfterAll
    public void reset() {
        domainFacade.resetEverything();
    }

    @Test
    @Order(1)
    public void testPlayerRegistration() {
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
        playerId = player.getPlayerId();
        playerApplicationService.registerPlayer();
        logger.info("Player created and registered");

        this.rabbitAdmin.purgeQueue(player.getPlayerQueue());

        this.createNewEventQueue(roundStatusEventQueue, player.getPlayerExchange(), EventType.ROUND_STATUS);
        this.createNewEventQueue(gameStatusEventQueue, player.getPlayerExchange(), EventType.GAME_STATUS);

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
        this.domainFacade.setBalanceForPlayer(player, playerBalance);
        playerRepository.save(player);

        enemyId = UUID.randomUUID();
        int enemyBalance = 400;
        Player enemy = Player.ownPlayer("enemy-name", "enemy-mail");
        enemy.assignPlayerId(enemyId);
        this.domainFacade.setBalanceForPlayer(enemy, enemyBalance);
        playerRepository.save(enemy);

        var planet1 = domainFacade.createNewPlanet();
        UUID planet1Id = UUID.randomUUID();
        int planet1XCoord = 0;
        int planet1YCoord = 0;
        int planet1MovementDifficulty = 2;
        domainFacade.setPlanetIdForPlanet(planet1, planet1Id);
        domainFacade.setCoordinatesForPlanet(planet1, planet1XCoord, planet1YCoord);
        domainFacade.setMovementDifficultyForPlanet(planet1, planet1MovementDifficulty);
        domainFacade.setResourceTypeForPlanet(planet1, MineableResourceType.COAL);
        domainFacade.setCurrentResourceAmountForPlanet(planet1, 10000);
        domainFacade.setMaxResourceAmountForPlanet(planet1, 10000);
        domainFacade.savePlanet(planet1);

        var planet2 = domainFacade.createNewPlanet();
        UUID planet2Id = UUID.randomUUID();
        int planet2XCoord = 0;
        int planet2YCoord = 1;
        int planet2MovementDifficulty = 1;
        domainFacade.setPlanetIdForPlanet(planet2, planet2Id);
        domainFacade.setCoordinatesForPlanet(planet2, planet2XCoord, planet2YCoord);
        domainFacade.setMovementDifficultyForPlanet(planet2, planet2MovementDifficulty);
        domainFacade.setResourceTypeForPlanet(planet2, MineableResourceType.COAL);
        domainFacade.setCurrentResourceAmountForPlanet(planet2, 10000);
        domainFacade.setMaxResourceAmountForPlanet(planet2, 10000);
        domainFacade.savePlanet(planet2);

        var planet3 = domainFacade.createNewPlanet();
        UUID planet3Id = UUID.randomUUID();
        int planet3XCoord = 1;
        int planet3YCoord = 0;
        int planet3MovementDifficulty = 1;
        domainFacade.setPlanetIdForPlanet(planet3, planet3Id);
        domainFacade.setCoordinatesForPlanet(planet3, planet3XCoord, planet3YCoord);
        domainFacade.setMovementDifficultyForPlanet(planet3, planet3MovementDifficulty);
        domainFacade.setResourceTypeForPlanet(planet3, MineableResourceType.COAL);
        domainFacade.setCurrentResourceAmountForPlanet(planet3, 10000);
        domainFacade.setMaxResourceAmountForPlanet(planet3, 10000);
        domainFacade.savePlanet(planet3);

        var friendlyRobot = domainFacade.createNewRobot();
        friendlyRobotId = UUID.randomUUID();
        int friendlyRobotHealthLevel = 1;
        int friendlyRobotEnergyLevel = 1;
        int friendlyRobotEnergyRegenLevel = 1;
        int friendlyRobotDamageLevel = 1;
        int friendlyRobotMiningLevel = 1;
        int friendlyRobotMiningSpeedLevel = 2;
        int friendlyRobotHealth = 25;
        int friendlyRobotEnergy = 30;
        domainFacade.setRobotIdForRobot(friendlyRobot, friendlyRobotId);
        domainFacade.setHealthLevelForRobot(friendlyRobot, friendlyRobotHealthLevel);
        domainFacade.setEnergyLevelForRobot(friendlyRobot, friendlyRobotEnergyLevel);
        domainFacade.setEnergyRegenLevelForRobot(friendlyRobot, friendlyRobotEnergyRegenLevel);
        domainFacade.setDamageLevelForRobot(friendlyRobot, friendlyRobotDamageLevel);
        domainFacade.setMiningLevelForRobot(friendlyRobot, friendlyRobotMiningLevel);
        domainFacade.setMiningSpeedLevelForRobot(friendlyRobot, friendlyRobotMiningSpeedLevel);
        domainFacade.setHealthForRobot(friendlyRobot, friendlyRobotHealth);
        domainFacade.setEnergyForRobot(friendlyRobot, friendlyRobotEnergy);
        domainFacade.setPlanetLocationForRobot(friendlyRobot, planet1);
        domainFacade.saveRobot(friendlyRobot);

        var enemyRobot = domainFacade.createNewRobot();
        enemyRobotId = UUID.randomUUID();
        int enemyRobotHealthLevel = 1;
        int enemyRobotEnergyLevel = 1;
        int enemyRobotEnergyRegenLevel = 1;
        int enemyRobotDamageLevel = 2;
        int enemyRobotMiningLevel = 1;
        int enemyRobotMiningSpeedLevel = 2;
        int enemyRobotHealth = 25;
        int enemyRobotEnergy = 30;
        domainFacade.setRobotIdForRobot(enemyRobot, enemyRobotId);
        domainFacade.setHealthLevelForRobot(enemyRobot, enemyRobotHealthLevel);
        domainFacade.setEnergyLevelForRobot(enemyRobot, enemyRobotEnergyLevel);
        domainFacade.setEnergyRegenLevelForRobot(enemyRobot, enemyRobotEnergyRegenLevel);
        domainFacade.setDamageLevelForRobot(enemyRobot, enemyRobotDamageLevel);
        domainFacade.setMiningLevelForRobot(enemyRobot, enemyRobotMiningLevel);
        domainFacade.setMiningSpeedLevelForRobot(enemyRobot, enemyRobotMiningSpeedLevel);
        domainFacade.setHealthForRobot(enemyRobot, enemyRobotHealth);
        domainFacade.setEnergyForRobot(enemyRobot, enemyRobotEnergy);
        domainFacade.setPlanetLocationForRobot(enemyRobot, planet2);
        domainFacade.saveRobot(enemyRobot);

        PlayerConfigDto friendlyPlayerConfigDto = new PlayerConfigDto(player.getPlayerId(), player.getName(), player.getEmail(), (double) playerBalance);
        PlayerConfigDto enemyPlayerConfigDto = new PlayerConfigDto(enemy.getPlayerId(), enemy.getName(), enemy.getEmail(), (double) enemyBalance);

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

        while (!sendOut) {
            RoundStatusEvent roundStatusEvent = (RoundStatusEvent) this.consumeNextEventInEventQueue(roundStatusEventQueue, RoundStatusEvent.class);

            if (roundStatusEvent != null && roundStatusEvent.getRoundNumber() >= 3 && roundStatusEvent.getRoundStatus() == RoundStatusType.STARTED) {
                Command command = Command.createFight(friendlyRobotId, gameId, playerId, enemyRobotId);
                this.sendCommand(command);

                sendOut = true;
            }
            Thread.sleep(Duration.ofSeconds(5).toMillis());
        }
    }



    private void consumeAllMessagesInQueue(Player player, Map<String, List<String>> forwardedEvents) {
        boolean queueStillFull = true;
        while (queueStillFull) {
            Message message = this.rabbitAdmin.getRabbitTemplate().receive(player.getPlayerQueue());
            if (message != null) {
                String eventType = new String(message.getMessageProperties().getHeader("type"), StandardCharsets.UTF_8);
                String eventBody = new String(message.getBody(), StandardCharsets.UTF_8);

                forwardedEvents.get(eventType).add(eventBody);
            } else {
                queueStillFull = false;
            }
        }
    }

    private List<AbstractEvent> consumeAllEventsInEventQueue(String queue, Class<? extends AbstractEvent> eventClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<AbstractEvent> events = new ArrayList<>();
        boolean queueStillFull = true;
        while (queueStillFull) {
            AbstractEvent event = this.consumeNextEventInEventQueue(queue, eventClass);
            if (event != null) {
                events.add(event);
            } else {
                queueStillFull = false;
            }
        }
        return events;
    }

    private AbstractEvent consumeNextEventInEventQueue(String queue, Class<? extends AbstractEvent> eventClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Message message = this.rabbitAdmin.getRabbitTemplate().receive(queue);
        if (message != null) {
            String eventBody = new String(message.getBody(), StandardCharsets.UTF_8);
            AbstractEvent event = eventClass.getDeclaredConstructor().newInstance();

            event.setEventHeader(null);
            event.fillWithPayload(eventBody);

            return event;
        } else {
            return null;
        }
    }

    private void sendCommand(Command command) throws JsonProcessingException {
        String jsonRequest = objectMapper.writeValueAsString(command);
        logger.info("Requested command: " + jsonRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> postResponse = restTemplate.postForEntity(
                mockHost + "/commands",
                new HttpEntity<>(jsonRequest, headers), String.class
        );

        logger.info("Http response: " + postResponse.getBody());
    }

    private void createNewEventQueue(String newEventQueueName, String playerExchange, EventType eventType) {
        Queue newEventQueue = QueueBuilder
                .durable(newEventQueueName)
                .build();

        Binding newEventTypeBinding = BindingBuilder
                .bind(newEventQueue)
                .to((Exchange) ExchangeBuilder
                        .topicExchange(playerExchange)
                        .build()
                )
                .with("IGNORED-NEW-EVENT-TYPE-BINDING")
                .and(Map.of("x-match", "all",
                        EventHeader.getTYPE_KEY(), eventType.getStringValue())
                );

        this.rabbitAdmin.declareQueue(newEventQueue);
        this.rabbitAdmin.declareBinding(newEventTypeBinding);

        this.rabbitAdmin.purgeQueue(newEventQueue.getName());
    }

}

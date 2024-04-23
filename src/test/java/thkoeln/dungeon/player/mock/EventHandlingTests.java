package thkoeln.dungeon.player.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.player.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.TradeableItem;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.TradeableType;
import thkoeln.dungeon.player.core.events.AbstractEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.GameStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetNeighboursDto;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetResourceDto;
import thkoeln.dungeon.player.core.events.concreteevents.planet.ResourceMinedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.change.RobotRegeneratedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.change.RobotRestoredAttributesEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.change.RobotUpgradedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.fight.RobotAttackedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.fight.RobotFightResultDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.mine.RobotResourceInventoryDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.mine.RobotResourceMinedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.mine.RobotResourceRemovedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.move.RobotMovePlanetDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.move.RobotMovedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotRevealedDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotsRevealedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.*;
import thkoeln.dungeon.player.core.events.concreteevents.trading.*;
import thkoeln.dungeon.player.core.restadapter.PlayerRegistryDto;
import thkoeln.dungeon.player.game.domain.Game;
import thkoeln.dungeon.player.game.domain.GameRepository;
import thkoeln.dungeon.player.game.domain.GameStatus;
import thkoeln.dungeon.player.mock.domain.DomainFacade;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotRevealedLevelDto;

import java.time.Duration;
import java.util.List;
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
    public void setupCleanStateAndPerformPlayerRegistration() throws JsonProcessingException {
        domainFacade.resetDomainFacade().resetEverything();

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
    public void cleanUpEverythingExceptPlayer() {
        domainFacade.resetDomainFacade().resetEverythingExceptPlayer();
    }

    @AfterAll
    public void cleanupEverything() {
        domainFacade.resetDomainFacade().resetEverything();
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
        assertEquals(RoundStatusType.STARTED, domainFacade.gameDomainFacade().getRoundStatusForCurrentRound(game));
        assertEquals(2, game.getCurrentRoundNumber());
    }

    @Test
    public void testPlanetDiscoveredEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        PlanetDiscoveredEvent planetDiscoveredEvent = new PlanetDiscoveredEvent();
        UUID planetId = UUID.randomUUID();
        planetDiscoveredEvent.setPlanetId(planetId);
        planetDiscoveredEvent.setMovementDifficulty(3);

        PlanetNeighboursDto planetNeighboursDto1 = new PlanetNeighboursDto();
        UUID northernNeighbourId = UUID.randomUUID();
        planetNeighboursDto1.setId(northernNeighbourId);
        planetNeighboursDto1.setDirection(CompassDirection.NORTH);

        PlanetNeighboursDto planetNeighboursDto2 = new PlanetNeighboursDto();
        UUID southernNeighbourId = UUID.randomUUID();
        planetNeighboursDto2.setId(southernNeighbourId);
        planetNeighboursDto2.setDirection(CompassDirection.SOUTH);

        PlanetNeighboursDto[] planetNeighboursDtos = { planetNeighboursDto1, planetNeighboursDto2 };
        planetDiscoveredEvent.setNeighbours(planetNeighboursDtos);

        PlanetResourceDto planetResourceDto = new PlanetResourceDto();
        planetResourceDto.setResourceType(MineableResourceType.COAL);
        planetResourceDto.setCurrentAmount(10000);
        planetResourceDto.setMaxAmount(10000);

        planetDiscoveredEvent.setResource(planetResourceDto);

        this.requestEventFromMockService(planetDiscoveredEvent, "/map/events/PlanetDiscovered");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        var planet = domainFacade.planetDomainFacade().getPlanetByPlanetId(planetId);
        var northernNeighbour = domainFacade.planetDomainFacade().getPlanetByPlanetId(northernNeighbourId);
        var southernNeighbour = domainFacade.planetDomainFacade().getPlanetByPlanetId(southernNeighbourId);

        assertNotNull(planet);
        assertNotNull(northernNeighbour);
        assertNotNull(southernNeighbour);

        var neighbours = domainFacade.planetDomainFacade().getNeighboursOfPlanet(planet);

        assertEquals(northernNeighbour, neighbours.get(CompassDirection.NORTH));
        assertEquals(southernNeighbour, neighbours.get(CompassDirection.SOUTH));
        assertEquals(2, neighbours.size());

        assertEquals(3, domainFacade.planetDomainFacade().getMovementDifficultyForPlanet(planet));
        assertNotNull(domainFacade.planetDomainFacade().getResourceTypeOfPlanet(planet));
        assertEquals(MineableResourceType.COAL, domainFacade.planetDomainFacade().getResourceTypeOfPlanet(planet));
        assertNotNull(domainFacade.planetDomainFacade().getCurrentResourceAmountOfPlanet(planet));
        assertEquals(10000, domainFacade.planetDomainFacade().getCurrentResourceAmountOfPlanet(planet));
        assertNotNull(domainFacade.planetDomainFacade().getMaxResourceAmountOfPlanet(planet));
        assertEquals(10000, domainFacade.planetDomainFacade().getMaxResourceAmountOfPlanet(planet));
    }

    @Test
    public void testResourceMinedEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        var planet = domainFacade.planetDomainFacade().createNewPlanet();
        UUID planetId = UUID.randomUUID();
        domainFacade.planetDomainFacade().setPlanetIdForPlanet(planet, planetId);
        domainFacade.planetDomainFacade().setResourceTypeForPlanet(planet, MineableResourceType.COAL);
        domainFacade.planetDomainFacade().setCurrentResourceAmountForPlanet(planet, 10000);
        domainFacade.planetDomainFacade().setMaxResourceAmountForPlanet(planet, 10000);
        domainFacade.planetDomainFacade().savePlanet(planet);

        ResourceMinedEvent resourceMinedEvent = new ResourceMinedEvent();
        resourceMinedEvent.setPlanetId(planetId);
        resourceMinedEvent.setMinedAmount(5);

        PlanetResourceDto planetResourceDto = new PlanetResourceDto();
        planetResourceDto.setResourceType(MineableResourceType.COAL);
        planetResourceDto.setCurrentAmount(9995);
        planetResourceDto.setMaxAmount(10000);
        resourceMinedEvent.setResource(planetResourceDto);

        this.requestEventFromMockService(resourceMinedEvent, "/map/events/ResourceMined");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        planet = domainFacade.planetDomainFacade().getPlanetByPlanetId(resourceMinedEvent.getPlanetId());

        assertNotNull(planet);
        assertEquals(resourceMinedEvent.getResource().getResourceType(), domainFacade.planetDomainFacade().getResourceTypeOfPlanet(planet));
        assertEquals(resourceMinedEvent.getResource().getCurrentAmount(), domainFacade.planetDomainFacade().getCurrentResourceAmountOfPlanet(planet));
        assertEquals(resourceMinedEvent.getResource().getMaxAmount(), domainFacade.planetDomainFacade().getMaxResourceAmountOfPlanet(planet));
    }

    @Test
    public void testRobotRegeneratedEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        var robot = domainFacade.robotDomainFacade().createNewRobot();
        UUID robotId = UUID.randomUUID();
        domainFacade.robotDomainFacade().setRobotIdForRobot(robot, robotId);
        domainFacade.robotDomainFacade().setEnergyForRobot(robot, 15);
        domainFacade.robotDomainFacade().saveRobot(robot);

        RobotRegeneratedEvent robotRegeneratedEvent = new RobotRegeneratedEvent();
        robotRegeneratedEvent.setRobotId(robotId);
        robotRegeneratedEvent.setAvailableEnergy(20);

        this.requestEventFromMockService(robotRegeneratedEvent, "/robot/events/RobotRegenerated");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        robot = domainFacade.robotDomainFacade().getRobotByRobotId(robotId);

        assertNotNull(robot);
        assertEquals(robotRegeneratedEvent.getAvailableEnergy(), domainFacade.robotDomainFacade().getEnergyOfRobot(robot));
    }

    @Test
    public void testRobotRestoredAttributesEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        var robot = domainFacade.robotDomainFacade().createNewRobot();
        UUID robotId = UUID.randomUUID();
        domainFacade.robotDomainFacade().setRobotIdForRobot(robot, robotId);
        domainFacade.robotDomainFacade().setHealthForRobot(robot, 7);
        domainFacade.robotDomainFacade().setEnergyForRobot(robot, 15);
        domainFacade.robotDomainFacade().saveRobot(robot);

        RobotRestoredAttributesEvent robotRestoredAttributesEvent = new RobotRestoredAttributesEvent();
        robotRestoredAttributesEvent.setRobotId(UUID.randomUUID());
        robotRestoredAttributesEvent.setRestorationType("HEALTH");
        robotRestoredAttributesEvent.setAvailableHealth(10);
        robotRestoredAttributesEvent.setAvailableEnergy(15);

        this.requestEventFromMockService(robotRestoredAttributesEvent, "/robot/events/RobotRestoredAttributes");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        robot = domainFacade.robotDomainFacade().getRobotByRobotId(robotId);

        assertNotNull(robot);
        assertEquals(robotRestoredAttributesEvent.getAvailableHealth(), domainFacade.robotDomainFacade().getHealthOfRobot(robot));
    }

    @Test
    public void testRobotUpgradedEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        var robot = domainFacade.robotDomainFacade().createNewRobot();
        UUID robotId = UUID.randomUUID();
        domainFacade.robotDomainFacade().setRobotIdForRobot(robot, robotId);
        domainFacade.robotDomainFacade().setHealthForRobot(robot, 10);
        domainFacade.robotDomainFacade().setEnergyForRobot(robot, 20);
        domainFacade.robotDomainFacade().setHealthLevelForRobot(robot, 0);
        domainFacade.robotDomainFacade().saveRobot(robot);

        RobotUpgradedEvent robotUpgradedEvent = new RobotUpgradedEvent();
        robotUpgradedEvent.setRobotId(UUID.randomUUID());
        robotUpgradedEvent.setLevel(1);
        robotUpgradedEvent.setUpgrade("HEALTH");

        RobotDto robotDto = new RobotDto();
        robotDto.setId(robotId);
        robotDto.setPlayer(UUID.randomUUID());
        robotDto.setHealth(10);
        robotDto.setEnergy(20);
        robotDto.setHealthLevel(0);
        robotDto.setMiningSpeed(2);
        robotDto.setMaxHealth(10);
        robotDto.setMaxEnergy(20);
        robotDto.setEnergyRegen(3);
        robotDto.setAttackDamage(1);

        RobotPlanetDto robotPlanetDto = new RobotPlanetDto();
        robotPlanetDto.setPlanetId(UUID.randomUUID());
        robotPlanetDto.setResourceType("COAL");
        robotPlanetDto.setMovementDifficulty(2);
        robotPlanetDto.setGameWorldId(UUID.randomUUID());
        robotDto.setPlanet(robotPlanetDto);

        RobotInventoryDto robotInventoryDto = new RobotInventoryDto();
        robotInventoryDto.setFull(false);
        robotInventoryDto.setStorageLevel(0);
        robotInventoryDto.setUsedStorage(0);
        robotInventoryDto.setMaxStorage(10);

        RobotInventoryResourcesDto robotInventoryResourcesDto = new RobotInventoryResourcesDto();
        robotInventoryDto.setResources(robotInventoryResourcesDto);
        robotDto.setInventory(robotInventoryDto);

        robotUpgradedEvent.setRobotDto(robotDto);

        this.requestEventFromMockService(robotUpgradedEvent, "/robot/events/RobotUpgraded");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        robot = domainFacade.robotDomainFacade().getRobotByRobotId(robotId);

        assertNotNull(robot);
        assertEquals(robotUpgradedEvent.getLevel(), domainFacade.robotDomainFacade().getHealthLevelOfRobot(robot));
    }

    @Test
    public void testRobotAttackedEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        var attacker = domainFacade.robotDomainFacade().createNewRobot();
        UUID attackedId = UUID.randomUUID();
        domainFacade.robotDomainFacade().setRobotIdForRobot(attacker, attackedId);
        domainFacade.robotDomainFacade().setHealthForRobot(attacker, 10);
        domainFacade.robotDomainFacade().setEnergyForRobot(attacker, 20);
        domainFacade.robotDomainFacade().saveRobot(attacker);

        var target = domainFacade.robotDomainFacade().createNewRobot();
        UUID targetId = UUID.randomUUID();
        domainFacade.robotDomainFacade().setRobotIdForRobot(target, targetId);
        domainFacade.robotDomainFacade().setHealthForRobot(target, 10);
        domainFacade.robotDomainFacade().setEnergyForRobot(target, 20);
        domainFacade.robotDomainFacade().saveRobot(target);

        RobotAttackedEvent robotAttackedEvent = new RobotAttackedEvent();

        RobotFightResultDto attackerDto = new RobotFightResultDto();
        attackerDto.setRobotId(attackedId);
        attackerDto.setAlive(true);
        attackerDto.setAvailableHealth(10);
        attackerDto.setAvailableEnergy(18);

        RobotFightResultDto targetDto = new RobotFightResultDto();
        targetDto.setRobotId(targetId);
        targetDto.setAlive(true);
        targetDto.setAvailableHealth(9);
        targetDto.setAvailableEnergy(20);

        robotAttackedEvent.setAttacker(attackerDto);
        robotAttackedEvent.setTarget(targetDto);

        this.requestEventFromMockService(robotAttackedEvent, "/robot/events/RobotAttacked");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        attacker = domainFacade.robotDomainFacade().getRobotByRobotId(attackedId);
        target = domainFacade.robotDomainFacade().getRobotByRobotId(targetId);

        assertNotNull(attacker);
        assertNotNull(target);

        assertEquals(10, domainFacade.robotDomainFacade().getHealthOfRobot(attacker));
        assertEquals(18, domainFacade.robotDomainFacade().getEnergyOfRobot(attacker));
        assertTrue(domainFacade.robotDomainFacade().getAliveStatusOfRobot(attacker));

        assertEquals(9, domainFacade.robotDomainFacade().getHealthOfRobot(target));
        assertEquals(20, domainFacade.robotDomainFacade().getEnergyOfRobot(target));
        assertTrue(domainFacade.robotDomainFacade().getAliveStatusOfRobot(target));
    }

    @Test
    public void testRobotResourceMinedEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        var robot = domainFacade.robotDomainFacade().createNewRobot();
        UUID robotId = UUID.randomUUID();
        domainFacade.robotDomainFacade().setRobotIdForRobot(robot, robotId);
        domainFacade.robotDomainFacade().setHealthForRobot(robot, 10);
        domainFacade.robotDomainFacade().setEnergyForRobot(robot, 20);
        domainFacade.robotDomainFacade().setCoalAmountForRobot(robot, 2);
        domainFacade.robotDomainFacade().saveRobot(robot);

        RobotResourceMinedEvent robotResourceMinedEvent = new RobotResourceMinedEvent();
        robotResourceMinedEvent.setRobotId(robotId);
        robotResourceMinedEvent.setMinedResource("COAL");
        robotResourceMinedEvent.setMinedAmount(2);

        RobotResourceInventoryDto robotResourceInventoryDto = new RobotResourceInventoryDto();
        robotResourceInventoryDto.setCoal(4);

        robotResourceMinedEvent.setResourceInventory(robotResourceInventoryDto);

        this.requestEventFromMockService(robotResourceMinedEvent, "/robot/events/RobotResourceMined");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        robot = domainFacade.robotDomainFacade().getRobotByRobotId(robotId);

        assertNotNull(robot);
        assertEquals(4, domainFacade.robotDomainFacade().getCoalAmountOfRobot(robot));
    }

    @Test
    public void testRobotResourceRemovedEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        var robot = domainFacade.robotDomainFacade().createNewRobot();
        UUID robotId = UUID.randomUUID();
        domainFacade.robotDomainFacade().setRobotIdForRobot(robot, robotId);
        domainFacade.robotDomainFacade().setHealthForRobot(robot, 10);
        domainFacade.robotDomainFacade().setEnergyForRobot(robot, 20);
        domainFacade.robotDomainFacade().setCoalAmountForRobot(robot, 2);
        domainFacade.robotDomainFacade().saveRobot(robot);

        RobotResourceRemovedEvent robotResourceRemovedEvent = new RobotResourceRemovedEvent();
        robotResourceRemovedEvent.setRobotId(robotId);
        robotResourceRemovedEvent.setRemovedResource("COAL");
        robotResourceRemovedEvent.setRemovedAmount(2);

        RobotResourceInventoryDto robotResourceInventoryDto = new RobotResourceInventoryDto();
        robotResourceInventoryDto.setCoal(0);

        robotResourceRemovedEvent.setResourceInventory(robotResourceInventoryDto);

        this.requestEventFromMockService(robotResourceRemovedEvent, "/robot/events/RobotResourceRemoved");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        robot = domainFacade.robotDomainFacade().getRobotByRobotId(robotId);

        assertNotNull(robot);
        assertEquals(0, domainFacade.robotDomainFacade().getCoalAmountOfRobot(robot));
    }

    @Test
    public void testRobotMovedEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        var planet = domainFacade.planetDomainFacade().createNewPlanet();
        UUID planetId = UUID.randomUUID();
        domainFacade.planetDomainFacade().setPlanetIdForPlanet(planet, planetId);
        domainFacade.planetDomainFacade().setResourceTypeForPlanet(planet, MineableResourceType.COAL);
        domainFacade.planetDomainFacade().setCurrentResourceAmountForPlanet(planet, 10000);
        domainFacade.planetDomainFacade().setMaxResourceAmountForPlanet(planet, 10000);
        domainFacade.planetDomainFacade().savePlanet(planet);

        var robot = domainFacade.robotDomainFacade().createNewRobot();
        UUID robotId = UUID.randomUUID();
        domainFacade.robotDomainFacade().setRobotIdForRobot(robot, robotId);
        domainFacade.robotDomainFacade().setHealthForRobot(robot, 10);
        domainFacade.robotDomainFacade().setEnergyForRobot(robot, 20);
        domainFacade.robotDomainFacade().setPlanetLocationForRobot(robot, planet);
        domainFacade.robotDomainFacade().saveRobot(robot);

        RobotMovedEvent robotMovedEvent = new RobotMovedEvent();
        robotMovedEvent.setRobotId(robotId);
        robotMovedEvent.setRemainingEnergy(18);

        RobotMovePlanetDto fromPlanetDto = new RobotMovePlanetDto();
        fromPlanetDto.setId(planetId);
        fromPlanetDto.setMovementDifficulty(2);

        RobotMovePlanetDto toPlanetDto = new RobotMovePlanetDto();
        UUID targetId = UUID.randomUUID();
        toPlanetDto.setId(targetId);
        toPlanetDto.setMovementDifficulty(3);

        robotMovedEvent.setFromPlanet(fromPlanetDto);
        robotMovedEvent.setFromPlanet(toPlanetDto);

        this.requestEventFromMockService(robotMovedEvent, "/robot/events/RobotMoved");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        robot = domainFacade.robotDomainFacade().getRobotByRobotId(robotId);

        assertNotNull(robot);
        assertEquals(targetId, domainFacade.planetDomainFacade().getPlanetIdOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(robot)));
        assertEquals(18, domainFacade.robotDomainFacade().getEnergyOfRobot(robot));
    }

    @Test
    public void testRobotsRevealedEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        var robot1 = domainFacade.robotDomainFacade().createNewRobot();
        UUID robot1Id = UUID.randomUUID();
        UUID planet1Id = UUID.randomUUID();
        domainFacade.robotDomainFacade().setRobotIdForRobot(robot1, robot1Id);
        domainFacade.robotDomainFacade().setHealthForRobot(robot1, 10);
        domainFacade.robotDomainFacade().setEnergyForRobot(robot1, 20);
        domainFacade.robotDomainFacade().saveRobot(robot1);

        var robot2 = domainFacade.robotDomainFacade().createNewRobot();
        UUID robot2Id = UUID.randomUUID();
        UUID planet2Id = UUID.randomUUID();
        domainFacade.robotDomainFacade().setRobotIdForRobot(robot2, robot2Id);
        domainFacade.robotDomainFacade().setHealthForRobot(robot2, 10);
        domainFacade.robotDomainFacade().setEnergyForRobot(robot2, 15);
        domainFacade.robotDomainFacade().saveRobot(robot2);

        var robot3 = domainFacade.robotDomainFacade().createNewRobot();
        UUID robot3Id = UUID.randomUUID();
        UUID planet3Id = UUID.randomUUID();
        domainFacade.robotDomainFacade().setRobotIdForRobot(robot3, robot3Id);
        domainFacade.robotDomainFacade().setHealthForRobot(robot3, 8);
        domainFacade.robotDomainFacade().setEnergyForRobot(robot3, 13);
        domainFacade.robotDomainFacade().saveRobot(robot3);

        RobotsRevealedEvent robotsRevealedEvent = new RobotsRevealedEvent();

        RobotRevealedDto robot1Dto = new RobotRevealedDto();
        robot1Dto.setRobotId(robot1Id);
        robot1Dto.setHealth(10);
        robot1Dto.setEnergy(20);
        robot1Dto.setPlayerNotion(robot1Id.toString().substring(0, 8));
        robot1Dto.setPlanetId(planet1Id);
        robot1Dto.setLevels(RobotRevealedLevelDto.defaults());

        RobotRevealedDto robot2Dto = new RobotRevealedDto();
        robot2Dto.setRobotId(robot1Id);
        robot2Dto.setHealth(10);
        robot2Dto.setEnergy(15);
        robot2Dto.setPlayerNotion(robot2Id.toString().substring(0, 8));
        robot2Dto.setPlanetId(planet2Id);
        robot2Dto.setLevels(RobotRevealedLevelDto.defaults());

        RobotRevealedDto robot3Dto = new RobotRevealedDto();
        robot3Dto.setRobotId(robot1Id);
        robot3Dto.setHealth(8);
        robot3Dto.setEnergy(13);
        robot3Dto.setPlayerNotion(robot3Id.toString().substring(0, 8));
        robot3Dto.setPlanetId(planet3Id);
        robot3Dto.setLevels(RobotRevealedLevelDto.defaults());

        RobotRevealedDto[] robots = { robot1Dto, robot2Dto, robot3Dto };

        robotsRevealedEvent.setRobots(robots);

        this.requestEventFromMockService(robotsRevealedEvent, "/robot/events/RobotsRevealed");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        robot1 = domainFacade.robotDomainFacade().getRobotByRobotId(robot1Id);
        robot2 = domainFacade.robotDomainFacade().getRobotByRobotId(robot2Id);
        robot3 = domainFacade.robotDomainFacade().getRobotByRobotId(robot3Id);

        assertNotNull(robot1);
        assertNotNull(robot2);
        assertNotNull(robot3);

        assertEquals(planet1Id, domainFacade.planetDomainFacade().getPlanetIdOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(robot1)));
        assertEquals(planet2Id, domainFacade.planetDomainFacade().getPlanetIdOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(robot2)));
        assertEquals(planet3Id, domainFacade.planetDomainFacade().getPlanetIdOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(robot3)));
    }

    @Test
    public void testRobotSpawnedEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        UUID robotId = UUID.randomUUID();

        RobotSpawnedEvent robotSpawnedEvent = new RobotSpawnedEvent();
        robotSpawnedEvent.setPlayerId(playerRepository.findAll().get(0).getPlayerId());

        RobotDto robotDto = new RobotDto();
        robotDto.setId(robotId);
        robotDto.setPlayer(UUID.randomUUID());
        robotDto.setHealth(10);
        robotDto.setEnergy(20);
        robotDto.setMiningSpeed(2);
        robotDto.setMaxHealth(10);
        robotDto.setMaxEnergy(20);
        robotDto.setEnergyRegen(3);
        robotDto.setAttackDamage(1);

        RobotPlanetDto robotPlanetDto = new RobotPlanetDto();
        UUID planetId = UUID.randomUUID();
        robotPlanetDto.setPlanetId(planetId);
        robotPlanetDto.setResourceType("COAL");
        robotPlanetDto.setMovementDifficulty(2);
        robotPlanetDto.setGameWorldId(UUID.randomUUID());
        robotDto.setPlanet(robotPlanetDto);

        RobotInventoryDto robotInventoryDto = new RobotInventoryDto();
        robotInventoryDto.setFull(false);
        robotInventoryDto.setStorageLevel(0);
        robotInventoryDto.setUsedStorage(0);
        robotInventoryDto.setMaxStorage(10);

        RobotInventoryResourcesDto robotInventoryResourcesDto = new RobotInventoryResourcesDto();
        robotInventoryDto.setResources(robotInventoryResourcesDto);
        robotDto.setInventory(robotInventoryDto);

        robotSpawnedEvent.setRobotDto(robotDto);

        this.requestEventFromMockService(robotSpawnedEvent, "/robot/events/RobotSpawned");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        var robot = domainFacade.robotDomainFacade().getRobotByRobotId(robotId);

        assertNotNull(robot);
        assertEquals(10, domainFacade.robotDomainFacade().getHealthLevelOfRobot(robot));
        assertEquals(20, domainFacade.robotDomainFacade().getEnergyOfRobot(robot));
        assertEquals(0, domainFacade.robotDomainFacade().getHealthLevelOfRobot(robot));
        assertEquals(0, domainFacade.robotDomainFacade().getEnergyLevelOfRobot(robot));
        assertEquals(0, domainFacade.robotDomainFacade().getDamageLevelOfRobot(robot));
        assertEquals(0, domainFacade.robotDomainFacade().getMiningSpeedLevelOfRobot(robot));
        assertEquals(0, domainFacade.robotDomainFacade().getMiningLevelOfRobot(robot));
        assertEquals(0, domainFacade.robotDomainFacade().getEnergyRegenLevelOfRobot(robot));
        assertEquals(2, domainFacade.robotDomainFacade().getMiningSpeedOfRobot(robot));
        assertEquals(10, domainFacade.robotDomainFacade().getMaxHealthOfRobot(robot));
        assertEquals(20, domainFacade.robotDomainFacade().getMaxEnergyOfRobot(robot));
        assertEquals(3, domainFacade.robotDomainFacade().getEnergyRegenOfRobot(robot));
        assertEquals(1, domainFacade.robotDomainFacade().getAttackDamageOfRobot(robot));

        assertEquals(planetId, domainFacade.planetDomainFacade().getPlanetIdOfPlanet(domainFacade.robotDomainFacade().getPlanetLocationOfRobot(robot)));

        assertFalse(domainFacade.robotDomainFacade().getInventoryFullStateOfRobot(robot));
        assertEquals(0, domainFacade.robotDomainFacade().getStorageLevelOfRobot(robot));
        assertEquals(0, domainFacade.robotDomainFacade().getInventoryUsedStorageOfRobot(robot));
        assertEquals(10, domainFacade.robotDomainFacade().getInventoryMaxStorageOfRobot(robot));
    }

    @Test
    public void testBankAccountClearedEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        Player player = playerRepository.findAll().get(0);
        domainFacade.playerDomainFacade().setBalanceForPlayer(player, 500);
        playerRepository.save(player);

        BankAccountClearedEvent bankAccountClearedEvent = new BankAccountClearedEvent();
        bankAccountClearedEvent.setPlayerId(player.getPlayerId());
        bankAccountClearedEvent.setBalance(0);

        this.requestEventFromMockService(bankAccountClearedEvent, "/trading/events/BankAccountCleared");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        player = playerRepository.findAll().get(0);

        assertNotNull(player);
        assertEquals(0, domainFacade.playerDomainFacade().getBalanceOfPlayer(player));
    }

    @Test
    public void testBankAccountTransactionBookedEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        Player player = playerRepository.findAll().get(0);
        domainFacade.playerDomainFacade().setBalanceForPlayer(player, 500);
        playerRepository.save(player);

        BankAccountTransactionBookedEvent bankAccountTransactionBookedEvent = new BankAccountTransactionBookedEvent();
        bankAccountTransactionBookedEvent.setPlayerId(player.getPlayerId());
        bankAccountTransactionBookedEvent.setBalance(400);
        bankAccountTransactionBookedEvent.setTransactionAmount(-100);

        this.requestEventFromMockService(bankAccountTransactionBookedEvent, "/trading/events/BankAccountTransactionBooked");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        player = playerRepository.findAll().get(0);

        assertNotNull(player);
        assertEquals(400, domainFacade.playerDomainFacade().getBalanceOfPlayer(player));
    }

    @Test
    public void testBankAccountInitializedEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        Player player = playerRepository.findAll().get(0);
        domainFacade.playerDomainFacade().setBalanceForPlayer(player, 0);
        playerRepository.save(player);

        BankInitializedEvent bankInitializedEvent = new BankInitializedEvent();
        bankInitializedEvent.setPlayerId(player.getPlayerId());
        bankInitializedEvent.setBalance(500);

        this.requestEventFromMockService(bankInitializedEvent, "/trading/events/BankAccountInitialized");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        player = playerRepository.findAll().get(0);

        assertNotNull(player);
        assertEquals(500, domainFacade.playerDomainFacade().getBalanceOfPlayer(player));
    }

    @Test
    public void testTradableBoughtEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        Player player = playerRepository.findAll().get(0);
        domainFacade.playerDomainFacade().setBalanceForPlayer(player, 500);
        playerRepository.save(player);

        TradableBoughtEvent tradableBoughtEvent = new TradableBoughtEvent();
        tradableBoughtEvent.setPlayerId(player.getPlayerId());
        tradableBoughtEvent.setRobotId(null);
        tradableBoughtEvent.setType("ITEM");
        tradableBoughtEvent.setName("ROBOT");
        tradableBoughtEvent.setAmount(3);
        tradableBoughtEvent.setPricePerUnit(100);
        tradableBoughtEvent.setTotalPrice(300);

        this.requestEventFromMockService(tradableBoughtEvent, "/trading/events/TradableBought");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        player = playerRepository.findAll().get(0);
        List<Object> robots = domainFacade.robotDomainFacade().getAllRobots();

        assertNotNull(player);
        assertEquals(3, robots.size());
        assertEquals(200, domainFacade.playerDomainFacade().getBalanceOfPlayer(player));
    }

    @Test
    public void testTradablePricesEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        TradablePricesEvent tradablePricesEvent = new TradablePricesEvent();
        tradablePricesEvent.setTradeableItems(List.of(
                new TradeableItem("STORAGE_1", Money.from(50), TradeableType.UPGRADE),
                new TradeableItem("HEALTH_1", Money.from(50), TradeableType.UPGRADE),
                new TradeableItem("HEALTH_2", Money.from(300), TradeableType.UPGRADE),
                new TradeableItem("HEALTH_RESTORE", Money.from(50), TradeableType.RESTORATION),
                new TradeableItem("ENERGY_RESTORE", Money.from(75), TradeableType.RESTORATION)
        ));

        this.requestEventFromMockService(tradablePricesEvent, "/trading/events/TradablePrices");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        List<Object> tradableItems = domainFacade.tradableDomainFacade().getAllTradableItems();

        assertEquals(5, tradableItems.size());

        assertEquals(50, domainFacade.tradableDomainFacade().getPriceOfTradableItem(domainFacade.tradableDomainFacade().getTradableItemByName("STORAGE_1")));
        assertEquals(50, domainFacade.tradableDomainFacade().getPriceOfTradableItem(domainFacade.tradableDomainFacade().getTradableItemByName("HEALTH_1")));
        assertEquals(300, domainFacade.tradableDomainFacade().getPriceOfTradableItem(domainFacade.tradableDomainFacade().getTradableItemByName("HEALTH_2")));
        assertEquals(50, domainFacade.tradableDomainFacade().getPriceOfTradableItem(domainFacade.tradableDomainFacade().getTradableItemByName("HEALTH_RESTORE")));
        assertEquals(75, domainFacade.tradableDomainFacade().getPriceOfTradableItem(domainFacade.tradableDomainFacade().getTradableItemByName("ENERGY_RESTORE")));

        assertEquals(TradeableType.UPGRADE, domainFacade.tradableDomainFacade().getTradableTypeOfTradableItem(domainFacade.tradableDomainFacade().getTradableItemByName("STORAGE_1")));
        assertEquals(TradeableType.UPGRADE, domainFacade.tradableDomainFacade().getTradableTypeOfTradableItem(domainFacade.tradableDomainFacade().getTradableItemByName("HEALTH_1")));
        assertEquals(TradeableType.UPGRADE, domainFacade.tradableDomainFacade().getTradableTypeOfTradableItem(domainFacade.tradableDomainFacade().getTradableItemByName("HEALTH_2")));
        assertEquals(TradeableType.RESTORATION, domainFacade.tradableDomainFacade().getTradableTypeOfTradableItem(domainFacade.tradableDomainFacade().getTradableItemByName("HEALTH_RESTORE")));
        assertEquals(TradeableType.RESTORATION, domainFacade.tradableDomainFacade().getTradableTypeOfTradableItem(domainFacade.tradableDomainFacade().getTradableItemByName("ENERGY_RESTORE")));
    }

    @Test
    public void testTradableSoldEventHandling() throws JsonProcessingException, InterruptedException {
        UUID gameId = UUID.randomUUID();
        Game game = Game.newlyCreatedGame(gameId);
        game.setGameStatus(GameStatus.STARTED);
        game.setCurrentRoundNumber(1);
        gameRepository.save(game);

        UUID domainId = game.getId();

        Player player = playerRepository.findAll().get(0);
        domainFacade.playerDomainFacade().setBalanceForPlayer(player, 500);
        playerRepository.save(player);

        var robot = domainFacade.robotDomainFacade().createNewRobot();
        UUID robotId = UUID.randomUUID();
        domainFacade.robotDomainFacade().setRobotIdForRobot(robot, robotId);
        domainFacade.robotDomainFacade().setHealthForRobot(robot, 10);
        domainFacade.robotDomainFacade().setEnergyForRobot(robot, 20);
        domainFacade.robotDomainFacade().setCoalAmountForRobot(robot, 10);
        domainFacade.robotDomainFacade().saveRobot(robot);

        TradableSoldEvent tradableSoldEvent = new TradableSoldEvent();
        tradableSoldEvent.setPlayerId(player.getPlayerId());
        tradableSoldEvent.setRobotId(robotId);
        tradableSoldEvent.setType("RESOURCE");
        tradableSoldEvent.setName("COAL");
        tradableSoldEvent.setAmount(10);
        tradableSoldEvent.setPricePerUnit(5);
        tradableSoldEvent.setTotalPrice(50);

        this.requestEventFromMockService(tradableSoldEvent, "/trading/events/TradableSold");

        //waiting for generated event to be consumed and processed by the player service
        Thread.sleep(Duration.ofSeconds(5).toMillis());

        player = playerRepository.findAll().get(0);
        robot = domainFacade.robotDomainFacade().getRobotByRobotId(robotId);

        assertNotNull(player);
        assertNotNull(robot);

        assertEquals(550, domainFacade.playerDomainFacade().getBalanceOfPlayer(player));
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

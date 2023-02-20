package thkoeln.dungeon.monte.robot.application;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal.RobotRevealedDto;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal.RobotsRevealedIntegrationEvent;
import thkoeln.dungeon.monte.core.util.PlayerInformation;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.player.application.RobotDtoMapper;
import thkoeln.dungeon.monte.robot.domain.*;
import thkoeln.dungeon.monte.trading.application.TradingAccountApplicationService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal.RobotRevealedDto.DEFAULT_STRENGTH;
import static thkoeln.dungeon.monte.robot.domain.RobotType.*;

@SpringBootTest
public class RobotApplicationServiceTest {
    private Robot w1, m1, m2, s1, s2, s3, s4, eA, eB;
    private UUID gameId, playerId;
    private Planet p1, p2, p3;
    private Energy energ15 = Energy.from( 15 );
    UUID newEnemyIdB, newEnemyIdC;

    private RobotApplicationService robotApplicationService;
    private PlayerInformation playerInformation;

    @Autowired
    private RobotRepository robotRepository;
    @Autowired
    private PlanetApplicationService planetApplicationService;
    @Autowired
    TradingAccountApplicationService tradingAccountApplicationService;
    @Autowired
    RobotDtoMapper robotDtoMapper;
    @Autowired @Qualifier( "warriorStrategy" )
    AbstractRobotStrategy warriorStrategy;
    @Autowired @Qualifier( "scoutStrategy" )
    AbstractRobotStrategy scoutStrategy;
    @Autowired @Qualifier( "minerStrategy" )
    AbstractRobotStrategy minerStrategy;

    protected class MockPlayerInformation implements PlayerInformation {
        @Override
        public UUID currentGameId() {
            return UUID.fromString( "db5dde14-9fff-45d0-9d9a-1aff53ad5a16" );
        }
        @Override
        public UUID currentPlayerId() {
            return UUID.fromString( "4aa72b16-acd5-42da-ab66-8e1eab86ca96" );
        }
    }


    @BeforeEach
    public void setUp() {
        robotRepository.deleteAll();

        initializeMockGameAndPlayer();
        initializeOwnRobots();
        initializeEnemyRobots();
        initializePlanets();
    }


    private void initializeMockGameAndPlayer() {
        playerInformation = new MockPlayerInformation();
        robotApplicationService = new RobotApplicationService( robotRepository, playerInformation,
                tradingAccountApplicationService, planetApplicationService, robotDtoMapper,
                warriorStrategy, scoutStrategy, minerStrategy );
        gameId = playerInformation.currentGameId();
        playerId = playerInformation.currentPlayerId();
    }


    private void initializeOwnRobots() {
        w1 = Robot.of( UUID.randomUUID(), WARRIOR, gameId, playerId );
        m1 = Robot.of( UUID.randomUUID(), MINER, gameId, playerId );
        m2 = Robot.of( UUID.randomUUID(), MINER, gameId, playerId );
        s1 = Robot.of( UUID.randomUUID(), SCOUT, gameId, playerId );
        s2 = Robot.of( UUID.randomUUID(), SCOUT, gameId, playerId );
        s3 = Robot.of( UUID.randomUUID(), SCOUT, gameId, playerId );
        s3.setAlive( false );
        s4 = Robot.of( UUID.randomUUID(), SCOUT, gameId, playerId );
        robotRepository.save( w1 );
        robotRepository.save( m1 );
        robotRepository.save( m2 );
        robotRepository.save( s1 );
        robotRepository.save( s2 );
        robotRepository.save( s3 );
        robotRepository.save( s4 );
    }


    private void initializeEnemyRobots() {
        eA = Robot.of( UUID.randomUUID(), null, gameId, null );
        eA.setEnemyChar( 'A' );
        eB = Robot.of( UUID.randomUUID(), null, gameId, null );
        eB.setEnemyChar( 'B' );
        robotRepository.save( eA );
        robotRepository.save( eB );
        newEnemyIdB = UUID.randomUUID();
        newEnemyIdC = UUID.randomUUID();
    }


    private void initializePlanets() {
        p1 = planetApplicationService.addOrUpdatePlanet( UUID.randomUUID(), Energy.from( 1 ) );
        p2 = planetApplicationService.addOrUpdatePlanet( UUID.randomUUID(), Energy.from( 2 ) );
        p3 = planetApplicationService.addOrUpdatePlanet( UUID.randomUUID(), Energy.from( 3 ) );
    }



    @Test
    public void test_ListSizes() {
        // given
        // when
        int totalNumOfRobots = robotApplicationService.allLivingRobots().size();
        int numOfOwnRobots = robotApplicationService.allLivingOwnRobots().size();
        int numOfEnemies = robotApplicationService.allLivingEnemyRobots().size();

        // then
        assertEquals( 8, totalNumOfRobots );
        assertEquals( 6, numOfOwnRobots );
        assertEquals( 2, numOfEnemies );
    }


    public void test_ListContains() {
        // given
        // when
        List<Robot> allRobots = robotApplicationService.allLivingRobots();
        List<Robot> ownRobots  = robotApplicationService.allLivingOwnRobots();
        List<Robot> enemies  = robotApplicationService.allLivingEnemyRobots();

        // then
        assertTrue( allRobots.contains( w1 ) );
        assertTrue( allRobots.contains( m2 ) );
        assertTrue( allRobots.contains( eA ) );
        assertTrue( allRobots.contains( eB ) );
        assertTrue( ownRobots.contains( w1 ) );
        assertTrue( ownRobots.contains( m1 ) );
        assertTrue( ownRobots.contains( s3 ) );
        assertFalse( ownRobots.contains( eA ) );
        assertFalse( ownRobots.contains( eA ) );
        assertTrue( enemies.contains( eA ) );
        assertTrue( enemies.contains( eA ) );
        assertFalse( ownRobots.contains( m1 ) );
        assertFalse( ownRobots.contains( s4 ) );
    }


    @Test
    public void testQuota() {
        // given
        // Total = 6; warrior-% is 16 (should be 20), miner-% is 33 (should be 30)

        // when
        // Should buy miner.
        RobotType warrior1 = robotApplicationService.nextRobotTypeAccordingToQuota();
        Robot newR = Robot.of( UUID.randomUUID(), warrior1, gameId, playerId );
        robotRepository.save( newR );
        // Afterwards: Total = 7; warrior-%  28, miner-% 28

        // Should buy miner.
        RobotType miner2 = robotApplicationService.nextRobotTypeAccordingToQuota();
        newR = Robot.of( UUID.randomUUID(), miner2, gameId, playerId );
        robotRepository.save( newR );
        // Afterwards: Total = 8; Warrior-% 25, miner-% 37

        // Should buy scout.
        RobotType scout3 = robotApplicationService.nextRobotTypeAccordingToQuota();
        newR = Robot.of( UUID.randomUUID(), scout3, gameId, playerId );
        robotRepository.save( newR );
        // Afterwards: Total = 9; Warrior-% 22, miner-% 33

        // Should buy scout.
        RobotType scout4 = robotApplicationService.nextRobotTypeAccordingToQuota();
        newR = Robot.of( UUID.randomUUID(), scout4, gameId, playerId );
        robotRepository.save( newR );
        // Afterwards: Total = 10; Warrior-% 20, miner-% 30

        // Should buy scout.
        RobotType scout5 = robotApplicationService.nextRobotTypeAccordingToQuota();
        newR = Robot.of( UUID.randomUUID(), scout5, gameId, playerId );
        robotRepository.save( newR );
        // Afterwards: Total = 11; Warrior-% 18, miner-% 27

        // Should buy warrior.
        RobotType warrior6 = robotApplicationService.nextRobotTypeAccordingToQuota();
        newR = Robot.of( UUID.randomUUID(), warrior6, gameId, playerId );
        robotRepository.save( newR );

        // then
        assertEquals( WARRIOR, warrior1 );
        assertEquals( MINER, miner2 );
        assertEquals( SCOUT, scout3 );
        assertEquals( SCOUT, scout4 );
        assertEquals( SCOUT, scout5 );
        assertEquals( WARRIOR, warrior6 );
    }


    @Test
    public void test_addNewOwnRobot_new_Robot_is_properly_saved() {
        // given
        UUID robotId = UUID.randomUUID();

        // when
        Robot robot = robotApplicationService.addNewOwnRobot( robotId, p1 );

        // then
        assertsForHealthyOwnRobot( robot, robotId, p1 );
    }




    @Test
    public void test_addNewOwnRobot_parameter_validation() {
        assertThrows( RobotException.class, () -> {
            robotApplicationService.addNewOwnRobot( null, p1 );
        });
        assertThrows( RobotException.class, () -> {
            robotApplicationService.addNewOwnRobot( UUID.randomUUID(), null );
        });
    }


    @Test
    public void test_addNewOwnRobot_no_double_robotId() {
        assertThrows( RobotException.class, () -> {
            robotApplicationService.addNewOwnRobot( s1.getRobotId(), p1 );
        });
    }


    @Test
    public void test_queryAndIfNeededAddRobot_parameter_validation() {
        assertThrows( RobotException.class, () -> {
            robotApplicationService.queryAndIfNeededAddRobot( null, 'A' );
        });
    }


    @Test
    public void test_queryAndIfNeededAddRobot_own_vs_enemy_mismatch() {
        assertThrows( RobotException.class, () -> {
            robotApplicationService.queryAndIfNeededAddRobot( s2.getRobotId(), 'A' );
        });
    }


    @Test
    public void test_queryAndIfNeededAddRobot_incorrect_enemy_char() {
        assertThrows( RobotException.class, () -> {
            robotApplicationService.queryAndIfNeededAddRobot( eA.getRobotId(), 'B' );
        });
    }


    @Test
    public void test_queryAndIfNeededAddRobot_get_correct_robot() {
        // given
        Robot enemy = robotApplicationService.queryAndIfNeededAddRobot( eA.getRobotId(), 'A' );
        Robot own = robotApplicationService.queryAndIfNeededAddRobot( m2.getRobotId(), null );

        // when
        // then
        assertEquals( eA, enemy );
        assertEquals( m2, own );
    }

    @Test
    public void test_queryAndIfNeededAddRobot_properly_add_own_robot() {
        // given
        UUID robotId = UUID.randomUUID();
        Robot own = robotApplicationService.queryAndIfNeededAddRobot( robotId, null );
        
        // when
        // then
        assertsForHealthyOwnRobot( own, robotId, null );
    }




    @Test
    public void test_queryAndIfNeededAddRobot_properly_add_enemy_robot() {
        // given
        UUID robotId = UUID.randomUUID();
        Robot enemy = robotApplicationService.queryAndIfNeededAddRobot( robotId, 'B' );

        // when
        // then
        assertsForHealthyEnemyRobot( enemy, robotId, 'B', null );
    }


    @Test
    public void test_moveRobotToNewPlanet_parameter_validation() {
        assertThrows( RobotException.class, () -> {
            robotApplicationService.moveRobotToNewPlanet( null, p1, energ15 );
        });
        assertThrows( RobotException.class, () -> {
            robotApplicationService.moveRobotToNewPlanet( w1.getRobotId(), null, energ15 );
        });
        assertThrows( RobotException.class, () -> {
            robotApplicationService.moveRobotToNewPlanet( w1.getRobotId(), p2, null );
        });
    }



    @Test
    public void test_moveRobotToNewPlanet_null_when_robot_unknown() {
        // given
        // when
        Robot robot = robotApplicationService.moveRobotToNewPlanet( UUID.randomUUID(), p2, energ15 );

        // then
        assertNull( robot );
    }


    @Test
    public void test_moveRobotToNewPlanet_robot_properly_updated() {
        // given
        // when
        Robot robot = robotApplicationService.moveRobotToNewPlanet( s2.getRobotId(), p2, energ15 );

        // then
        assertsForHealthyOwnRobot( robot, s2.getRobotId(), p2 );
        assertEquals( s2, robot );
        assertEquals( energ15, robot.getEnergy() );
    }


    @Test
    public void test_updateRobotsFromExternalEvent_numbers_are_correct() {
        // given
        RobotsRevealedIntegrationEvent event = createRobotsRevealedEvent();
        int totalNumOfRobotsBefore = robotApplicationService.allLivingRobots().size();
        int numOfOwnRobotsBefore = robotApplicationService.allLivingOwnRobots().size();
        int numOfEnemiesBefore = robotApplicationService.allLivingEnemyRobots().size();

        // when
        robotApplicationService.updateRobotsFromExternalEvent( event );
        List<Robot> allRobots = robotApplicationService.allLivingRobots();
        List<Robot> ownRobots  = robotApplicationService.allLivingOwnRobots();
        List<Robot> enemies  = robotApplicationService.allLivingEnemyRobots();

        // then
        assertEquals( totalNumOfRobotsBefore + 2, allRobots.size() );
        assertEquals( numOfOwnRobotsBefore, ownRobots.size() );
        assertEquals( numOfEnemiesBefore + 2, enemies.size() );
    }


    @Test
    public void test_updateRobotsFromExternalEvent_new_robots() {
        // given
        RobotsRevealedIntegrationEvent event = createRobotsRevealedEvent();

        // when
        robotApplicationService.updateRobotsFromExternalEvent( event );
        List<Robot> enemies  = robotApplicationService.allLivingEnemyRobots();

        // then
        for ( Robot robot : enemies ) {
            if ( robot.getRobotId().equals( newEnemyIdB ) ) {
                assertEquals( p1, robot.getLocation() );
                assertEquals( 'B', robot.getEnemyChar() );
            }
            if ( robot.getRobotId().equals( newEnemyIdC ) ) {
                assertEquals( p3, robot.getLocation() );
                assertEquals( 'C', robot.getEnemyChar() );
            }
        }
    }


    @Test
    public void test_updateRobotsFromExternalEvent_existing_robots() {
        // given
        RobotsRevealedIntegrationEvent event = createRobotsRevealedEvent();

        // when
        robotApplicationService.updateRobotsFromExternalEvent( event );
        Robot w1updated = robotApplicationService.queryAndIfNeededAddRobot( w1.getRobotId() );
        Robot s1updated = robotApplicationService.queryAndIfNeededAddRobot( s1.getRobotId() );
        Robot eAupdated = robotApplicationService.queryAndIfNeededAddRobot( eA.getRobotId(), 'A' );
        Robot eBupdated = robotApplicationService.queryAndIfNeededAddRobot( eB.getRobotId(), 'B' );

        // then
        assertEquals( w1, w1updated );
        assertEquals( p1, w1updated.getLocation() );
        assertEquals( DEFAULT_STRENGTH, w1updated.getEnergy().getEnergyAmount() );
        assertFalse( w1updated.isEnemy() );
        // todo: test health and levels, once they are implemented

        assertEquals( s1, s1updated );
        assertEquals( p3, s1updated.getLocation() );
        assertEquals( DEFAULT_STRENGTH, s1updated.getEnergy().getEnergyAmount() );
        assertFalse( s1updated.isEnemy() );

        assertEquals( eA, eAupdated );
        assertEquals( p2, eAupdated.getLocation() );
        assertEquals( DEFAULT_STRENGTH, eAupdated.getEnergy().getEnergyAmount() );
        assertTrue( eAupdated.isEnemy() );
        assertEquals( 'A', eAupdated.getEnemyChar() );

        assertEquals( eB, eBupdated );
        assertEquals( p3, eBupdated.getLocation() );
        assertEquals( DEFAULT_STRENGTH, eBupdated.getEnergy().getEnergyAmount() );
        assertTrue( eBupdated.isEnemy() );
        assertEquals( 'B', eBupdated.getEnemyChar() );
    }


    @Test
    public void test_updateRobotsFromExternalEvent_correct_robots_on_planet() {
        // given
        RobotsRevealedIntegrationEvent event = createRobotsRevealedEvent();

        // when
        robotApplicationService.updateRobotsFromExternalEvent(event);
        List<Robot> p1robots  = robotApplicationService.livingRobotsOnPlanet( p1 );
        List<Robot> p2robots  = robotApplicationService.livingRobotsOnPlanet( p2 );
        List<Robot> p3robots  = robotApplicationService.livingRobotsOnPlanet( p3 );

        // then
        assertEquals( 2, p1robots.size() );
        assertEquals( 1, p2robots.size() );
        assertEquals( 3, p3robots.size() );

        assertTrue( p1robots.contains( w1 ) );
        assertTrue( p2robots.contains( eA ) );
        assertTrue( p3robots.contains( s1 ) );
        assertTrue( p3robots.contains( eB ) );
    }


    @NotNull
    private RobotsRevealedIntegrationEvent createRobotsRevealedEvent() {
        RobotsRevealedIntegrationEvent event = new RobotsRevealedIntegrationEvent();
        RobotRevealedDto[] revealedDtos = new RobotRevealedDto[] {
            RobotRevealedDto.defaultsFor( w1.getRobotId(), p1.getPlanetId(), null, null ),
            RobotRevealedDto.defaultsFor( s1.getRobotId(), p3.getPlanetId(), null, null ),
            RobotRevealedDto.defaultsFor( eA.getRobotId(), p2.getPlanetId(), null, 'A' ),
            RobotRevealedDto.defaultsFor( eB.getRobotId(), p3.getPlanetId(), null, 'B' ),
            RobotRevealedDto.defaultsFor( newEnemyIdB, p1.getPlanetId(), null, 'B' ),
            RobotRevealedDto.defaultsFor( newEnemyIdC, p3.getPlanetId(), null, 'C' )
        };
        event.setRobots( revealedDtos );
        return event;
    }


    private void assertsForHealthyOwnRobot( Robot robot, UUID robotId, Planet planet ) {
        // when
        List<Robot> robots = robotApplicationService.allLivingRobots();

        // then
        assertTrue( robots.contains( robot ) );
        assertNotNull( robot.getStrategy() );
        assertEquals(robotId, robot.getRobotId() );
        assertEquals( gameId, robot.getGameId() );
        assertEquals( playerId, robot.getPlayerId() );
        assertTrue( robot.isAlive() );
        assertFalse( robot.isEnemy() );
        assertNull( robot.enemyChar() );
        assertEquals( planet, robot.getLocation() );
    }


    private void assertsForHealthyEnemyRobot( Robot robot, UUID robotId, Character enemyChar, Planet planet ) {
        // when
        List<Robot> robots = robotApplicationService.allLivingRobots();

        // then
        assertTrue( robots.contains( robot ) );
        assertEquals( robotId, robot.getRobotId() );
        assertEquals( gameId, robot.getGameId() );
        assertNull( robot.getPlayerId() );
        assertTrue( robot.isAlive() );
        assertTrue( robot.isEnemy() );
        assertEquals( enemyChar, robot.enemyChar() );
        assertEquals( planet, robot.getLocation() );
    }
}

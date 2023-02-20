package thkoeln.dungeon.monte.planet.application;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.planet.PlanetNeighboursDto;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.planet.domain.PlanetRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection.*;


/**
 * This class tests via random walks if the planet map is correctly built.
 */

@SpringBootTest
public class MapWalkTest {
    private Logger logger = LoggerFactory.getLogger(MapWalkTest.class);
    Random random = new Random();

    @AllArgsConstructor
    @NoArgsConstructor
    public class Coordinate {
        public int x, y;
        public Coordinate neighbour( CompassDirection direction ) {
            if ( direction == NORTH ) return ( y > 0 ) ? new Coordinate( x, y-1 ) : null;
            if ( direction == EAST ) return ( x < DIM_X-1 ) ? new Coordinate( x+1, y ) : null;
            if ( direction == SOUTH ) return ( y < DIM_Y-1 ) ? new Coordinate( x, y+1 ) : null;
            if ( direction == WEST ) return ( x > 0 ) ? new Coordinate( x-1, y ) : null;
            return null;
        }
        public UUID neighbourId( CompassDirection direction ) {
            Coordinate c = neighbour( direction );
            if ( c == null ) return null;
            return planetIds[c.y][c.x];
        }
        public Coordinate randomStep() {
            for ( int i = 0; i < 15; i++ ) {
                CompassDirection direction = CompassDirection.random();
                Coordinate c = neighbour( direction );
                if ( c != null ) {
                    UUID id = neighbourId( direction );
                    if ( id != null ) return c;
                }
            }
            return this;
        }
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    private static int DIM_X, DIM_Y;
    private static float BLACK_HOLE_RATIO;
    private static int NUMOF_ROBOTS;
    private static int NUMOF_ITERATIONS;

    private boolean[][] blackHoles;
    private UUID[][] planetIds;
    private UUID[] robotIds;
    private Coordinate[] robotCoordinates;

    @Autowired
    private PlanetApplicationService planetApplicationService;
    @Autowired
    private PlanetRepository planetRepository;

    @BeforeEach
    public void setupRandom() {
        DIM_X = 11;
        DIM_Y = 9;
        BLACK_HOLE_RATIO = 0.3f;
        NUMOF_ROBOTS = 1;
        NUMOF_ITERATIONS = 30;

        planetRepository.deleteAll();
        initializeBlackHoles();
        initializePlanets();
        initializeRobots();
        logger.info( "--- Test Setup done");
    }


    private void initializeBlackHoles() {
        blackHoles = new boolean[DIM_Y][DIM_X];
        for ( int x = 0; x < DIM_X; x++ ) {
            for ( int y = 0; y < DIM_Y; y++ ) {
                blackHoles[y][x] = ( random.nextFloat() <= BLACK_HOLE_RATIO );
            }
        }
    }


    private void initializePlanets() {
        planetIds = new UUID[DIM_Y][DIM_X];
        for ( int x = 0; x < DIM_X; x++ ) {
            for ( int y = 0; y < DIM_Y; y++ ) {
                planetIds[y][x] = blackHoles[y][x] ? null : UUID.randomUUID();
            }
        }
    }


    private void initializeRobots() {
        robotIds = new UUID[NUMOF_ROBOTS];
        robotCoordinates = new Coordinate[NUMOF_ROBOTS];
        for ( int i = 0; i < NUMOF_ROBOTS; i++ ) {
            robotIds[i] = UUID.randomUUID();
            Coordinate c;
            do {
                c = randomCoordinate();
            } while ( blackHoles[c.y][c.x] );
            robotCoordinates[i] = c;
        }
    }


    // can't do this as static factory method within Coordinate :-(
    private Coordinate randomCoordinate() {
        Coordinate c = new Coordinate();
        c.x = random.nextInt( MapWalkTest.DIM_X );
        c.y = random.nextInt( MapWalkTest.DIM_Y );
        return c;
    }


    @Test
    public void testRandomWalk() {
        logger.info( "--- Random Walk");
        testProperSetup();
        spawnRobots();
        for ( int i = 0; i < NUMOF_ITERATIONS; i++ ) {
            logger.info( "--- Start Iteration " + i );
            robotRandomWalk();
        }
        assertTrue( true );
    }

    @Test
    public void testRandomWalk99() {
        for (int i = 0; i < 99; i++) {
            setupRandom();
            testRandomWalk();
        }
    }



    /**
     * make sure the init has been properly done
     */
    private void testProperSetup() {
        for ( int x = 0; x < DIM_X; x++ ) {
            for ( int y = 0; y < DIM_Y; y++ ) {
                assertEquals( blackHoles[y][x], planetIds[y][x] == null );
            }
        }
    }

    private void spawnRobots() {
        for ( int iR = 0; iR < NUMOF_ROBOTS; iR++ ) {
            Coordinate c = robotCoordinates[iR];
            logger.info( "--- Spawn robot no. " + iR + " at " + c );
            mockReceiveRobotSpawnedEvent( c );
            mockReceivePlanetDiscoveredEvent( c );
        }
        testAllPlanets();
    }


    private void robotRandomWalk() {
        for ( int iR = 0; iR < NUMOF_ROBOTS; iR++ ) {
            Coordinate c = robotCoordinates[iR];
            c = c.randomStep();
            logger.info( "--- Walk robot no. " + iR + " to " + c );
            if ( c != null ) {
                mockReceivePlanetDiscoveredEvent(c);
            }
        }
        testAllPlanets();
    }


    private void mockReceiveRobotSpawnedEvent( Coordinate c ) {
        UUID planetId = planetIds[c.y][c.x];
        assertNotNull( planetId );
        Planet planet = planetApplicationService.addOrUpdatePlanet( planetId, Energy.defaultMovementDifficulty() );
        planet.setVisited( true );
        planetApplicationService.save( planet );
    }


    private void mockReceivePlanetDiscoveredEvent( Coordinate c ) {
        List<PlanetNeighboursDto> neighbourDtos = new ArrayList<>();
        for ( CompassDirection direction : CompassDirection.values() ) {
            Coordinate d = c.neighbour( direction );
            if ( d != null && !blackHoles[d.y][d.x] ) {
                PlanetNeighboursDto dto = new PlanetNeighboursDto();
                dto.setDirection( direction );
                dto.setId( planetIds[d.y][d.x] );
                assertNotNull( dto.getId() );
                neighbourDtos.add( dto );
            }
        }
        PlanetNeighboursDto[] dtoArray = new PlanetNeighboursDto[neighbourDtos.size()];
        dtoArray = neighbourDtos.toArray( dtoArray );

        PlanetDiscoveredEvent event = new PlanetDiscoveredEvent();
        event.setPlanetId( planetIds[c.y][c.x] );
        assertNotNull( event.getPlanetId() );
        event.setNeighbours( dtoArray );
        event.setMovementDifficulty( 1 );
        planetApplicationService.addPlanetNeighbours( event );
    }


    private void testAllPlanets() {
        List<Planet> allPlanets = planetApplicationService.allPlanets();
        for ( Planet planet : allPlanets ) {
            Coordinate c = findCoordinateFor( planet );
            for ( CompassDirection direction : CompassDirection.values() ) {
                Planet neighbour = planet.getNeighbour( direction );
                if ( neighbour != null ) {
                    // ... then we already know the neighbour
                    if ( TRUE.equals( planet.getHardBorder( direction ) ) ) {
                        assertNotEquals( TRUE, planet.getHardBorder( direction ) );
                    }
                    assertNotEquals( TRUE, planet.getHardBorder( direction ) );
                    UUID expectedId = c.neighbourId( direction );
                    assertEquals( expectedId, neighbour.getPlanetId() );
                    // and also test for bidirectional pointers
                    CompassDirection oppositeDirection = direction.getOppositeDirection();
                    Planet thisShouldBeMe = neighbour.getNeighbour( oppositeDirection );
                    if ( thisShouldBeMe == null ) {
                        assertNotNull( thisShouldBeMe );
                    }
                    assertEquals( planet, thisShouldBeMe );
                }
                Coordinate neighbourC = c.neighbour( direction );
                if ( neighbourC != null && blackHoles[neighbourC.y][neighbourC.x] ) {
                    assertNotEquals( FALSE, planet.getHardBorder( direction ) );
                }
            }
        }
    }


    private Coordinate findCoordinateFor( Planet planet ) {
        // not an efficient implementation ...
        for ( int x = 0; x < DIM_X; x++ ) {
            for ( int y = 0; y < DIM_Y; y++ ) {
                if ( !blackHoles[y][x] && planetIds[y][x].equals( planet.getPlanetId() ) ) {
                    Coordinate c = new Coordinate();
                    c.x = x;
                    c.y = y;
                    return c;
                }
            }
        }
        return null;
    }


    /**
     * Test a specific black hole problem. Consider a 2x3 matrix with 1 black hole and 2 robots.
     * After initial RobotSpawned event:
     *
     *    ( )   (H)   ( )
     *
     *    (R)   ( )   (R)
     *
     * After first PlanetDiscoveredEvent:
     *
     *    ( )   (H)   ( )
     *     |           |
     *    (R) - ( ) - (R)
     *
     *  Iteration 0: left robot moves up. After PlanetDiscoveredEvent (and closing cycle):
     *
     *    (R) - (H)   ( )
     *     |     |     |
     *    ( ) - ( ) - (R)
     *
     *  Iteration 1: right robot moves up. After PlanetDiscoveredEvent (and closing cycle):
     *
     *    (R) - (H) (H)(R)
     *     |     | /   |
     *    ( ) - ( ) - ( )
     *
     *   I.e. this second move causes an extra black hole to be produced - if the code does not
     *   check that.
     */
    @Test
    public void testBlackHolePattern() {
        init32BlackHolePattern();
        spawnRobots();
        robotWalk( 0, new Coordinate( 0, 0 ) );
        robotWalk( 1, new Coordinate( 2, 0 ) );

        List<Planet> allPlanets = planetApplicationService.allPlanets();
        assertEquals( 5, allPlanets.size() );
    }


    private void init32BlackHolePattern() {
        DIM_X = 3;
        DIM_Y = 2;
        NUMOF_ROBOTS = 2;
        blackHoles = new boolean[][] { {false, true, false}, {false, false, false} };
        planetIds = new UUID[][] { {UUID.randomUUID(), null, UUID.randomUUID()}, {UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()} };
        robotCoordinates = new Coordinate[] { new Coordinate(0, 1), new Coordinate(2, 1),  };
        testProperSetup();
    }

    private void robotWalk( int robotIndex, Coordinate target ) {
        robotCoordinates[robotIndex] = target;
        mockReceiveRobotSpawnedEvent( target );
        mockReceivePlanetDiscoveredEvent( target );
        testAllPlanets();
    }


}
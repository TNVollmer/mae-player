package thkoeln.dungeon.monte.planet.application;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.Before;
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
import thkoeln.dungeon.monte.player.application.PlayerApplicationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection.*;


/**
 * This class tests via random walks if the planet map is correctly built.
 */

@SpringBootTest
public class MapBuildingRandomTest {
    private Logger logger = LoggerFactory.getLogger(MapBuildingRandomTest.class);
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
            return planetIds[c.x][c.y];
        }
        public Coordinate randomStep() {
            for ( int i = 0; i < 10; i++ ) {
                CompassDirection direction = CompassDirection.random();
                Coordinate c = neighbour( direction );
                if ( c != null ) return c;
            }
            return null;
        }
    }

    private final static int DIM_X = 10;
    private final static int DIM_Y = 10;
    private final static float BLACK_HOLE_RATIO = 0.0f;
    private final static int NUMOF_ROBOTS = 4;
    private final static int NUMOF_ITERATIONS = 30;

    private boolean[][] blackHoles;
    private UUID[][] planetIds;
    private UUID[] robotIds;
    private Coordinate[] robotCoordinates;

    @Autowired
    private PlanetApplicationService planetApplicationService;
    @Autowired
    private PlanetRepository planetRepository;

    @BeforeEach
    public void setup() {
        planetRepository.deleteAll();
        initializeBlackHoles();
        initializePlanets();
        initializeRobots();
        logger.info( "--- Test Setup done");
    }


    private void initializeBlackHoles() {
        blackHoles = new boolean[DIM_X][DIM_Y];
        for ( int x = 0; x < DIM_X; x++ ) {
            for ( int y = 0; y < DIM_Y; y++ ) {
                blackHoles[x][y] = ( random.nextFloat() <= BLACK_HOLE_RATIO );
            }
        }
    }


    private void initializePlanets() {
        planetIds = new UUID[DIM_X][DIM_Y];
        for ( int x = 0; x < DIM_X; x++ ) {
            for ( int y = 0; y < DIM_Y; y++ ) {
                planetIds[x][y] = blackHoles[x][y] ? null : UUID.randomUUID();
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
            } while ( blackHoles[c.x][c.y] );
            robotCoordinates[i] = c;
        }
    }


    // can't do this as static factory method within Coordinate :-(
    private Coordinate randomCoordinate() {
        Coordinate c = new Coordinate();
        c.x = random.nextInt( MapBuildingRandomTest.DIM_X );
        c.x = random.nextInt( MapBuildingRandomTest.DIM_X );
        return c;
    }


    @Test
    public void testRandomWalk() {
        logger.info( "--- Random Walk");
        testProperSetup();
        spawnRobots();
        testAllPlanets();
        for ( int i = 0; i < NUMOF_ITERATIONS; i++ ) {
            logger.info( "--- Start Iteration " + i );
            robotRandomWalk();
            testAllPlanets();
        }
        assertTrue( true );
    }


    /**
     * make sure the init has been properly done
     */
    private void testProperSetup() {
        for ( int x = 0; x < DIM_X; x++ ) {
            for ( int y = 0; y < DIM_Y; y++ ) {
                assertEquals( blackHoles[x][y], planetIds[x][y] == null );
            }
        }
    }

    private void spawnRobots() {
        for ( int iR = 0; iR < NUMOF_ROBOTS; iR++ ) {
            Coordinate c = robotCoordinates[iR];
            mockReceiveRobotSpawnedEvent( c );
            mockReceivePlanetDiscoveredEvent( c );
        }
    }


    private void robotRandomWalk() {
        for ( int iR = 0; iR < NUMOF_ROBOTS; iR++ ) {
            Coordinate c = robotCoordinates[iR];
            c = c.randomStep();
            if ( c != null ) {
                mockReceiveRobotSpawnedEvent(c);
                mockReceivePlanetDiscoveredEvent(c);
            }
        }
    }


    private void mockReceiveRobotSpawnedEvent( Coordinate c ) {
        UUID planetId = planetIds[c.x][c.y];
        Planet planet = planetApplicationService.addOrUpdatePlanet( planetId, Energy.defaultMovementDifficulty() );
        planet.setVisited( true );
        planetApplicationService.save( planet );
    }


    private void mockReceivePlanetDiscoveredEvent( Coordinate c ) {
        List<PlanetNeighboursDto> neighbourDtos = new ArrayList<>();
        for ( CompassDirection direction : CompassDirection.values() ) {
            Coordinate d = c.neighbour( direction );
            if ( d != null && !blackHoles[d.x][d.y] ) {
                PlanetNeighboursDto dto = new PlanetNeighboursDto();
                dto.setDirection( direction );
                dto.setId( planetIds[d.x][d.y] );
                assertNotNull( dto.getId() );
                neighbourDtos.add( dto );
            }
        }
        PlanetNeighboursDto[] dtoArray = new PlanetNeighboursDto[neighbourDtos.size()];
        dtoArray = neighbourDtos.toArray( dtoArray );

        PlanetDiscoveredEvent event = new PlanetDiscoveredEvent();
        event.setPlanetId( planetIds[c.x][c.y] );
        assertNotNull( event.getPlanetId() );
        event.setNeighbours( dtoArray );
        event.setMovementDifficulty( 1 );
        planetApplicationService.addPlanetNeighbours( event );
    }


    private void testAllPlanets() {
        List<Planet> allPlanets = planetApplicationService.allPlanetsWithoutBlackHoles();
        for ( Planet planet : allPlanets ) {
            Coordinate c = findCoordinateFor( planet );
            for ( CompassDirection direction : CompassDirection.values() ) {
                Planet neighbour = planet.getNeighbour( direction );
                if ( neighbour != null ) {
                    // ... then we already know the neighbour
                    if ( blackHoles[c.x][c.y] ) {
                        assertTrue( neighbour.isBlackHole() );
                    }
                    else {
                        UUID expectedId = c.neighbourId( direction );
                        assertEquals( expectedId, neighbour.getPlanetId() );
                    }
                    // and also test for bidirectional pointers
                    CompassDirection oppositeDirection = direction.getOppositeDirection();
                    Planet thisShouldBeMe = neighbour.getNeighbour( oppositeDirection );
                    assertNotNull( thisShouldBeMe );
                    assertEquals( planet, thisShouldBeMe );
                }
            }
        }
    }


    private Coordinate findCoordinateFor( Planet planet ) {
        // not an efficient implementation ...
        for ( int x = 0; x < DIM_X; x++ ) {
            for ( int y = 0; y < DIM_Y; y++ ) {
                if ( !blackHoles[x][y] && planetIds[x][y].equals( planet.getPlanetId() ) ) {
                    Coordinate c = new Coordinate();
                    c.x = x;
                    c.y = y;
                    return c;
                }
            }
        }
        return null;
    }

}
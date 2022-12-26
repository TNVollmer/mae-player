package thkoeln.dungeon.monte.planet.application;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import thkoeln.dungeon.monte.DungeonPlayerConfiguration;
import thkoeln.dungeon.monte.domainprimitives.CompassDirection;
import thkoeln.dungeon.monte.domainprimitives.Coordinate;
import thkoeln.dungeon.monte.domainprimitives.TwoDimDynamicArray;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.planet.domain.PlanetRepository;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class PlanetNeighbourRelationTest {
    private Logger logger = LoggerFactory.getLogger( PlanetNeighbourRelationTest.class );
    private Planet n, s, ne, se, nee, see;
    Coordinate c01, c11, c21, c00, c10, c20;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    PlanetApplicationService planetApplicationService;

    @Before
    public void setUp() {
        c00 = Coordinate.fromInteger( 0, 0 );
        c10 = Coordinate.fromInteger( 1, 0 );
        c20 = Coordinate.fromInteger( 2, 0 );
        c01 = Coordinate.fromInteger( 0, 1 );
        c11 = Coordinate.fromInteger( 1, 1 );
        c21 = Coordinate.fromInteger( 2, 1 );

        planetRepository.deleteAll();
        n = new Planet( "n" );
        n.setSpacestation( Boolean.TRUE );
        s = new Planet( "s" );
        ne = new Planet( "ne" );
        nee = new Planet( "nee" );
        se = new Planet( "se" );
        see = new Planet( "see" );
        see.setSpacestation( Boolean.TRUE );
        planetRepository.save( n );
        planetRepository.save( s );
        planetRepository.save( ne );
        planetRepository.save( se );
        planetRepository.save( nee );
        planetRepository.save( see );
    }

    private void saveAll() {
        planetRepository.save( n );
        planetRepository.save( s );
        planetRepository.save( ne );
        planetRepository.save( se );
        planetRepository.save( nee );
        planetRepository.save( see );
    }


    @Test
    public void testOneIsland() {
        // given
        n.defineNeighbour( s, CompassDirection.SOUTH );
        n.defineNeighbour( ne, CompassDirection.EAST );
        ne.defineNeighbour( nee, CompassDirection.EAST );
        s.defineNeighbour( se, CompassDirection.EAST );
        se.defineNeighbour( see, CompassDirection.EAST );
        saveAll();
        //    N--NE--NEE
        //    |
        //    S--SE--SEE

        // when
        Map<Planet, TwoDimDynamicArray<Planet>> planetMap = planetApplicationService.allPlanetsAs2DArrays();

        // then
        logger.info( "\n\n---------\n" + planetMap.get( n ).toString() );
        assertEquals( 1, planetMap.size() );
        assertEquals( 2, planetMap.get( n ).sizeY() );
        assertEquals( 3, planetMap.get( n ).sizeX() );
        assertEquals( "s", planetMap.get( n ).get( c01 ).getName() );
        assertEquals( "se", planetMap.get( n ).get( c11 ).getName() );
        assertEquals( "see", planetMap.get( n ).get( c21 ).getName() );
        assertEquals( "n", planetMap.get( n ).get( c00 ).getName() );
        assertEquals( "ne", planetMap.get( n ).get( c10 ).getName() );
        assertEquals( "nee", planetMap.get( n ).get( c20 ).getName() );
    }


    @Test
    public void testTwoIslands() {
        // given
        n.defineNeighbour( s, CompassDirection.SOUTH );
        n.defineNeighbour( ne, CompassDirection.EAST );
        //    N--NE
        //    |
        //    S        SEE
        saveAll();

        // when
        Map<Planet, TwoDimDynamicArray<Planet>> planetMap = planetApplicationService.allPlanetsAs2DArrays();
        logger.info( "\n\n---------\n" + planetMap.get( n ).toString() );
        logger.info( "\n\n---------\n" + planetMap.get( see ).toString() );

        // then
        assertEquals( 2, planetMap.size() );
        assertEquals( 2, planetMap.get( n ).sizeY() );
        assertEquals( 2, planetMap.get( n ).sizeX() );
        assertEquals( "n", planetMap.get( n ).get( c00 ).getName() );
        assertEquals( "s", planetMap.get( n ).get( c01 ).getName() );
        assertEquals( "ne", planetMap.get( n ).get( c10 ).getName() );

        assertEquals( 1, planetMap.get( see ).sizeY() );
        assertEquals( 1, planetMap.get( see ).sizeX() );
        assertEquals( "see", planetMap.get( see ).get( c00 ).getName() );
    }

}

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

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Autowired
    PlanetPrinter planetPrinter;

    @Before
    public void setUp() {
        c00 = Coordinate.fromInteger( 0, 0 );
        c10 = Coordinate.fromInteger( 1, 0 );
        c20 = Coordinate.fromInteger( 2, 0 );
        c01 = Coordinate.fromInteger( 0, 1 );
        c11 = Coordinate.fromInteger( 1, 1 );
        c21 = Coordinate.fromInteger( 2, 1 );

        planetRepository.deleteAll();
        n = new Planet( UUID.randomUUID() );
        n.setSpacestation( Boolean.TRUE );
        s = new Planet( UUID.randomUUID() );
        ne = new Planet( UUID.randomUUID() );
        nee = new Planet( UUID.randomUUID() );
        se = new Planet( UUID.randomUUID() );
        see = new Planet( UUID.randomUUID() );
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
        List<TwoDimDynamicArray<Planet>> planetClusters = planetPrinter.allPlanetClusters();
        assertEquals( 1, planetClusters.size() );
        TwoDimDynamicArray<Planet> planetCluster = planetClusters.get( 0 );

        // then
        assertEquals( 2, planetCluster.sizeY() );
        assertEquals( 3, planetCluster.sizeX() );
        assertEquals( s, planetCluster.at( c01 ) );
        assertEquals( se, planetCluster.at( c11 ) );
        assertEquals( see, planetCluster.at( c21 ) );
        assertEquals( n, planetCluster.at( c00 ) );
        assertEquals( ne, planetCluster.at( c10 ) );
        assertEquals( nee, planetCluster.at( c20 ) );
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
        TwoDimDynamicArray<Planet> nCluster;
        TwoDimDynamicArray<Planet> seeCluster;

        // when
        List<TwoDimDynamicArray<Planet>> planetClusters = planetPrinter.allPlanetClusters();
        assertEquals( 2, planetClusters.size() );
        if ( planetClusters.get( 0 ).contains( n ) ) {
            nCluster = planetClusters.get( 0 );
            seeCluster = planetClusters.get( 1 );
        }
        else {
            nCluster = planetClusters.get( 1 );
            seeCluster = planetClusters.get( 0 );
        }

        // then
        assertEquals( 2, nCluster.sizeY() );
        assertEquals( 2, nCluster.sizeX() );
        assertEquals( n, nCluster.at( c00 ) );
        assertEquals( s, nCluster.at( c01 ) );
        assertEquals( ne, nCluster.at( c10 ) );

        assertEquals( 1, seeCluster.sizeY() );
        assertEquals( 1, seeCluster.sizeX() );
        assertEquals( see, seeCluster.at( c00 ) );
    }

}

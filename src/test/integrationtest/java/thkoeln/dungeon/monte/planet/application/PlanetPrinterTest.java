package thkoeln.dungeon.monte.planet.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.monte.core.domainprimitives.location.Coordinate;
import thkoeln.dungeon.monte.core.util.TwoDimDynamicArray;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.planet.domain.PlanetRepository;
import thkoeln.dungeon.monte.player.application.MapPrinter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection.*;

@SpringBootTest
public class PlanetPrinterTest {
    private Planet[] spacestations;
    private Map<Planet, Map<CompassDirection, Planet>> neighbours;
    private Coordinate c00, c11, c22;
    private UUID[] sids = new UUID[] {
        UUID.fromString( "5e1ecd4e-467e-40bc-b703-c9e295f8188b" ),
        UUID.fromString( "e76bfa6a-fc49-48de-aeef-e9f19de6f494" ),
        UUID.fromString( "1d82a593-5b86-4f03-b1f6-d322885f51a2" ),
        UUID.fromString( "467c127b-b7c3-4c5a-92ce-e753f7788ea0" ),
        UUID.fromString( "166eaf1f-a0ec-4455-80df-bf74af004d07" ),
    };
    private final UUID s3id = UUID.fromString( "bb6d96ed-40ab-41e2-b4c2-1b91479882d2" );

    @Autowired
    private PlanetPrinter planetPrinter;

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private PlanetApplicationService planetApplicationService;

    // todo  - not a legal dependency!!
    @Autowired
    private MapPrinter mapPrinter;

    @BeforeEach
    public void setup() {
        planetRepository.deleteAll();
        c00 = Coordinate.fromInteger( 0, 0 );
        c11 = Coordinate.fromInteger( 1, 1 );
        c22 = Coordinate.fromInteger( 2, 2 );

        spacestations = new Planet[5];
        neighbours = new HashMap<>();
        for ( int i = 0; i<5; i++ ) {
            spacestations[i] = Planet.spacestation( sids[i] );
        }
        // We make this 4 clusters.
        // s0 is on its own (no neighbours).
        neighbours.put( spacestations[0], new HashMap<>() );

        // s1 has west and south neighbour.
        Map<CompassDirection, Planet> s1neighbours = new HashMap<>();
        s1neighbours.put( WEST, new Planet( UUID.randomUUID() ) );
        s1neighbours.put( SOUTH, new Planet( UUID.randomUUID() ) );
        neighbours.put( spacestations[1], s1neighbours );
        spacestations[1].defineNeighbour( s1neighbours.get( WEST ), WEST );
        spacestations[1].defineNeighbour( s1neighbours.get( SOUTH ), SOUTH );

        // s2 and s3 will be neighbours. In addition, there are some more neighbours.
        Map<CompassDirection, Planet> s2neighbours = new HashMap<>();
        s2neighbours.put( NORTH, new Planet( UUID.randomUUID() ) );
        s2neighbours.put( EAST, spacestations[3] );
        neighbours.put( spacestations[2], s2neighbours );
        spacestations[2].defineNeighbour( s2neighbours.get( EAST ), EAST );
        spacestations[2].defineNeighbour( s2neighbours.get( NORTH ), NORTH );
        Map<CompassDirection, Planet> s3neighbours = new HashMap<>();
        s3neighbours.put( SOUTH, new Planet( UUID.randomUUID() ) );
        s3neighbours.put( WEST, spacestations[2] );
        s3neighbours.put( EAST, new Planet( UUID.randomUUID() ) );
        neighbours.put( spacestations[3], s3neighbours );
        spacestations[3].defineNeighbour( s3neighbours.get( EAST ), EAST );
        spacestations[3].defineNeighbour( s3neighbours.get( SOUTH ), SOUTH );
        spacestations[3].defineNeighbour( s3neighbours.get( WEST ), WEST );
        // xx      (null)  (null)
        // s2      s3      xx
        // (null)  xx      (null)

        // s4 has neighbours all around
        Map<CompassDirection, Planet> s4neighbours = new HashMap<>();
        s4neighbours.put( NORTH, new Planet( UUID.randomUUID() ) );
        s4neighbours.put( EAST, new Planet( UUID.randomUUID() ) );
        s4neighbours.put( SOUTH, new Planet( UUID.randomUUID() ) );
        s4neighbours.put( WEST, new Planet( UUID.randomUUID() ) );
        neighbours.put( spacestations[4], s4neighbours );
        spacestations[4].defineNeighbour( s4neighbours.get( NORTH ), NORTH );
        spacestations[4].defineNeighbour( s4neighbours.get( EAST ), EAST );
        spacestations[4].defineNeighbour( s4neighbours.get( SOUTH ), SOUTH );
        spacestations[4].defineNeighbour( s4neighbours.get( WEST ), WEST );

        for ( Planet spacestation : spacestations ) {
            planetRepository.save( spacestation );
            for ( Planet planet : neighbours.get( spacestation ).values() ) {
                planetRepository.save( planet );
            }
        }
    }


    @Test
    public void makeSureThisTestIsProperlySetUp() {
        // given
        // when
        List<Planet> allSpacestations = planetApplicationService.allSpaceStations();
        List<Planet> allPlanets = planetApplicationService.allPlanets();

        // then
        assertEquals( 5, allSpacestations.size() );
        assertEquals( 14, allPlanets.size() );
        assertEquals( 0, spacestations[0].allNeighbours().size() );
        assertEquals( 2, spacestations[1].allNeighbours().size() );
        assertEquals( 2, spacestations[2].allNeighbours().size() );
        assertEquals( 3, spacestations[3].allNeighbours().size() );
        assertEquals( 4, spacestations[4].allNeighbours().size() );

        // test all neighbours
        for ( Planet spacestation : spacestations ) {
            Map<CompassDirection, Planet> localNeighbours = neighbours.get( spacestation );
            for ( Map.Entry<CompassDirection, Planet> entry : localNeighbours.entrySet() ) {
                assertEquals( entry.getValue(), spacestation.getNeighbour( entry.getKey() ) );
            }
        }
    }




    private TwoDimDynamicArray<Planet> findCluster( Planet spacestation ) {
        // given
        List<TwoDimDynamicArray<Planet>> allClusters = planetPrinter.allPlanetClusters();

        // when
        assertEquals( 4, allClusters.size() );
        TwoDimDynamicArray<Planet> myCluster = null;
        for ( TwoDimDynamicArray<Planet> cluster : allClusters ) {
            if ( cluster.contains( spacestation ) ) {
                myCluster = cluster;
                break;
            }
        }
        assertNotNull( myCluster );
        return myCluster;
    }


    @Test
    public void testCluster0() {
        // given
        // when
        TwoDimDynamicArray<Planet> myCluster = findCluster( spacestations[0] );
        assertNotNull( myCluster );

        // then
        assertEquals( c00, myCluster.getMaxCoordinate() );
        assertEquals( spacestations[0], myCluster.at( 0, 0 ) );
    }


    @Test
    public void testCluster1() {
        // given
        // when
        TwoDimDynamicArray<Planet> myCluster = findCluster( spacestations[1] );
        assertNotNull( myCluster );

        // then
        assertEquals( c11, myCluster.getMaxCoordinate() );
        assertEquals( spacestations[1], myCluster.at( 1, 0 ) );
        assertEquals( neighbours.get( spacestations[1] ).get( WEST ), myCluster.at( 0, 0 ) );
        assertEquals( neighbours.get( spacestations[1] ).get( SOUTH ), myCluster.at( 1, 1 ) );
        assertNull( myCluster.at( 0, 1 ) );
    }

    @Test
    public void testCluster23() {
        // given
        // when
        TwoDimDynamicArray<Planet> myCluster = findCluster( spacestations[2] );
        assertNotNull( myCluster );
        // xx      (null)  (null)
        // s2      s3      xx
        // (null)  xx      (null)

        // then
        assertEquals( c22, myCluster.getMaxCoordinate() );
        assertEquals( neighbours.get( spacestations[2] ).get( NORTH ), myCluster.at( 0, 0 ) );
        assertNull( myCluster.at( 1, 0 ) );
        assertNull( myCluster.at( 2, 0 ) );

        assertEquals( spacestations[2], myCluster.at( 0, 1 ) );
        assertEquals( spacestations[3], myCluster.at( 1, 1 ) );
        assertEquals( neighbours.get( spacestations[3] ).get( EAST ), myCluster.at( 2, 1 ) );

        assertNull( myCluster.at( 0, 2 ) );
        assertEquals( neighbours.get( spacestations[3] ).get( SOUTH ), myCluster.at( 1, 2 ) );
        assertNull( myCluster.at( 2, 2 ) );
    }


    @Test
    public void testCluster4() {
        // given
        // when
        TwoDimDynamicArray<Planet> myCluster = findCluster( spacestations[4] );
        assertNotNull( myCluster );

        // then
        assertEquals( c22, myCluster.getMaxCoordinate() );
        assertNull( myCluster.at( 0, 0 ) );
        assertEquals( neighbours.get( spacestations[4] ).get( NORTH ), myCluster.at( 1, 0 ) );
        assertNull( myCluster.at( 2, 0 ) );
        assertEquals( neighbours.get( spacestations[4] ).get( WEST ), myCluster.at( 0, 1 ) );
        assertEquals( spacestations[4], myCluster.at( 1, 1 ) );
        assertEquals( neighbours.get( spacestations[4] ).get( EAST ), myCluster.at( 2, 1 ) );
        assertNull( myCluster.at( 0, 2 ) );
        assertEquals( neighbours.get( spacestations[4] ).get( SOUTH ), myCluster.at( 1, 2 ) );
        assertNull( myCluster.at( 2, 2 ) );
    }


    @Test
    public void testsNothingJustPrints() {
        mapPrinter.initializeOutput();
        mapPrinter.printMap();
        mapPrinter.flush();
        assertEquals( 1, 1 );
    }
}

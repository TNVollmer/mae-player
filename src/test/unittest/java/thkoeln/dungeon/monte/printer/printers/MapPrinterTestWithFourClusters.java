package thkoeln.dungeon.monte.printer.printers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.printer.finderservices.PlanetFinderService;
import thkoeln.dungeon.monte.printer.printables.PlanetPrintable;
import thkoeln.dungeon.monte.printer.util.MapCoordinate;
import thkoeln.dungeon.monte.printer.util.MapDirection;
import thkoeln.dungeon.monte.printer.util.TwoDimDynamicArray;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection.*;
import static thkoeln.dungeon.monte.printer.util.MapDirection.*;


public class MapPrinterTestWithFourClusters {
    private Planet[] spawnPoints;
    private Map<PlanetPrintable, Map<MapDirection, PlanetPrintable>> neighbours;
    private MapCoordinate c00, c11, c22;
    private UUID[] sids = new UUID[] {
        UUID.fromString( "5e1ecd4e-467e-40bc-b703-c9e295f8188b" ),
        UUID.fromString( "e76bfa6a-fc49-48de-aeef-e9f19de6f494" ),
        UUID.fromString( "1d82a593-5b86-4f03-b1f6-d322885f51a2" ),
        UUID.fromString( "467c127b-b7c3-4c5a-92ce-e753f7788ea0" ),
        UUID.fromString( "166eaf1f-a0ec-4455-80df-bf74af004d07" ),
    };
    private final UUID s3id = UUID.fromString( "bb6d96ed-40ab-41e2-b4c2-1b91479882d2" );
    private Map<MapDirection, PlanetPrintable> s1neighbours, s2neighbours, s3neighbours, s4neighbours;
    protected PlanetPrinter planetPrinter;
    protected PlanetFinderService planetFinderService;


    @BeforeEach
    public void setup() {
        c00 = MapCoordinate.fromInteger( 0, 0 );
        c11 = MapCoordinate.fromInteger( 1, 1 );
        c22 = MapCoordinate.fromInteger( 2, 2 );

        planetFinderService = new MockPlanetFinderServiceImpl();
        planetPrinter = new PlanetPrinter( planetFinderService, new ArrayList<>() );

        spawnPoints = new Planet[5];
        neighbours = new HashMap<>();
        for ( int i = 0; i<5; i++ ) {
            spawnPoints[i] = Planet.spacestation( sids[i] );
        }
        // We make this 4 clusters.
        // s0 is on its own (no neighbours).
        neighbours.put( spawnPoints[0], new HashMap<>() );

        // s1 has west and south neighbour.
        s1neighbours = new HashMap<>();
        s1neighbours.put( we, new Planet( UUID.randomUUID() ) );
        s1neighbours.put( so, new Planet( UUID.randomUUID() ) );
        neighbours.put( spawnPoints[1], s1neighbours );
        spawnPoints[1].defineNeighbour( (Planet) s1neighbours.get( we ), WEST );
        spawnPoints[1].defineNeighbour( (Planet) s1neighbours.get( so ), SOUTH );

        // s2 and s3 will be neighbours. In addition, there are some more neighbours.
        s2neighbours = new HashMap<>();
        s2neighbours.put( no, new Planet( UUID.randomUUID() ) );
        s2neighbours.put( ea, spawnPoints[3] );
        neighbours.put( spawnPoints[2], s2neighbours );
        spawnPoints[2].defineNeighbour( (Planet) s2neighbours.get( ea ), EAST );
        spawnPoints[2].defineNeighbour( (Planet) s2neighbours.get( no ), NORTH );

        s3neighbours = new HashMap<>();
        s3neighbours.put( so, new Planet( UUID.randomUUID() ) );
        s3neighbours.put( we, spawnPoints[2] );
        s3neighbours.put( ea, new Planet( UUID.randomUUID() ) );
        neighbours.put( spawnPoints[3], s3neighbours );
        spawnPoints[3].defineNeighbour( (Planet) s3neighbours.get( ea ), EAST );
        spawnPoints[3].defineNeighbour( (Planet) s3neighbours.get( so ), SOUTH );
        spawnPoints[3].defineNeighbour( (Planet) s3neighbours.get( we ), WEST );
        // xx      (null)  (null)
        // s2      s3      xx
        // (null)  xx      (null)

        // s4 has neighbours all around
        s4neighbours = new HashMap<>();
        s4neighbours.put( no, new Planet( UUID.randomUUID() ) );
        s4neighbours.put( ea, new Planet( UUID.randomUUID() ) );
        s4neighbours.put( so, new Planet( UUID.randomUUID() ) );
        s4neighbours.put( we, new Planet( UUID.randomUUID() ) );
        neighbours.put( spawnPoints[4], s4neighbours );
        spawnPoints[4].defineNeighbour( (Planet) s4neighbours.get( no ), NORTH );
        spawnPoints[4].defineNeighbour( (Planet) s4neighbours.get( ea ), EAST );
        spawnPoints[4].defineNeighbour( (Planet) s4neighbours.get( so ), SOUTH );
        spawnPoints[4].defineNeighbour( (Planet) s4neighbours.get( we ), WEST );
    }


    @Test
    public void makeSureThisTestIsProperlySetUp() {
        // given
        // when
        List<? extends PlanetPrintable> allSpacestations = planetFinderService.allSpawnPoints();
        List<? extends PlanetPrintable> allPlanets = planetFinderService.allPlanets();

        // then
        assertEquals( 5, allSpacestations.size() );
        assertEquals( 14, allPlanets.size() );
        assertEquals( 0, spawnPoints[0].allNeighbours().size() );
        assertEquals( 2, spawnPoints[1].allNeighbours().size() );
        assertEquals( 2, spawnPoints[2].allNeighbours().size() );
        assertEquals( 3, spawnPoints[3].allNeighbours().size() );
        assertEquals( 4, spawnPoints[4].allNeighbours().size() );

        // test all neighbours
        for ( Planet spawnPoint : spawnPoints) {
            Map<MapDirection, PlanetPrintable> localNeighbours = neighbours.get( spawnPoint );
            for ( Map.Entry<MapDirection, PlanetPrintable> entry : localNeighbours.entrySet() ) {
                CompassDirection compassDirection = null;
                if ( entry.getKey() == no ) compassDirection = NORTH;
                if ( entry.getKey() == ea ) compassDirection = EAST;
                if ( entry.getKey() == so ) compassDirection = SOUTH;
                if ( entry.getKey() == we ) compassDirection = WEST;
                assertEquals( entry.getValue(), spawnPoint.getNeighbour( compassDirection ) );
            }
        }
    }




    private TwoDimDynamicArray<PlanetPrintable> findCluster( Planet spacestation ) {
        // given
        List<TwoDimDynamicArray<PlanetPrintable>> allClusters = planetPrinter.allPlanetClusters();

        // when
        assertEquals( 4, allClusters.size() );
        TwoDimDynamicArray<PlanetPrintable> myCluster = null;
        for ( TwoDimDynamicArray<PlanetPrintable> cluster : allClusters ) {
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
        TwoDimDynamicArray<PlanetPrintable> myCluster = findCluster( spawnPoints[0] );
        assertNotNull( myCluster );

        // then
        assertEquals( c00, myCluster.getMaxCoordinate() );
        assertEquals( spawnPoints[0], myCluster.at( 0, 0 ) );
    }


    @Test
    public void testCluster1() {
        // given
        // when
        TwoDimDynamicArray<PlanetPrintable> myCluster = findCluster( spawnPoints[1] );
        assertNotNull( myCluster );

        // then
        assertEquals( c11, myCluster.getMaxCoordinate() );
        assertEquals( spawnPoints[1], myCluster.at( 1, 0 ) );
        assertEquals( neighbours.get( spawnPoints[1] ).get( we ), myCluster.at( 0, 0 ) );
        assertEquals( neighbours.get( spawnPoints[1] ).get( so ), myCluster.at( 1, 1 ) );
        assertNull( myCluster.at( 0, 1 ) );
    }

    @Test
    public void testCluster23() {
        // given
        // when
        TwoDimDynamicArray<PlanetPrintable> myCluster = findCluster( spawnPoints[2] );
        assertNotNull( myCluster );
        // xx      (null)  (null)
        // s2      s3      xx
        // (null)  xx      (null)

        // then
        assertEquals( c22, myCluster.getMaxCoordinate() );
        assertEquals( neighbours.get( spawnPoints[2] ).get( no ), myCluster.at( 0, 0 ) );
        assertNull( myCluster.at( 1, 0 ) );
        assertNull( myCluster.at( 2, 0 ) );

        assertEquals( spawnPoints[2], myCluster.at( 0, 1 ) );
        assertEquals( spawnPoints[3], myCluster.at( 1, 1 ) );
        assertEquals( neighbours.get( spawnPoints[3] ).get( ea ), myCluster.at( 2, 1 ) );

        assertNull( myCluster.at( 0, 2 ) );
        assertEquals( neighbours.get( spawnPoints[3] ).get( so ), myCluster.at( 1, 2 ) );
        assertNull( myCluster.at( 2, 2 ) );
    }


    @Test
    public void testCluster4() {
        // given
        // when
        TwoDimDynamicArray<PlanetPrintable> myCluster = findCluster( spawnPoints[4] );
        assertNotNull( myCluster );

        // then
        assertEquals( c22, myCluster.getMaxCoordinate() );
        assertNull( myCluster.at( 0, 0 ) );
        assertEquals( neighbours.get( spawnPoints[4] ).get( no ), myCluster.at( 1, 0 ) );
        assertNull( myCluster.at( 2, 0 ) );
        assertEquals( neighbours.get( spawnPoints[4] ).get( we ), myCluster.at( 0, 1 ) );
        assertEquals( spawnPoints[4], myCluster.at( 1, 1 ) );
        assertEquals( neighbours.get( spawnPoints[4] ).get( ea ), myCluster.at( 2, 1 ) );
        assertNull( myCluster.at( 0, 2 ) );
        assertEquals( neighbours.get( spawnPoints[4] ).get( so ), myCluster.at( 1, 2 ) );
        assertNull( myCluster.at( 2, 2 ) );
    }


    protected class MockPlanetFinderServiceImpl implements PlanetFinderService {
        @Override
        public List<? extends PlanetPrintable> allPlanets() {
            Set<PlanetPrintable> planetPrintableSet = new HashSet<>();
            planetPrintableSet.addAll( Arrays.asList( spawnPoints ) );
            planetPrintableSet.addAll( s1neighbours.values() );
            planetPrintableSet.addAll( s2neighbours.values() );
            planetPrintableSet.addAll( s3neighbours.values() );
            planetPrintableSet.addAll( s4neighbours.values() );
            return new ArrayList<>( planetPrintableSet );
        }

        @Override
        public List<? extends PlanetPrintable> allSpawnPoints() {
            return Arrays.asList( spawnPoints );
        }
    }
}

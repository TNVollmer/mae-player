package thkoeln.dungeon.monte.printer.printers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.printer.finderservices.PlanetFinderService;
import thkoeln.dungeon.monte.printer.printables.PlanetPrintable;
import thkoeln.dungeon.monte.printer.util.MapCoordinate;
import thkoeln.dungeon.monte.printer.util.TwoDimDynamicArray;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection.*;



public class MapPrinterTest {
    protected Planet[][] planetArray = new Planet[3][3];
    protected Planet n, s, ne, se, nee, see;
    protected MapCoordinate c01, c11, c21, c00, c10, c20;
    protected PlanetPrinter planetPrinter_OneIsland, planetPrinter_TwoIslands;

    private Planet[] spacestations;
    private Map<Planet, Map<CompassDirection, Planet>> neighbours;
    private UUID[] sids = new UUID[] {
            UUID.fromString( "5e1ecd4e-467e-40bc-b703-c9e295f8188b" ),
            UUID.fromString( "e76bfa6a-fc49-48de-aeef-e9f19de6f494" ),
            UUID.fromString( "1d82a593-5b86-4f03-b1f6-d322885f51a2" ),
            UUID.fromString( "467c127b-b7c3-4c5a-92ce-e753f7788ea0" ),
            UUID.fromString( "166eaf1f-a0ec-4455-80df-bf74af004d07" ),
    };
    private final UUID s3id = UUID.fromString( "bb6d96ed-40ab-41e2-b4c2-1b91479882d2" );


    @BeforeEach
    public void setup() {
        for( int i = 0; i<=2; i++ ) {
            for (int j = 0; j <= 2; j++) {
                planetArray[i][j] = new Planet();
            }
        }

        c00 = MapCoordinate.fromInteger( 0, 0 );
        c10 = MapCoordinate.fromInteger( 1, 0 );
        c20 = MapCoordinate.fromInteger( 2, 0 );
        c01 = MapCoordinate.fromInteger( 0, 1 );
        c11 = MapCoordinate.fromInteger( 1, 1 );
        c21 = MapCoordinate.fromInteger( 2, 1 );

        n = new Planet( UUID.randomUUID() );
        n.setSpawnPoint( Boolean.TRUE );
        s = new Planet( UUID.randomUUID() );
        ne = new Planet( UUID.randomUUID() );
        nee = new Planet( UUID.randomUUID() );
        se = new Planet( UUID.randomUUID() );
        see = new Planet( UUID.randomUUID() );
        see.setSpawnPoint( true );

        planetPrinter_OneIsland = new PlanetPrinter( new MockPlanetFinderServiceImpl_OneIsland(), new ArrayList<>() );
        planetPrinter_TwoIslands = new PlanetPrinter( new MockPlanetFinderServiceImpl_TwoIslands(), new ArrayList<>() );
    }


    @Test
    public void testConstructLocalCluster() {
        // given
        Planet spawnPoint = new Planet( UUID.randomUUID() );
        Planet northNeighbour = new Planet( UUID.randomUUID() );
        Planet eastNeighbour = new Planet( UUID.randomUUID() );
        Planet southNeighbour = new Planet( UUID.randomUUID() );
        //  no   NULL
        //  ss   ea
        //  so   NULL

        MapCoordinate c00 = MapCoordinate.fromInteger( 0, 0 );
        MapCoordinate c01 = MapCoordinate.fromInteger( 0, 1 );
        MapCoordinate c02 = MapCoordinate.fromInteger( 0, 2 );
        MapCoordinate c10 = MapCoordinate.fromInteger( 1, 0 );
        MapCoordinate c11 = MapCoordinate.fromInteger( 1, 1 );
        MapCoordinate c12 = MapCoordinate.fromInteger( 1, 2 );

        // when
        spawnPoint.defineNeighbour( northNeighbour, NORTH );
        spawnPoint.defineNeighbour( eastNeighbour, EAST );
        spawnPoint.defineNeighbour( southNeighbour, SOUTH );
        PlanetMapConstructor planetMapConstructor = new PlanetMapConstructor( spawnPoint );
        TwoDimDynamicArray<PlanetPrintable> planetCluster = planetMapConstructor.constructLocalClusterMap();

        // then
        assertEquals( c12, planetCluster.getMaxCoordinate() );
        assertEquals( northNeighbour, planetCluster.at( c00 ) );
        assertEquals( spawnPoint, planetCluster.at( c01 ) );
        assertEquals( southNeighbour, planetCluster.at( c02 ) );
        assertNull( planetCluster.at( c10 ) );
        assertEquals( eastNeighbour, planetCluster.at( c11 ) );
        assertNull( planetCluster.at( c12 ) );
    }




    @Test
    public void testOneIsland() {
        // given
        n.defineNeighbour( s, CompassDirection.SOUTH );
        n.defineNeighbour( ne, CompassDirection.EAST );
        ne.defineNeighbour( nee, CompassDirection.EAST );
        s.defineNeighbour( se, CompassDirection.EAST );
        se.defineNeighbour( see, CompassDirection.EAST );
        //    N--NE--NEE
        //    |
        //    S--SE--SEE

        // when
        List<TwoDimDynamicArray<PlanetPrintable>> planetClusters = planetPrinter_OneIsland.allPlanetClusters();
        assertEquals( 1, planetClusters.size() );
        TwoDimDynamicArray<PlanetPrintable> planetCluster = planetClusters.get( 0 );

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


    @org.junit.Test
    public void testTwoIslands() {
        // given
        n.defineNeighbour( s, CompassDirection.SOUTH );
        n.defineNeighbour( ne, CompassDirection.EAST );
        //    N--NE
        //    |
        //    S        SEE
        TwoDimDynamicArray<PlanetPrintable> nCluster;
        TwoDimDynamicArray<PlanetPrintable> seeCluster;

        // when
        List<TwoDimDynamicArray<PlanetPrintable>> planetClusters = planetPrinter_TwoIslands.allPlanetClusters();
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


    protected class MockPlanetFinderServiceImpl_OneIsland implements PlanetFinderService {
        @Override
        public List<? extends PlanetPrintable> allPlanets() {
            return Arrays.asList( n, s, ne, se, nee, see );
        }

        @Override
        public List<? extends PlanetPrintable> allSpawnPoints() {
            return Arrays.asList( n );
        }
    }


    protected class MockPlanetFinderServiceImpl_TwoIslands implements PlanetFinderService {
        @Override
        public List<? extends PlanetPrintable> allPlanets() {
            return Arrays.asList( n, s, ne, se, nee, see );
        }

        @Override
        public List<? extends PlanetPrintable> allSpawnPoints() {
            return Arrays.asList( n, see );
        }
    }
}

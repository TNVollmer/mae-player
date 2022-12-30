package thkoeln.dungeon.monte.planet.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.monte.domainprimitives.CompassDirection;
import thkoeln.dungeon.monte.domainprimitives.Coordinate;
import thkoeln.dungeon.monte.domainprimitives.TwoDimDynamicArray;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.dungeon.monte.domainprimitives.CompassDirection.*;

public class PlanetTest {
    private Planet[][] planetArray= new Planet[3][3];

    @BeforeEach
    public void setup() {
        for( int i = 0; i<=2; i++ ) {
            for (int j = 0; j <= 2; j++) {
                planetArray[i][j] = new Planet();
            }
        }
    }


    @Test
    public void testNeighbouringSetterGetterViaReflection() throws Exception {
        // given
        Planet planet = new Planet();

        // when
        Method getter = planet.neighbouringGetter( CompassDirection.SOUTH);
        Method setter = planet.neighbouringSetter( CompassDirection.WEST);

        // then
        assertEquals( "getSouthNeighbour", getter.getName() );
        assertEquals( "setWestNeighbour", setter.getName() );
    }


    @Test
    public void testEstablishNeighbouringRelationship() {
        // given
        // when
        planetArray[0][1].defineNeighbour( planetArray[1][1], CompassDirection.EAST );
        planetArray[0][1].defineNeighbour( planetArray[0][2], NORTH );

        // then
        assertEquals( planetArray[1][1], planetArray[0][1].getEastNeighbour() );
        assertEquals( planetArray[0][1], planetArray[1][1].getWestNeighbour() );
        assertEquals( planetArray[0][2], planetArray[0][1].getNorthNeighbour() );
        assertEquals( planetArray[0][1], planetArray[0][2].getSouthNeighbour() );
    }


    @Test
    public void testClosingCycles() {
        // given
        Planet n = new Planet();
        Planet s = new Planet();
        Planet ne = new Planet();
        Planet nee = new Planet();
        Planet se = new Planet();
        Planet see = new Planet();

        // when
        n.defineNeighbour( s, CompassDirection.SOUTH );
        //    N
        //    |
        //    S

        n.defineNeighbour( ne, CompassDirection.EAST );
        //    N--NE
        //    |
        //    S

        ne.defineNeighbour( nee, CompassDirection.EAST );
        //    N--NE--NEE
        //    |
        //    S

        s.defineNeighbour( se, CompassDirection.EAST );
        //    N--NE--NEE
        //    |
        //    S--SE

        se.defineNeighbour( see, CompassDirection.EAST );
        //    N--NE--NEE
        //    |
        //    S--SE--SEE

        // then
        assertEquals( nee,
                s.getNorthNeighbour().
                    getEastNeighbour().
                        getSouthNeighbour().
                            getEastNeighbour().
                                getNorthNeighbour() );
    }


    @Test
    public void testHasNeighbours() {
        // given
        Planet p1 = new Planet();
        Planet p2 = new Planet();
        Planet p3 = new Planet();

        // when
        p2.defineNeighbour( p3, CompassDirection.SOUTH );

        // then
        assertTrue( p2.hasNeighbours() );
        assertTrue( p3.hasNeighbours() );
        assertFalse( p1.hasNeighbours() );
    }

    @Test
    public void testConstructLocalCluster() {
        // given
        Planet spacestation = new Planet( UUID.randomUUID() );
        Planet northNeighbour = new Planet( UUID.randomUUID() );
        Planet eastNeighbour = new Planet( UUID.randomUUID() );
        Planet southNeighbour = new Planet( UUID.randomUUID() );
        //  no   NULL
        //  ss   ea
        //  so   NULL

        Coordinate c00 = Coordinate.fromInteger( 0, 0 );
        Coordinate c01 = Coordinate.fromInteger( 0, 1 );
        Coordinate c02 = Coordinate.fromInteger( 0, 2 );
        Coordinate c10 = Coordinate.fromInteger( 1, 0 );
        Coordinate c11 = Coordinate.fromInteger( 1, 1 );
        Coordinate c12 = Coordinate.fromInteger( 1, 2 );

        // when
        spacestation.defineNeighbour( northNeighbour, NORTH );
        spacestation.defineNeighbour( eastNeighbour, EAST );
        spacestation.defineNeighbour( southNeighbour, SOUTH );
        TwoDimDynamicArray<Planet> planetCluster = spacestation.constructLocalClusterMap();

        // then
        assertEquals( c12, planetCluster.getMaxCoordinate() );
        assertEquals( northNeighbour, planetCluster.at( c00 ) );
        assertEquals( spacestation, planetCluster.at( c01 ) );
        assertEquals( southNeighbour, planetCluster.at( c02 ) );
        assertNull( planetCluster.at( c10 ) );
        assertEquals( eastNeighbour, planetCluster.at( c11 ) );
        assertNull( planetCluster.at( c12 ) );
    }

}

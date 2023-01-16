package thkoeln.dungeon.monte.planet.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.monte.printer.util.MapCoordinate;
import thkoeln.dungeon.monte.printer.util.TwoDimDynamicArray;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection.*;

public class PlanetTest {
    private Planet[][] planetArray = new Planet[3][3];

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
        planetArray[0][1].defineNeighbour( planetArray[1][1], EAST );
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
        Planet p1 = new Planet( UUID.randomUUID() );
        Planet p2 = new Planet( UUID.randomUUID() );
        Planet p3 = new Planet( UUID.randomUUID() );

        // when
        p2.defineNeighbour( p3, CompassDirection.SOUTH );

        // then
        assertTrue( p2.hasNeighbours() );
        assertTrue( p3.hasNeighbours() );
        assertFalse( p1.hasNeighbours() );
    }


    @Test
    public void testEmptyNeighbourSlots() {
        // given
        Planet p1 = new Planet( UUID.randomUUID() );
        Planet p2 = new Planet( UUID.randomUUID() );
        Planet p3 = new Planet( UUID.randomUUID() );

        // when
        p2.defineNeighbour( p3, SOUTH );
        p1.defineNeighbour( p2, EAST );
        p1.fillEmptyNeighbourSlotsWithBlackHoles();
        p2.fillEmptyNeighbourSlotsWithBlackHoles();
        p3.fillEmptyNeighbourSlotsWithBlackHoles();

        // then
        assertTrue( p1.getNorthNeighbour().isBlackHole() );
        assertFalse( p1.getEastNeighbour().isBlackHole() );
        assertTrue( p1.getSouthNeighbour().isBlackHole() );
        assertTrue( p1.getWestNeighbour().isBlackHole() );

        assertTrue( p2.getNorthNeighbour().isBlackHole() );
        assertTrue( p2.getEastNeighbour().isBlackHole() );
        assertFalse( p2.getSouthNeighbour().isBlackHole() );
        assertFalse( p2.getWestNeighbour().isBlackHole() );

        assertFalse( p3.getNorthNeighbour().isBlackHole() );
        assertTrue( p3.getEastNeighbour().isBlackHole() );
        assertTrue( p3.getSouthNeighbour().isBlackHole() );
        assertTrue( p3.getWestNeighbour().isBlackHole() );
    }

}

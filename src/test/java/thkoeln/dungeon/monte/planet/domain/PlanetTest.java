package thkoeln.dungeon.monte.planet.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection;

import java.lang.reflect.Method;
import java.util.UUID;
import thkoeln.dungeon.monte.planet.domain.Planet;

import static java.lang.Boolean.TRUE;
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
        Method getter = planet.directionalGetter( CompassDirection.SOUTH, "Neighbour" );
        Method setter = planet.directionalSetter( CompassDirection.WEST, "Neighbour", Planet.class );

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
                            getEastNeighbour() );
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
        p1.defineEmptyNeighbourSlotsAsHardBorders();
        p2.defineEmptyNeighbourSlotsAsHardBorders();
        p3.defineEmptyNeighbourSlotsAsHardBorders();

        // then
        assertTrue( p1.getNorthHardBorder() );
        assertNotEquals( TRUE, p1.getEastHardBorder() );
        assertTrue( p1.getSouthHardBorder() );
        assertTrue( p1.getWestHardBorder() );

        assertTrue( p2.getNorthHardBorder() );
        assertTrue( p2.getEastHardBorder() );
        assertNotEquals( TRUE, p2.getSouthHardBorder() );
        assertNotEquals( TRUE, p2.getWestHardBorder() );

        assertNotEquals( TRUE, p3.getNorthHardBorder() );
        assertTrue( p3.getEastHardBorder() );
        assertTrue( p3.getSouthHardBorder() );
        assertTrue( p3.getWestHardBorder() );
    }

}

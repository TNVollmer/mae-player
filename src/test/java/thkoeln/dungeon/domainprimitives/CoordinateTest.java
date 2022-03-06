package thkoeln.dungeon.domainprimitives;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoordinateTest {
    private Coordinate c00, c23, c24, c22, c33, c13, c_10;

    @BeforeEach
    public void setUp() {
        c00 = Coordinate.fromInteger( 0, 0 );
        c23 = Coordinate.fromInteger( 2, 3 );
        c24 = Coordinate.fromInteger( 2, 4 );
        c22 = Coordinate.fromInteger( 2, 2 );
        c33 = Coordinate.fromInteger( 3, 3 );
        c13 = Coordinate.fromInteger( 1, 3 );
        c_10 = Coordinate.fromInteger( -1, 0 );
    }

    @Test
    public void testNeighbouringMethod() {
        assertEquals( c_10, c00.neighbourCoordinate( CompassDirection.WEST ) );
        assertEquals( c24, c23.neighbourCoordinate( CompassDirection.NORTH ) );
        assertEquals( c13, c23.neighbourCoordinate( CompassDirection.WEST ) );
        assertEquals( c22, c23.neighbourCoordinate( CompassDirection.SOUTH ) );
        assertEquals( c33, c23.neighbourCoordinate( CompassDirection.EAST ) );
    }

    @Test
    public void testValidation() {
        assertThrows( CoordinateException.class, () -> {
            c23.neighbourCoordinate( null );
        });
    }

}

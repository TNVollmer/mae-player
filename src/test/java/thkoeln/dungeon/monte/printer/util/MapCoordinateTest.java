package thkoeln.dungeon.monte.printer.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.monte.printer.util.MapCoordinate;
import thkoeln.dungeon.monte.printer.util.PrinterException;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.dungeon.monte.printer.util.MapDirection.*;

public class MapCoordinateTest {
    private MapCoordinate c00, c23, c24, c22, c33, c13;

    @BeforeEach
    public void setUp() {
        c00 = MapCoordinate.fromInteger( 0, 0 );
        c23 = MapCoordinate.fromInteger( 2, 3 );
        c24 = MapCoordinate.fromInteger( 2, 4 );
        c22 = MapCoordinate.fromInteger( 2, 2 );
        c33 = MapCoordinate.fromInteger( 3, 3 );
        c13 = MapCoordinate.fromInteger( 1, 3 );
    }

    @Test
    public void testNeighbouringMethod() {
        assertEquals( c22, c23.neighbourCoordinate( no ) );
        assertEquals( c13, c23.neighbourCoordinate( we ) );
        assertEquals( c24, c23.neighbourCoordinate( so ) );
        assertEquals( c33, c23.neighbourCoordinate( ea ) );
    }

    @Test
    public void testValidation() {
        assertThrows( PrinterException.class, () -> {
            c23.neighbourCoordinate( null );
        });
        assertThrows( PrinterException.class, () -> {
            MapCoordinate.fromInteger( -1, 2 );
        });
        assertThrows( PrinterException.class, () -> {
            MapCoordinate.fromInteger( 1, -2 );
        });
    }

    @Test
    public void testLargerAndSmallerAndEuals() {
        assertEquals( c00, MapCoordinate.initialCoordinate() );
        assertTrue( c00.isSmallerEqualsThan( MapCoordinate.initialCoordinate() ) );
        assertFalse( c00.isLargerThan( MapCoordinate.initialCoordinate() ) );
        assertTrue( c23.isSmallerEqualsThan( c24 ) );
        assertTrue( c24.isLargerThan( c23 ) );
        assertTrue( c22.isSmallerEqualsThan( c33 ) );
        assertTrue( c13.isLargerThan( c22 ) );
    }

}

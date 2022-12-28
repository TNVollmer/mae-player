package thkoeln.dungeon.monte.domainprimitives;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TwoDimDynamicArrayTest {

    @Test
    public void testCreationValidation() {
        assertThrows( DomainPrimitiveException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( -1, 5 );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, -12 );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 0, 5 );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, 0 );
        });
    }

    @Test
    public void testCreation() {
        // given
        // when
        assertThrows( DomainPrimitiveException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( -1, 5 );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, -1 );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 0, 0 );
        });
        TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, 5 );
        TwoDimDynamicArray<String> arr1 = new TwoDimDynamicArray( 1, 1 );

        // then
        assertEquals( 3, arr.sizeX() );
        assertEquals( 5, arr.sizeY() );
        assertEquals( Coordinate.fromInteger( 2, 4 ), arr.getMaxCoordinate() );
        assertEquals( 1, arr1.sizeX() );
        assertEquals( 1, arr1.sizeY() );
        assertEquals( Coordinate.fromInteger( 0, 0 ), arr1.getMaxCoordinate() );
    }

    @Test
    public void testPutGet() {
        // given
        TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, 5 );

        // when
        assertThrows( DomainPrimitiveException.class, () -> {
            arr.put( null, "hallo" );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            arr.put( Coordinate.fromInteger( 2, 6 ), "hallo" );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            arr.put( Coordinate.fromInteger( 3, 5 ), "hallo" );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            arr.put( Coordinate.fromInteger( 3, 4 ), "hallo" );
        });
        arr.put( Coordinate.fromInteger( 1, 2 ), "yeah" );

        // then
        assertThrows( DomainPrimitiveException.class, () -> {
            arr.at( null );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            arr.at( Coordinate.fromInteger( 2, 6 ) );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            arr.at( Coordinate.fromInteger( 3, 5 ) );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            arr.at( Coordinate.fromInteger( 3, 4 ) );
        });
        assertEquals( "yeah", arr.at( Coordinate.fromInteger( 1, 2 ) ) );
        assertNull( arr.at( Coordinate.fromInteger( 1, 1 ) ) );
        assertNull( arr.at( Coordinate.fromInteger( 0, 2 ) ) );
    }

    @Test
    public void testAddColOrRow() {
        // given
        TwoDimDynamicArray<String> arr = new TwoDimDynamicArray(3, 3);
        arr.put( Coordinate.fromInteger( 0, 1 ), "Penny");
        arr.put( Coordinate.fromInteger( 1, 1 ), "Sheldon");
        arr.put( Coordinate.fromInteger( 2, 1 ), "Cooper");
        arr.put( Coordinate.fromInteger( 1, 2 ), "Leonard");
        for (int y = 0; y < arr.sizeY(); y++) {
            for (int x = 0; x < arr.sizeX(); x++) {
                System.out.print( arr.at( Coordinate.fromInteger( x, y ) ) + " | ");
            }
            System.out.println( "" );
        }
        System.out.println( "--------------------------" );
        // |       |         |        |
        // | Penny | Sheldon | Cooper |
        // |       | Leonard |        |

        // when
        assertThrows(DomainPrimitiveException.class, () -> {
            arr.addRowAt(-1);
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            arr.addRowAt(5);
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            arr.addColumnAt(-1);
        });
        assertThrows(DomainPrimitiveException.class, () -> {
            arr.addColumnAt(4);
        });
        arr.addRowAt(2);
        arr.addColumnAt(1);
        arr.addRowAt(4);
        // |       |   |          |        |
        // | Penny |   |  Sheldon | Cooper |
        // |       |   |          |        |
        // |       |   |  Leonard |        |
        // |       |   |          |        |

        // then
        for (int y = 0; y < arr.sizeY(); y++) {
            for (int x = 0; x < arr.sizeX(); x++) {
                System.out.print( arr.at( Coordinate.fromInteger( x, y ) ) + " | " );
            }
            System.out.println( "" );
        }
        assertEquals( "Penny", arr.at( Coordinate.fromInteger(0, 1 ) ) );
        assertNull( arr.at( Coordinate.fromInteger(1, 1) ) );
        assertNull( arr.at( Coordinate.fromInteger(1, 2) ) );
        assertEquals( "Sheldon", arr.at( Coordinate.fromInteger(2, 1 ) ) );
        assertEquals( "Cooper", arr.at( Coordinate.fromInteger(3, 1 ) ) );
        assertEquals( "Leonard", arr.at( Coordinate.fromInteger(2, 3 ) ) );
        assertNull( arr.at( Coordinate.fromInteger(2, 4) ) );
    }


    @Test
    public void testEnhanceIfNeeded_maxPoint() {
        // given
        TwoDimDynamicArray<String> arrNorth = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrEast = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrSouth = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrWest = new TwoDimDynamicArray(2, 3);

        // when
        Coordinate c12 = Coordinate.fromInteger(1, 2);
        arrNorth.enhanceIfNeededAt( c12, CompassDirection.NORTH );
        arrEast.enhanceIfNeededAt( c12, CompassDirection.EAST );
        arrSouth.enhanceIfNeededAt( c12, CompassDirection.SOUTH );
        arrWest.enhanceIfNeededAt( c12, CompassDirection.WEST );

        // then
        assertEquals(2, arrNorth.sizeX());
        assertEquals(3, arrNorth.sizeY());
        assertEquals(3, arrEast.sizeX());
        assertEquals(3, arrEast.sizeY());
        assertEquals(2, arrSouth.sizeX());
        assertEquals(4, arrSouth.sizeY());
        assertEquals(2, arrWest.sizeX());
        assertEquals(3, arrWest.sizeY());
    }

    @Test
    public void testEnhanceIfNeeded_00() {
        // given
        TwoDimDynamicArray<String> arrNorth = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrEast = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrSouth = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrWest = new TwoDimDynamicArray(2, 3);

        // when
        Coordinate c00 = Coordinate.fromInteger(0, 0 );
        arrNorth.enhanceIfNeededAt( c00, CompassDirection.NORTH );
        arrEast.enhanceIfNeededAt( c00, CompassDirection.EAST );
        arrSouth.enhanceIfNeededAt( c00, CompassDirection.SOUTH );
        arrWest.enhanceIfNeededAt( c00, CompassDirection.WEST );

        // then
        assertEquals( 2, arrNorth.sizeX() );
        assertEquals( 4, arrNorth.sizeY() );
        assertEquals( 2, arrEast.sizeX() );
        assertEquals( 3, arrEast.sizeY() );
        assertEquals( 2, arrSouth.sizeX() );
        assertEquals( 3, arrSouth.sizeY() );
        assertEquals( 3, arrWest.sizeX() );
        assertEquals( 3, arrWest.sizeY() );
    }
}

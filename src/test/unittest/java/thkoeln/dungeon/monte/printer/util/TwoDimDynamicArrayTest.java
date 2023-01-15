package thkoeln.dungeon.monte.printer.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.monte.core.util.UtilException;
import static thkoeln.dungeon.monte.printer.util.MapDirection.*;
import static org.junit.jupiter.api.Assertions.*;

public class TwoDimDynamicArrayTest {
    private TwoDimDynamicArray<String> bigBangCharacters;
    private MapCoordinate c00, c01, c02, c11, c12, c21, c23, c24, c26, c31, c34, c35;

    @BeforeEach
    public void setUp() {
        c01 = MapCoordinate.fromInteger( 0, 1 );
        c02 = MapCoordinate.fromInteger( 0, 2 );
        c11 = MapCoordinate.fromInteger( 1, 1 );
        c12 = MapCoordinate.fromInteger( 1, 2 );
        c00 = MapCoordinate.fromInteger( 0, 0 );
        c21 = MapCoordinate.fromInteger( 2, 1 );
        c23 = MapCoordinate.fromInteger( 2, 3 );
        c24 = MapCoordinate.fromInteger( 2, 4 );
        c26 = MapCoordinate.fromInteger( 2, 6 );
        c31 = MapCoordinate.fromInteger( 3, 1 );
        c34 = MapCoordinate.fromInteger( 3, 4 );
        c35 = MapCoordinate.fromInteger( 3, 5 );

        bigBangCharacters = new TwoDimDynamicArray(3, 3);
        bigBangCharacters.put( c01, "Penny");
        bigBangCharacters.put( c11 , "Sheldon");
        bigBangCharacters.put( c21 , "Cooper");
        bigBangCharacters.put( c12, "Leonard");
        for (int y = 0; y < bigBangCharacters.sizeY(); y++) {
            for (int x = 0; x < bigBangCharacters.sizeX(); x++) {
                System.out.print( bigBangCharacters.at( MapCoordinate.fromInteger( x, y ) ) + " | ");
            }
            System.out.println( "" );
        }
        System.out.println( "--------------------------" );
        // |       |         |        |
        // | Penny | Sheldon | Cooper |
        // |       | Leonard |        |


    }


    @Test
    public void testCreationValidation() {
        assertThrows( UtilException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( -1, 5 );
        });
        assertThrows( UtilException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, -12 );
        });
        assertThrows( UtilException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 0, 5 );
        });
        assertThrows( UtilException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, 0 );
        });
    }

    @Test
    public void testCreation() {
        // given
        // when
        assertThrows( UtilException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( -1, 5 );
        });
        assertThrows( UtilException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, -1 );
        });
        assertThrows( UtilException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 0, 0 );
        });
        TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, 5 );
        TwoDimDynamicArray<String> arr1 = new TwoDimDynamicArray( 1, 1 );

        // then
        assertEquals( 3, arr.sizeX() );
        assertEquals( 5, arr.sizeY() );
        assertEquals( c24, arr.getMaxCoordinate() );
        assertEquals( 1, arr1.sizeX() );
        assertEquals( 1, arr1.sizeY() );
        assertEquals( c00, arr1.getMaxCoordinate() );
    }

    @Test
    public void testPutGet() {
        // given
        TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, 5 );

        // when
        assertThrows( UtilException.class, () -> {
            arr.put( null, "hallo" );
        });
        assertThrows( UtilException.class, () -> {
            arr.put( c26, "hallo" );
        });
        assertThrows( UtilException.class, () -> {
            arr.put( c35, "hallo" );
        });
        assertThrows( UtilException.class, () -> {
            arr.put( c34, "hallo" );
        });
        arr.put( c12, "yeah" );

        // then
        assertThrows( UtilException.class, () -> {
            arr.at( null );
        });
        assertThrows( UtilException.class, () -> {
            arr.at( c26 );
        });
        assertThrows( UtilException.class, () -> {
            arr.at( c35 );
        });
        assertThrows( UtilException.class, () -> {
            arr.at( c34 );
        });
        assertEquals( "yeah", arr.at( c12 ) );
        assertNull( arr.at( c11 ) );
        assertNull( arr.at( c02 ) );
    }

    @Test
    public void testAddColOrRow() {
        // given
        // when
        assertThrows(UtilException.class, () -> {
            bigBangCharacters.addRowAt(-1);
        });
        assertThrows(UtilException.class, () -> {
            bigBangCharacters.addRowAt(5);
        });
        assertThrows(UtilException.class, () -> {
            bigBangCharacters.addColumnAt(-1);
        });
        assertThrows(UtilException.class, () -> {
            bigBangCharacters.addColumnAt(4);
        });
        bigBangCharacters.addRowAt(2 );
        bigBangCharacters.addColumnAt(1 );
        bigBangCharacters.addRowAt(4 );
        // |       |   |          |        |
        // | Penny |   |  Sheldon | Cooper |
        // |       |   |          |        |
        // |       |   |  Leonard |        |
        // |       |   |          |        |

        // then
        for (int y = 0; y < bigBangCharacters.sizeY(); y++) {
            for (int x = 0; x < bigBangCharacters.sizeX(); x++) {
                System.out.print( bigBangCharacters.at( MapCoordinate.fromInteger( x, y ) ) + " | " );
            }
            System.out.println( "" );
        }
        assertEquals( "Penny", bigBangCharacters.at( c01 ) );
        assertNull( bigBangCharacters.at( c11 ) );
        assertNull( bigBangCharacters.at( c12 ) );
        assertEquals( "Sheldon", bigBangCharacters.at( c21 ) );
        assertEquals( "Cooper", bigBangCharacters.at( c31 ) );
        assertEquals( "Leonard", bigBangCharacters.at( c23 ) );
        assertNull( bigBangCharacters.at( c24 ) );
    }


    @Test
    public void testEnhanceIfNeeded_maxPoint() {
        // given
        TwoDimDynamicArray<String> arrNorth = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrEast = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrSouth = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrWest = new TwoDimDynamicArray(2, 3);

        // when
        arrNorth.enhanceIfNeededAt( c12, no );
        arrEast.enhanceIfNeededAt( c12, ea );
        arrSouth.enhanceIfNeededAt( c12, so );
        arrWest.enhanceIfNeededAt( c12, we );

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
        arrNorth.enhanceIfNeededAt( c00, no );
        arrEast.enhanceIfNeededAt( c00, ea );
        arrSouth.enhanceIfNeededAt( c00, so );
        arrWest.enhanceIfNeededAt( c00, we );

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

    @Test
    public void testContainsAndFind() {
        // given
        // |       |         |        |
        // | Penny | Sheldon | Cooper |
        // |       | Leonard |        |

        // when
        // then
        assertTrue( bigBangCharacters.contains( "Penny" ) );
        assertTrue( bigBangCharacters.contains( "Leonard" ) );
        assertFalse( bigBangCharacters.contains( "Kripke" ) );
        assertEquals( c01, bigBangCharacters.find( "Penny" ) );
        assertEquals( c21, bigBangCharacters.find( "Cooper" ) );
        assertEquals( c12, bigBangCharacters.find( "Leonard" ) );
        assertNull( bigBangCharacters.find( "Kripke" ) );
    }
}

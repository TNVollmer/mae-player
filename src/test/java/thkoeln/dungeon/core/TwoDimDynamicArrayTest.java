package thkoeln.dungeon.core;

import org.junit.Assert;
import org.junit.Test;
import thkoeln.dungeon.domainprimitives.Moneten;
import thkoeln.dungeon.domainprimitives.MonetenException;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertNull;

public class TwoDimDynamicArrayTest {

    @Test
    public void testCreationValidation() {
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( -1, 5 );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, -12 );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 0, 5 );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, 0 );
        });
    }

    @Test
    public void testCreation() {
        // given
        // when
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( -1, 5 );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, -1 );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 0, 0 );
        });
        TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, 5 );
        TwoDimDynamicArray<String> arr1 = new TwoDimDynamicArray( 1, 1 );

        // then
        assertEquals( 3, arr.sizeX() );
        assertEquals( 5, arr.sizeY() );
        assertEquals( 1, arr1.sizeX() );
        assertEquals( 1, arr1.sizeY() );
    }

    @Test
    public void testPutGet() {
        // given
        TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, 5 );

        // when
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.put( -1, 2, "hallo" );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.put( 3, 2, "hallo" );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.put( 2, -2, "hallo" );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.put( 1, 6, "hallo" );
        });
        arr.put( 1,2, "yeah" );

        // then
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.get( -1, 2 );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.get( 3, 2 );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.get( 2, -2 );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.get( 1, 6 );
        });
        assertEquals( "yeah", arr.get( 1, 2 ) );
        assertNull( arr.get( 1, 1) );
        assertNull( arr.get( 0, 2) );
    }

    @Test
    public void testAddColOrRow() {
        // given
        TwoDimDynamicArray<String> arr = new TwoDimDynamicArray(3, 3);
        arr.put(0, 1, "Penny");
        arr.put(1, 1, "Sheldon");
        arr.put(2, 1, "Cooper");
        arr.put(1, 2, "Leonard");
        for (int y = 0; y < arr.sizeY(); y++) {
            for (int x = 0; x < arr.sizeX(); x++) {
                System.out.print(arr.get(x, y) + " | ");
            }
            System.out.println( "" );
        }
        System.out.println( "--------------------------" );
        // |       |         |        |
        // | Penny | Sheldon | Cooper |
        // |       | Leonard |        |

        // when
        assertThrows(TwoDimDynamicArrayException.class, () -> {
            arr.addRowAt(-1);
        });
        assertThrows(TwoDimDynamicArrayException.class, () -> {
            arr.addRowAt(5);
        });
        assertThrows(TwoDimDynamicArrayException.class, () -> {
            arr.addColumnAt(-1);
        });
        assertThrows(TwoDimDynamicArrayException.class, () -> {
            arr.addColumnAt(4);
        });
        arr.addRowAt(2);
        arr.addColumnAt(1);
        // |       |   |          |        |
        // | Penny |   |  Sheldon | Cooper |
        // |       |   |          |        |
        // |       |   |  Leonard |        |

        // then
        for (int y = 0; y < arr.sizeY(); y++) {
            for (int x = 0; x < arr.sizeX(); x++) {
                System.out.print(arr.get(x, y) + " | ");
            }
            System.out.println( "" );
        }
        assertEquals( "Penny", arr.get( 0, 1 ) );
        assertNull( arr.get( 1, 1) );
        assertNull( arr.get( 1, 2) );
        assertEquals( "Sheldon", arr.get( 2, 1 ) );
        assertEquals( "Cooper", arr.get( 3, 1 ) );
        assertEquals( "Leonard", arr.get( 2, 3 ) );
    }


}

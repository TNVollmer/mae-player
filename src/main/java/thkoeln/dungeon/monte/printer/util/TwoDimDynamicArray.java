package thkoeln.dungeon.monte.printer.util;

import static thkoeln.dungeon.monte.printer.util.MapDirection.*;

import java.util.ArrayList;

/**
 * 2dim Array als a list of rows of equal length, able to grow dynamically.
 * To my great surprise I haven't found a class "off the shelf" that can do this ... probably I didn't look
 * well enough, but so be it - here is a (unit tested) own implementation.
 *
 * For convenience (compatibility with our reading directions), the array is seen as growing from top to bottom
 * and from left to right. I.e. it looks like this:
 *
 *        x---->
 *         0  1  2  3  4
 *   y   0
 *   |   1
 *   |   2
 *   V   3
 *
 * (c) Stefan Bente 2022
 * @param <T>
 */
public class TwoDimDynamicArray<T> {
    private ArrayList<ArrayList<T>> array = new ArrayList<ArrayList<T>>();

    public TwoDimDynamicArray( int maxX, int maxY ) {
        if ( maxX <= 0 || maxY <= 0 )
            throw new PrinterException( "maxX / maxY must be > 0: " + maxX + ", " + maxY );
        for ( int y = 0; y < maxY; y++ ) {
            array.add( createNullRow( maxX )  );
        }
    }

    public TwoDimDynamicArray( MapCoordinate max ) {
        this( max.getX()+1 , max.getY()+1 );
    }

    public TwoDimDynamicArray( T value ) {
        this( 1,1 );
        put( MapCoordinate.initialCoordinate(), value );
    }

    public int sizeX() {
        return array.get( 0 ).size();
    }

    public int sizeY() {
        return array.size();
    }

    public MapCoordinate getMaxCoordinate() {
        return MapCoordinate.fromInteger( sizeX()-1, sizeY()-1 );
    }

    public T at( MapCoordinate mapCoordinate) {
        if ( mapCoordinate == null ) throw new PrinterException( "mapCoordinate must not be null" );
        if ( mapCoordinate.isLargerThan( getMaxCoordinate() ) )
            throw new PrinterException( "mapCoordinate out of bounds: " + mapCoordinate);
        return array.get( mapCoordinate.getY() ).get( mapCoordinate.getX() );
    }

    public T at( int x, int y ) {
        MapCoordinate mapCoordinate = MapCoordinate.fromInteger( x, y );
        return at(mapCoordinate);
    }


    public void put( MapCoordinate mapCoordinate, T value ) {
        if ( mapCoordinate == null ) throw new PrinterException( "mapCoordinate must not be null" );
        if ( mapCoordinate.isLargerThan( getMaxCoordinate() ) )
            throw new PrinterException( "mapCoordinate out of bounds: " + mapCoordinate);
        array.get(mapCoordinate.getY()).set(mapCoordinate.getX(), value);
    }

    public void put( int x, int y, T value ) {
        MapCoordinate mapCoordinate = MapCoordinate.fromInteger( x, y );
        put(mapCoordinate, value );
    }

    /**
     * Put a value into the array no/we/so/ea of the given mapCoordinate. Enhance the array if needed.
     * @param mapCoordinate
     * @param mapDirection
     * @param value
     * @return the MapCoordinate of the (potentially enlarged) array, where the value is now located
     */
    public MapCoordinate putAndEnhance( MapCoordinate mapCoordinate, MapDirection mapDirection, T value ) {
        if ( mapCoordinate == null ) throw new PrinterException( "mapCoordinate must not be null" );
        if ( mapCoordinate.isLargerThan( getMaxCoordinate() ) )
            throw new PrinterException( "mapCoordinate out of bounds: " + mapCoordinate);
        if ( mapDirection == null ) throw new PrinterException( "direction must not be null" );

        MapCoordinate whereToInsert = enhanceIfNeededAt(mapCoordinate, mapDirection );
        put( whereToInsert, value );
        return whereToInsert;
    }

    /**
     * Enhance the array at a given position if this is needed, in the compass direction specified
     * @param mapCoordinate
     * @param mapDirection
     * @return
     */
    protected MapCoordinate enhanceIfNeededAt( MapCoordinate mapCoordinate, MapDirection mapDirection ) {
        if ( mapCoordinate == null ) throw new PrinterException( "mapCoordinate must not be null" );
        if ( mapCoordinate.isLargerThan( getMaxCoordinate() ) )
            throw new PrinterException( "mapCoordinate out of bounds: " + mapCoordinate);
        if ( mapDirection == null ) throw new PrinterException( "compassDirection must not be null" );
        MapCoordinate neighbourMapCoordinate = mapCoordinate.neighbourCoordinate( mapDirection );
        if ( neighbourMapCoordinate.isLargerThan( getMaxCoordinate() ) ||
                ( mapCoordinate.getY() == 0 && mapDirection.equals( no ) ) ||
                ( mapCoordinate.getX() == 0 && mapDirection.equals( we ) ) ) {
            switch (mapDirection) {
                case no: addRowAt( 0 ); break; // just the opposite of what is intuitive: 0 = north!
                case ea: addColumnAt(mapCoordinate.getX() + 1); break;
                case so: addRowAt(mapCoordinate.getY() + 1);break;
                case we: addColumnAt(mapCoordinate.getX());
            }
        }
        return neighbourMapCoordinate;
    }

    public void addRowAt( int y ) {
        if ( y < 0 ) throw new PrinterException( "can't add row at index < 0: " + y );
        if ( y > sizeY() ) throw new PrinterException( "can't add row at index > sizeY: " + y );
        if ( y < sizeY() ) array.add( y, createNullRow( sizeX() ) );
        else array.add( createNullRow( sizeX() ) );
    }

    public void addColumnAt( int x ) {
        if( x < 0 ) throw new PrinterException( "can't add column at index < 0: " + x );
        if( x > sizeX() ) throw new PrinterException( "can't add column at index > sizeX: " + x );
        for ( int y = 0; y < sizeY(); y++ ) {
            if ( x < sizeX() ) array.get( y ).add( x, null );
            else array.get( y ).add( null );
        }
    }


    /**
     * Finds the coordinate for a specific element.
     * @param element
     * @return The coordinate, or null if not contained.
     */
    public MapCoordinate find(T element ) {
        if ( element == null ) throw new PrinterException( "element == null" );
        int y = 0;
        for ( ArrayList<T> innerList : array ) {
            int x = 0;
            for ( T containedElement : innerList ) {
                if ( element.equals( containedElement ) ) {
                    return MapCoordinate.fromInteger( x, y );
                }
                x++;
            }
            y++;
        }
        return null;
    }


    /**
     * @param element
     * @return True if element is in the 2d array, false otherwise
     */
    public boolean contains( T element ) {
        return ( find( element ) != null );
    }




    @Override
    public String toString() {
        String retVal = "";
        for ( int y = 0; y < sizeY(); y++ ) {
            for ( int x = 0; x < sizeX(); x++ ) {
                retVal += String.valueOf( at( MapCoordinate.fromInteger( x, y ) ) );
                retVal += "  ||  ";
            }
            retVal += "\n";
        }
        return retVal;
    }

    private ArrayList<T> createNullRow( int sizeY ) {
        ArrayList<T> yArray = new ArrayList<>();
        for ( int y = 0; y < sizeY; y++ ) {
            yArray.add( null );
        }
        return yArray;
    }
}

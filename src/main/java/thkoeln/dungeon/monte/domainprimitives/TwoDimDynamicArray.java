package thkoeln.dungeon.monte.domainprimitives;

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
            throw new DomainPrimitiveException( "maxX / maxY must be > 0: " + maxX + ", " + maxY );
        for ( int y = 0; y < maxY; y++ ) {
            array.add( createNullRow( maxX )  );
        }
    }

    public TwoDimDynamicArray( Coordinate max ) {
        this( max.getX()+1 , max.getY()+1 );
    }

    public TwoDimDynamicArray( T value ) {
        this( 1,1 );
        put( Coordinate.initialCoordinate(), value );
    }

    public int sizeX() {
        return array.get( 0 ).size();
    }

    public int sizeY() {
        return array.size();
    }

    public Coordinate getMaxCoordinate() {
        return Coordinate.fromInteger( sizeX()-1, sizeY()-1 );
    }

    public T at( Coordinate coordinate ) {
        if ( coordinate == null ) throw new DomainPrimitiveException( "coordinate must not be null" );
        if ( coordinate.isLargerThan( getMaxCoordinate() ) )
            throw new DomainPrimitiveException( "coordinate out of bounds: " + coordinate );
        return array.get( coordinate.getY() ).get( coordinate.getX() );
    }

    public T at( int x, int y ) {
        Coordinate coordinate = Coordinate.fromInteger( x, y );
        return at( coordinate );
    }


    public void put( Coordinate coordinate, T value ) {
        if ( coordinate == null ) throw new DomainPrimitiveException( "coordinate must not be null" );
        if ( coordinate.isLargerThan( getMaxCoordinate() ) )
            throw new DomainPrimitiveException( "coordinate out of bounds: " + coordinate );
        array.get(coordinate.getY()).set(coordinate.getX(), value);
    }

    public void put( int x, int y, T value ) {
        Coordinate coordinate = Coordinate.fromInteger( x, y );
        put( coordinate, value );
    }

    /**
     * Put a value into the array no/we/so/ea of the given coordinate. Enhance the array if needed.
     * @param coordinate
     * @param compassDirection
     * @param value
     * @return the Coordinate of the (potentially enlarged) array, where the value is now located
     */
    public Coordinate putAndEnhance( Coordinate coordinate, CompassDirection compassDirection, T value ) {
        if ( coordinate == null ) throw new DomainPrimitiveException( "coordinate must not be null" );
        if ( coordinate.isLargerThan( getMaxCoordinate() ) )
            throw new DomainPrimitiveException( "coordinate out of bounds: " + coordinate );
        if ( compassDirection == null ) throw new DomainPrimitiveException( "compassDirection must not be null" );

        Coordinate whereToInsert = enhanceIfNeededAt( coordinate, compassDirection );
        put( whereToInsert, value );
        return whereToInsert;
    }

    /**
     * Enhance the array at a given position if this is needed, in the compass direction specified
     * @param coordinate
     * @param compassDirection
     * @return
     */
    protected Coordinate enhanceIfNeededAt( Coordinate coordinate, CompassDirection compassDirection ) {
        if ( coordinate == null ) throw new DomainPrimitiveException( "coordinate must not be null" );
        if ( coordinate.isLargerThan( getMaxCoordinate() ) )
            throw new DomainPrimitiveException( "coordinate out of bounds: " + coordinate );
        if ( compassDirection == null ) throw new DomainPrimitiveException( "compassDirection must not be null" );
        Coordinate neighbourCoordinate = coordinate.neighbourCoordinate( compassDirection );
        if ( neighbourCoordinate.isLargerThan( getMaxCoordinate() ) ||
                ( coordinate.getY() == 0 && compassDirection.equals( CompassDirection.NORTH ) ) ||
                ( coordinate.getX() == 0 && compassDirection.equals( CompassDirection.WEST ) ) ) {
            switch (compassDirection) {
                case NORTH: addRowAt( 0 ); break; // just the opposite of what is intuitive: 0 = north!
                case EAST: addColumnAt(coordinate.getX() + 1); break;
                case SOUTH: addRowAt(coordinate.getY() + 1);break;
                case WEST: addColumnAt(coordinate.getX());
            }
        }
        return neighbourCoordinate;
    }

    public void addRowAt( int y ) {
        if ( y < 0 ) throw new DomainPrimitiveException( "can't add row at index < 0: " + y );
        if ( y > sizeY() ) throw new DomainPrimitiveException( "can't add row at index > sizeY: " + y );
        if ( y < sizeY() ) array.add( y, createNullRow( sizeX() ) );
        else array.add( createNullRow( sizeX() ) );
    }

    public void addColumnAt( int x ) {
        if( x < 0 ) throw new DomainPrimitiveException( "can't add column at index < 0: " + x );
        if( x > sizeX() ) throw new DomainPrimitiveException( "can't add column at index > sizeX: " + x );
        for ( int y = 0; y < sizeY(); y++ ) {
            if ( x < sizeX() ) array.get( y ).add( x, null );
            else array.get( y ).add( null );
        }
    }






    @Override
    public String toString() {
        String retVal = "";
        for ( int y = 0; y < sizeY(); y++ ) {
            for ( int x = 0; x < sizeX(); x++ ) {
                retVal += String.valueOf( at( Coordinate.fromInteger( x, y ) ) );
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

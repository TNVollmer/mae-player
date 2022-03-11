package thkoeln.dungeon.domainprimitives;

import java.util.ArrayList;
import java.util.Iterator;

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
            throw new TwoDimDynamicArrayException( "maxX / maxY must be > 0: " + maxX + ", " + maxY );
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

    public T get( Coordinate coordinate ) {
        if ( coordinate == null ) throw new TwoDimDynamicArrayException( "coordinate must not be null" );
        if ( coordinate.isLargerThan( getMaxCoordinate() ) )
            throw new TwoDimDynamicArrayException( "coordinate out of bounds: " + coordinate );
        return array.get( coordinate.getY() ).get( coordinate.getX() );
    }

    public void put( Coordinate coordinate, T value ) {
        if ( coordinate == null ) throw new TwoDimDynamicArrayException( "coordinate must not be null" );
        if ( coordinate.isLargerThan( getMaxCoordinate() ) )
            throw new TwoDimDynamicArrayException( "coordinate out of bounds: " + coordinate );
        array.get( coordinate.getY() ).set( coordinate.getX(), value );
    }

    public void addRowAt( int y ) {
        if ( y < 0 ) throw new TwoDimDynamicArrayException( "can't add row at index < 0: " + y );
        if ( y > sizeY() ) throw new TwoDimDynamicArrayException( "can't add row at index > sizeY: " + y );
        if ( y < sizeY() ) array.add( y, createNullRow( sizeX() ) );
        else array.add( createNullRow( sizeX() ) );
    }

    public void addColumnAt( int x ) {
        if( x < 0 ) throw new TwoDimDynamicArrayException( "can't add column at index < 0: " + x );
        if( x > sizeX() ) throw new TwoDimDynamicArrayException( "can't add column at index > sizeX: " + x );
        for ( int y = 0; y < sizeY(); y++ ) {
            if ( x < sizeX() ) array.get( y ).add( x, null );
            else array.get( y ).add( null );
        }
    }


    /**
     * Enhance the array at a given position if this is needed, in the compass direction specified
     * @param coordinate
     * @param compassDirection
     */
    public void enhanceIfNeededAt( Coordinate coordinate, CompassDirection compassDirection ) {
        if ( coordinate == null ) throw new TwoDimDynamicArrayException( "coordinate must not be null" );
        if ( coordinate.isLargerThan( getMaxCoordinate() ) )
            throw new TwoDimDynamicArrayException( "coordinate out of bounds: " + coordinate );
        if ( compassDirection == null ) throw new TwoDimDynamicArrayException( "compassDirection must not be null" );
        Coordinate neighbourCoordinate = coordinate.neighbourCoordinate( compassDirection );
        if ( neighbourCoordinate.isLargerThan( getMaxCoordinate() ) ||
                ( coordinate.equals( Coordinate.initialCoordinate() )
                     && compassDirection.equals( CompassDirection.NORTH ) ) ||
                ( coordinate.equals( Coordinate.initialCoordinate() )
                        && compassDirection.equals( CompassDirection.WEST ) ) ) {
            switch (compassDirection) {
                case NORTH: addRowAt(coordinate.getY() + 1);break;
                case EAST: addColumnAt(coordinate.getX() + 1); break;
                case SOUTH: addRowAt(coordinate.getY()); break;
                case WEST: addColumnAt(coordinate.getX());
            }
        }
    }


    @Override
    public String toString() {
        String retVal = "";
        for ( int y = 0; y < sizeY(); y++ ) {
            for ( int x = 0; x < sizeX(); x++ ) {
                retVal += String.valueOf( get( Coordinate.fromInteger( x, y ) ) );
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

package thkoeln.dungeon.core;

import thkoeln.dungeon.DungeonPlayerRuntimeException;

import java.util.ArrayList;

/**
 * 2dim Array als a list of rows of equal length
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

    public int sizeX() {
        return array.get( 0 ).size();
    }

    public int sizeY() {
        return array.size();
    }


    public T get( int x, int y ) {
        if ( x < 0 || x >= sizeX() ) throw new TwoDimDynamicArrayException( "x out of bounds: " + x );
        if ( y < 0 || y >= sizeY() ) throw new TwoDimDynamicArrayException( "y out of bounds: " + y );
        return array.get( y ).get( x );
    }

    public void put( int x, int y, T value ) {
        if ( x < 0 || x >= sizeX() ) throw new TwoDimDynamicArrayException( "x out of bounds: " + x );
        if ( y < 0 || y >= sizeY() ) throw new TwoDimDynamicArrayException( "y out of bounds: " + y );
        array.get( y ).set( x, value );
    }

    public void addRowAt( int y ) {
        if( y < 0 ) throw new TwoDimDynamicArrayException( "can't add row at index < 0: " + y );
        if( y >= sizeY() ) throw new TwoDimDynamicArrayException( "can't add row at index >= sizeY: " + y );
        array.add( y, createNullRow( sizeX() ) );
    }

    public void addColumnAt( int x ) {
        if( x < 0 ) throw new TwoDimDynamicArrayException( "can't add column at index < 0: " + x );
        if( x >= sizeX() ) throw new TwoDimDynamicArrayException( "can't add column at index >= sizeX: " + x );
        for ( int y = 0; y < sizeY(); y++ ) {
            array.get( y ).add( x, null );
        }
    }


    private ArrayList<T> createNullRow( int sizeY ) {
        ArrayList<T> yArray = new ArrayList<>();
        for ( int y = 0; y < sizeY; y++ ) {
            yArray.add( null );
        }
        return yArray;
    }
}

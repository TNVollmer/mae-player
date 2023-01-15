package thkoeln.dungeon.monte.printer.util;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

/*
 * For convenience (compatibility with our reading directions), Coordinates seen as growing from top to bottom
 * and from left to right. I.e. it looks like this:
 *
 *        x---->
 *         0  1  2  3  4
 *   y   0
 *   |   1
 *   |   2
 *   V   3
 */

@Getter
@Setter( AccessLevel.PROTECTED )
@Embeddable
@EqualsAndHashCode
public class MapCoordinate {

    private Integer x;
    private Integer y;

    public static MapCoordinate fromInteger( Integer x, Integer y ) {
        if ( x == null ) throw new PrinterException( "x must not be null!" );
        if ( y == null ) throw new PrinterException( "y must not be null!" );
        if ( x < 0 ) throw new PrinterException( "x must be >= 0: " + x );
        if ( y < 0 ) throw new PrinterException( "y must be >= 0: " + y );
        return new MapCoordinate( x, y );
    }

    /**
     * @param coordinateString the coordinate in form of a string e.g. (1,2)
     */
    public static MapCoordinate fromString( String coordinateString ) {
        String[] coords = coordinateString.replaceAll("\\(","").replaceAll("\\)","").split(",");
        if ( coords.length != 2 ) throw new PrinterException( "Not a valid string" );

        Integer x = Integer.valueOf(coords[0]);
        Integer y = Integer.valueOf(coords[1]);
        return new MapCoordinate( x, y );
    }

    /**
     * The first coordinate ever assigned is (0,0) - the map is built starting from here. This
     * allows negative coordinates as well.
     */
    public static MapCoordinate initialCoordinate() {
        return new MapCoordinate( 0, 0 );
    }

    protected MapCoordinate(Integer x, Integer y ) {
        this.x = x;
        this.y = y;
    }

    protected MapCoordinate() {}

    public boolean isSmallerEqualsThan( MapCoordinate anotherMapCoordinate) {
        if ( anotherMapCoordinate == null ) return false;
        return ( this.x <= anotherMapCoordinate.getX() && this.y <= anotherMapCoordinate.getY() );
    }

    public boolean isLargerThan( MapCoordinate anotherMapCoordinate) {
        if ( anotherMapCoordinate == null ) return false;
        return ( this.x > anotherMapCoordinate.getX() || this.y > anotherMapCoordinate.getY() );
    }

    public MapCoordinate neighbourCoordinate( MapDirection mapDirection ) {
        if ( mapDirection == null ) throw new PrinterException( "compassDirection must not be null." );
        Integer newX = this.x;
        Integer newY = this.y;
        if ( mapDirection == MapDirection.no ) newY = Math.max( newY-1, 0 );
        if ( mapDirection == MapDirection.ea ) newX++;
        if ( mapDirection == MapDirection.so ) newY++;
        if ( mapDirection == MapDirection.we ) newX = Math.max( newX-1, 0 );

        return MapCoordinate.fromInteger( newX, newY );
    }


    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

}

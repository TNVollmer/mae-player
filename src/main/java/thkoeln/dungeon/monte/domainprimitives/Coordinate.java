package thkoeln.dungeon.monte.domainprimitives;

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
public class Coordinate {

    private Integer x;
    private Integer y;

    public static Coordinate fromInteger( Integer x, Integer y ) {
        if ( x == null ) throw new DomainPrimitiveException( "x must not be null!" );
        if ( y == null ) throw new DomainPrimitiveException( "y must not be null!" );
        if ( x < 0 ) throw new DomainPrimitiveException( "x must be >= 0: " + x );
        if ( y < 0 ) throw new DomainPrimitiveException( "y must be >= 0: " + y );
        return new Coordinate( x, y );
    }

    /**
     * @param coordinateString the coordinate in form of a string e.g. (1,2)
     */
    public static Coordinate fromString( String coordinateString ) {
        String[] coords = coordinateString.replaceAll("\\(","").replaceAll("\\)","").split(",");
        if ( coords.length != 2 ) throw new DomainPrimitiveException( "Not a valid string" );

        Integer x = Integer.valueOf(coords[0]);
        Integer y = Integer.valueOf(coords[1]);
        return new Coordinate( x, y );
    }

    /**
     * The first coordinate ever assigned is (0,0) - the map is built starting from here. This
     * allows negative coordinates as well.
     */
    public static Coordinate initialCoordinate() {
        return new Coordinate( 0, 0 );
    }

    protected Coordinate ( Integer x, Integer y ) {
        this.x = x;
        this.y = y;
    }

    protected Coordinate() {}

    public boolean isSmallerEqualsThan( Coordinate anotherCoordinate ) {
        if ( anotherCoordinate == null ) return false;
        return ( this.x <= anotherCoordinate.getX() && this.y <= anotherCoordinate.getY() );
    }

    public boolean isLargerThan( Coordinate anotherCoordinate ) {
        if ( anotherCoordinate == null ) return false;
        return ( this.x > anotherCoordinate.getX() || this.y > anotherCoordinate.getY() );
    }

    public Coordinate neighbourCoordinate( CompassDirection compassDirection ) {
        if ( compassDirection == null ) throw new DomainPrimitiveException( "compassDirection must not be null." );
        Integer newX = this.x;
        Integer newY = this.y;
        if ( compassDirection == CompassDirection.NORTH ) newY = Math.max( newY-1, 0 );
        if ( compassDirection == CompassDirection.EAST ) newX++;
        if ( compassDirection == CompassDirection.SOUTH ) newY++;
        if ( compassDirection == CompassDirection.WEST ) newX = Math.max( newX-1, 0 );

        return Coordinate.fromInteger( newX, newY );
    }


    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

}

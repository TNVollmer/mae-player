package thkoeln.dungeon.monte.planet.application;

import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.monte.domainprimitives.Coordinate;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.planet.domain.PlanetException;

/**
 * Used for the output of planets to console or web interface. Planets, ressources, and Robots on it are
 * encoded as Strings.
 */
@Getter
public class PlanetConsolePrintDto {
    private String planetString;
    private String resourceString;
    @Setter
    private String robotString;

    public static final String EMPTY_CELL = "    ";
    public static final String SEPERATOR_CELL = "----";
    public static final String SEPERATOR_CHAR = "|";
    public static final String BLACK_BACKGROUND = "\033[40m";
    public static final String WHITE_BACKGROUND = "\033[47m";

    public PlanetConsolePrintDto( Planet planet ) {
        if ( planet == null ) {
            // black hole
            planetString = BLACK_BACKGROUND + EMPTY_CELL + WHITE_BACKGROUND + SEPERATOR_CHAR;
            resourceString = planetString;
            robotString = planetString;
            return;
        }
        planetString = planet.toString() + SEPERATOR_CHAR;
        resourceString = (planet.getMineableResource() == null) ?
                EMPTY_CELL + SEPERATOR_CHAR : planet.getMineableResource().toString() + SEPERATOR_CHAR;
        robotString = EMPTY_CELL + SEPERATOR_CHAR;
    }


    public String printLine( int lineNr ) {
        if ( lineNr < 0 || lineNr > 3 ) throw new PlanetException( "i < 1 || i > 3" );
        switch ( lineNr ) {
            case 0: return SEPERATOR_CELL + SEPERATOR_CHAR;
            case 1: return planetString;
            case 2: return resourceString;
            default: return robotString;
        }
    }

    /**
     * Print the top row of the matrix with coordinate numbers.
     * @param maxClusterPoint
     * @return
     */
    public static String printTopRow( Coordinate maxClusterPoint ) {
        String printString = EMPTY_CELL + " ";
        for ( int columnNumber = 0; columnNumber < maxClusterPoint.getX(); columnNumber++ ) {
            printString += String.format( "%1$3s", columnNumber ) + "  ";
        }
        printString += "\n" + EMPTY_CELL + SEPERATOR_CHAR;
        for ( int columnNumber = 0; columnNumber < maxClusterPoint.getX(); columnNumber++ ) {
            printString += SEPERATOR_CELL + SEPERATOR_CHAR;
        }
        return printString;
    }

    public static String printRowNumber( int rowNumber ) {
        return "\n" + String.format( "%1$3s", rowNumber ) + " " + SEPERATOR_CHAR;
    }

    public static String empty() {
        return EMPTY_CELL + SEPERATOR_CHAR;
    }

    public static String multiple( int howMany ) {
        return " (" + howMany + ")" + SEPERATOR_CHAR;
    }

    public static String cell( String cellContent ) {
        if ( cellContent == null ) throw new PlanetException( "cellContent == null" );
        return cellContent.substring( 0, 4 ) + SEPERATOR_CHAR;
    }
}

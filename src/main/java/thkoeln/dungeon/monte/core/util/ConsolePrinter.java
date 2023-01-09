package thkoeln.dungeon.monte.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConsolePrinter implements Printer {
    private Logger logger = LoggerFactory.getLogger( ConsolePrinter.class );

    // Singleton!
    public static StringBuffer stringBuffer = new StringBuffer();

    /**
     * Code for colors taken from
     * https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
     */

    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    // Underline
    public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
    public static final String RED_UNDERLINED = "\033[4;31m";    // RED
    public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
    public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
    public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
    public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE

    // Background
    public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
    public static final String RED_BACKGROUND = "\033[41m";    // RED
    public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
    public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
    public static final String RED_BRIGHT = "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    // High Intensity backgrounds
    public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
    public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
    public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
    public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
    public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
    public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m";  // CYAN
    public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m";   // WHITE

    protected static final String EMPTY_COMPARTMENT = "    ";
    protected static final String SEPERATOR_SEGMENT = "----|";
    protected static final String SEPERATOR_CHAR = "|";


    protected int currentNumberOfColumns = 0;
    protected int currentNumberOfCompartments = 0;
    protected List<String[]> currentRowCells = new ArrayList<>();


    @Override
    public void initializeOutput() {
        stringBuffer = new StringBuffer();
    }

    @Override
    public void header( String string ) {
        stringBuffer.append( "\n" ).append( BLUE_BOLD_BRIGHT ).append( string ).append( RESET ).append( "\n\n" );
    }

    @Override
    public void startBulletList() {
    }

    @Override
    public void endBulletList() {
    }

    @Override
    public void writeBulletItem( String string ) {
        stringBuffer.append( "\t * " ).append( string ).append( "\n" );
    }

    @Override
    public void startLine() {
    }

    @Override
    public void startLineIndent() {
        stringBuffer.append( "\t" );
    }

    @Override
    public void write( String string ) {
        stringBuffer.append( string );
    }

    @Override
    public void endLine() {
        stringBuffer.append( "\n" );
    }

    @Override
    public void startColorFont( Color color ) {
        switch ( color ) {
            case YELLOW: stringBuffer.append( YELLOW ); break;
            case RED: stringBuffer.append( RED ); break;
            case BLUE: stringBuffer.append( BLUE ); break;
            case GREEN: stringBuffer.append( GREEN ); break;
            case YELLOW_BRIGHT: stringBuffer.append( YELLOW_BRIGHT ); break;
            case RED_BRIGHT: stringBuffer.append( RED_BRIGHT ); break;
            case BLUE_BRIGHT: stringBuffer.append( BLUE_BRIGHT ); break;
            case GREEN_BRIGHT: stringBuffer.append( GREEN_BRIGHT ); break;
            default:
        }
    }

    @Override
    public void endColorFont() {
        stringBuffer.append( RESET );
    }

    @Override
    public void startColorBackground(Color color) {
        switch ( color ) {
            case YELLOW: stringBuffer.append( YELLOW_BACKGROUND ); break;
            case RED: stringBuffer.append( RED_BACKGROUND ); break;
            case BLUE: stringBuffer.append( BLUE_BACKGROUND ); break;
            case GREEN: stringBuffer.append( GREEN_BACKGROUND ); break;
            case YELLOW_BRIGHT: stringBuffer.append( YELLOW_BACKGROUND_BRIGHT ); break;
            case RED_BRIGHT: stringBuffer.append( RED_BACKGROUND_BRIGHT ); break;
            case BLUE_BRIGHT: stringBuffer.append( BLUE_BACKGROUND_BRIGHT ); break;
            case GREEN_BRIGHT: stringBuffer.append( GREEN_BACKGROUND_BRIGHT ); break;
            default:
        }
    }

    @Override
    public void endColorBackground() {
        stringBuffer.append( RESET );
    }

    @Override
    public void startTable( int numOfColumns ) {
        currentNumberOfColumns = numOfColumns;
        stringBuffer.append( EMPTY_COMPARTMENT ).append( SEPERATOR_CHAR );
        for ( int columnNumber = 0; columnNumber < currentNumberOfColumns; columnNumber++ ) {
            stringBuffer.append( String.format( "%1$3s", columnNumber ) + " " + SEPERATOR_CHAR );
        }
        stringBuffer.append( "\n" );
        for ( int columnNumber = 0; columnNumber <= currentNumberOfColumns; columnNumber++ ) {
            stringBuffer.append( SEPERATOR_SEGMENT );
        }
        stringBuffer.append( "\n" );
    }

    @Override
    public void endTable() {
    }

    @Override
    public void startRow( int rowNumber, int numOfCompartments ) {
        currentNumberOfCompartments = numOfCompartments;
        currentRowCells = new ArrayList<>();
        String[] cell = new String[numOfCompartments];
        for ( int compartmentNumber = 0; compartmentNumber < numOfCompartments; compartmentNumber++ ) {
            cell[compartmentNumber] = ( compartmentNumber == numOfCompartments / 2 ) ?
                    ( String.format( "%1$3s", rowNumber ) + " " ) : EMPTY_COMPARTMENT;
        }
        currentRowCells.add( cell );
    }

    @Override
    public void writeCell( String... compartmentStrings ) {
        currentRowCells.add( compartmentStrings );
    }

    @Override
    public void endRow() {
        for ( int compartmentNumber = 0; compartmentNumber < currentNumberOfCompartments; compartmentNumber++ ) {
            for ( String[] cell : currentRowCells ) {
                String compartmentString = String.format( "%-4s", cell[compartmentNumber] );
                stringBuffer.append( compartmentString ).append( SEPERATOR_CHAR );
            }
            stringBuffer.append( "\n" );
        }
        for ( int compartmentNumber = 0; compartmentNumber <= currentNumberOfColumns; compartmentNumber++ ) {
            stringBuffer.append( SEPERATOR_SEGMENT );
        }
        stringBuffer.append( "\n" );
    }

    @Override
    public void flush() {
        logger.info( "\n" + stringBuffer );
        stringBuffer = new StringBuffer();
    }

}
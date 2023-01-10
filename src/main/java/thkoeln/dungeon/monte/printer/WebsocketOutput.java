package thkoeln.dungeon.monte.printer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.monte.core.statusclient.OutputMessage;

import java.util.ArrayList;
import java.util.List;

@Component
public class WebsocketOutput implements OutputDevice {
    private Logger logger = LoggerFactory.getLogger( WebsocketOutput.class );

    // Singleton!
    public static StringBuffer stringBuffer = new StringBuffer();

    protected int currentNumberOfColumns = 0;
    protected int currentNumberOfCompartments = 0;
    protected List<String[]> currentRowCells = new ArrayList<>();

    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public WebsocketOutput(SimpMessagingTemplate simpMessagingTemplate ) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    @Override
    public void initializeOutput() {
        stringBuffer = new StringBuffer();
    }

    @Override
    public void header( String string ) {
        stringBuffer.append( "<h2>" ).append( string ).append( "</h2>\n" );
    }

    @Override
    public void startBulletList() {
        stringBuffer.append( "<ul>\n" );
    }

    @Override
    public void endBulletList() {
        stringBuffer.append( "</ul>\n" );
    }

    @Override
    public void writeBulletItem( String string ) {
        stringBuffer.append( "<li>" ).append( string ).append( "</li>\n" );
    }

    @Override
    public void startLine() {
        stringBuffer.append( "<p>\n" );
    }

    @Override
    public void startLineIndent() {
        stringBuffer.append( "<p>&nbsp;&nbsp;&nbsp;&nbsp;\n" );
    }

    @Override
    public void write( String string ) {
        stringBuffer.append( string );
    }

    @Override
    public void endLine() {
        stringBuffer.append( "</p>\n" );
    }

    @Override
    public void startColorFont( Color color ) {
        switch ( color ) {
            case YELLOW: stringBuffer.append( "<span style=\"color:yellow;\">\n" ); break;
            case RED: stringBuffer.append( "<span style=\"color:red;\">\n" ); break;
            case BLUE: stringBuffer.append( "<span style=\"color:blue;\">\n" ); break;
            case GREEN: stringBuffer.append( "<span style=\"color:green;\">\n" ); break;
            case YELLOW_BRIGHT: stringBuffer.append( "<span style=\"color:yellow;font-weight:bold;\">\n" ); break;
            case RED_BRIGHT: stringBuffer.append( "<span style=\"color:red;font-weight:bold;\">\n" ); break;
            case BLUE_BRIGHT: stringBuffer.append( "<span style=\"color:blue;font-weight:bold;\">\n" ); break;
            case GREEN_BRIGHT: stringBuffer.append( "<span style=\"color:green;font-weight:bold;\">\n" ); break;
            default:
        }
    }

    @Override
    public void endColorFont() {
        stringBuffer.append( "</span>\n" );
    }

    // todo
    @Override
    public void startColorBackground(Color color) {
        switch ( color ) {
            case YELLOW: stringBuffer.append( "<span style=\"background-color:yellow;\">\n" ); break;
            case RED: stringBuffer.append( "<span style=\"background-color:red;\">\n" ); break;
            case BLUE: stringBuffer.append( "<span style=\"background-color:blue;\">\n" ); break;
            case GREEN: stringBuffer.append( "<span style=\"background-color:green;\">\n" ); break;
            case YELLOW_BRIGHT: stringBuffer.append( "<span style=\"background-color:yellow;font-weight:bold;\">\n" ); break;
            case RED_BRIGHT: stringBuffer.append( "<span style=\"background-color:red;font-weight:bold;\">\n" ); break;
            case BLUE_BRIGHT: stringBuffer.append( "<span style=\"background-color:blue;font-weight:bold;\">\n" ); break;
            case GREEN_BRIGHT: stringBuffer.append( "<span style=\"background-color:green;font-weight:bold;\">\n" ); break;
            default:
        }
    }

    @Override
    public void endColorBackground() {

    }

    @Override
    public void startMap( int numOfColumns ) {
        currentNumberOfColumns = numOfColumns;
        stringBuffer.append( "<table>\n" ).append( "<thead>\n" ).append( "<tr>\n" ).append( "<th>&nbsp;</th>\n" );
        for ( int columnNumber = 0; columnNumber < currentNumberOfColumns; columnNumber++ ) {
            stringBuffer.append( "<th>" ).append( columnNumber ).append( "</th>\n" );
        }
        stringBuffer.append( "</tr>\n" ).append( "</thead>\n" ).append( "<tbody>\n" );
    }

    @Override
    public void endMap() {
        stringBuffer.append( "</tbody>\n" ).append( "</table>\n" );
    }


    @Override
    public void startMapRow( int rowNumber, int numOfCompartments ) {
        currentNumberOfCompartments = numOfCompartments;
        currentRowCells = new ArrayList<>();
        String[] cell = new String[numOfCompartments];
        for ( int compartmentNumber = 0; compartmentNumber < numOfCompartments; compartmentNumber++ ) {
            cell[compartmentNumber] = ( compartmentNumber == numOfCompartments / 2 ) ?
                    String.valueOf( rowNumber ) : "";
        }
        currentRowCells.add( cell );
    }

    @Override
    public void writeCell( String... compartmentStrings ) {
        currentRowCells.add( compartmentStrings );
    }

    @Override
    public void endMapRow() {
        for ( int compartmentNumber = 0; compartmentNumber < currentNumberOfCompartments; compartmentNumber++ ) {
            for ( String[] cell : currentRowCells ) {
                stringBuffer.append( "<td>" ).append( cell[compartmentNumber] ).append( "</td>\n" );
            }
        }
    }

    @Override
    public void flush() {
        OutputMessage outputMessage = new OutputMessage( stringBuffer.toString() );
        simpMessagingTemplate.convertAndSend("/topic/pushstatus", outputMessage );
        stringBuffer = new StringBuffer();
    }

}
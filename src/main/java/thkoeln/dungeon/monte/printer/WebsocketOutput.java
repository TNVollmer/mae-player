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
        stringBuffer.append( "<h2>" ).append( string ).append( "</h2>" );
    }

    @Override
    public void startBulletList() {
        stringBuffer.append( "<ul>" );
    }

    @Override
    public void endBulletList() {
        stringBuffer.append( "</ul>" );
    }

    @Override
    public void writeBulletItem( String string ) {
        stringBuffer.append( "<li>" ).append( string ).append( "</li>" );
    }

    @Override
    public void startLine() {
        stringBuffer.append( "<p>" );
    }

    @Override
    public void startLineIndent() {
        stringBuffer.append( "<p>&nbsp;&nbsp;&nbsp;&nbsp;" );
    }

    @Override
    public void write( String string ) {
        stringBuffer.append( string );
    }

    @Override
    public void endLine() {
        stringBuffer.append( "</p>" );
    }

    @Override
    public void startColorFont( Color color ) {
        switch ( color ) {
            case YELLOW: stringBuffer.append( "<span style=\"color:yellow;\">" ); break;
            case RED: stringBuffer.append( "<span style=\"color:red;\">" ); break;
            case BLUE: stringBuffer.append( "<span style=\"color:blue;\">" ); break;
            case GREEN: stringBuffer.append( "<span style=\"color:green;\">" ); break;
            case YELLOW_BRIGHT: stringBuffer.append( "<span style=\"color:yellow;font-weight:bold;\">" ); break;
            case RED_BRIGHT: stringBuffer.append( "<span style=\"color:red;font-weight:bold;\">" ); break;
            case BLUE_BRIGHT: stringBuffer.append( "<span style=\"color:blue;font-weight:bold;\">" ); break;
            case GREEN_BRIGHT: stringBuffer.append( "<span style=\"color:green;font-weight:bold;\">" ); break;
            default:
        }
    }

    @Override
    public void endColorFont() {
        stringBuffer.append( "</span>" );
    }

    // todo
    @Override
    public void startColorBackground(Color color) {
        switch ( color ) {
            case YELLOW: stringBuffer.append( "<span style=\"background-color:yellow;\">" ); break;
            case RED: stringBuffer.append( "<span style=\"background-color:red;\">" ); break;
            case BLUE: stringBuffer.append( "<span style=\"background-color:blue;\">" ); break;
            case GREEN: stringBuffer.append( "<span style=\"background-color:green;\">" ); break;
            case YELLOW_BRIGHT: stringBuffer.append( "<span style=\"background-color:yellow;font-weight:bold;\">" ); break;
            case RED_BRIGHT: stringBuffer.append( "<span style=\"background-color:red;font-weight:bold;\">" ); break;
            case BLUE_BRIGHT: stringBuffer.append( "<span style=\"background-color:blue;font-weight:bold;\">" ); break;
            case GREEN_BRIGHT: stringBuffer.append( "<span style=\"background-color:green;font-weight:bold;\">" ); break;
            default:
        }
    }

    @Override
    public void endColorBackground() {

    }

    @Override
    public void startMap( int numOfColumns ) {
        currentNumberOfColumns = numOfColumns;
        stringBuffer.append( "<table>" ).append( "<thead>" ).
                append( "<tr>" ).append( "<th width='50'>&nbsp;</th>" );
        for ( int columnNumber = 0; columnNumber < currentNumberOfColumns; columnNumber++ ) {
            stringBuffer.append( "<th width='50'>" ).append( columnNumber ).append( "</th>" );
        }
        stringBuffer.append( "</tr>" ).append( "</thead>" ).append( "<tbody>" );
    }

    @Override
    public void endMap() {
        stringBuffer.append( "</tbody>" ).append( "</table>" );
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
        stringBuffer.append( "<tr>" );
        for ( String[] cell : currentRowCells ) {
            stringBuffer.append( "<td width='50'><table class='inner'><tbody class='inner'>" );
            for ( int compartmentNumber = 0; compartmentNumber < currentNumberOfCompartments; compartmentNumber++ ) {
                stringBuffer.append( "<tr class='inner'><td class='inner'>" ).
                        append( cell[compartmentNumber] ).append( "</tr></td>" );
            }
            stringBuffer.append( "</tbody></table></td>" );
        }
        stringBuffer.append( "</tr>" );
    }

    @Override
    public void flush() {
        OutputMessage outputMessage = new OutputMessage( stringBuffer.toString() );
        simpMessagingTemplate.convertAndSend("/topic/pushstatus", outputMessage );
        stringBuffer = new StringBuffer();
    }

}
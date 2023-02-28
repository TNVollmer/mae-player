package thkoeln.dungeon.monte.printer.devices.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.monte.printer.devices.MapCellDto;
import thkoeln.dungeon.monte.printer.devices.OutputDevice;

import java.util.ArrayList;
import java.util.List;

@Component
public class WebSocketOutput implements OutputDevice {
    private Logger logger = LoggerFactory.getLogger( WebSocketOutput.class );

    // Singleton!
    public static StringBuffer stringBuffer = new StringBuffer();

    protected int currentNumberOfColumns = 0;
    protected int currentNumberOfCompartments = 0;
    protected List<MapCellDto> currentRowCells = new ArrayList<>();

    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public WebSocketOutput(SimpMessagingTemplate simpMessagingTemplate ) {
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
        stringBuffer.append( "<ol>" );
    }

    @Override
    public void endBulletList() {
        stringBuffer.append( "</ol>" );
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
        stringBuffer.append( "<div class='map'><div class='rownum'>&nbsp;</div>" );
        for ( int columnNumber = 0; columnNumber < currentNumberOfColumns; columnNumber++ ) {
            stringBuffer.append( "<div class='colnum'>" ).append( columnNumber ).append( "</div>" );
        }
    }

    @Override
    public void endMap() {
        stringBuffer.append( "</div>" );
    }


    @Override
    public void startMapRow( int rowNumber, int numOfCompartments ) {
        currentNumberOfCompartments = numOfCompartments;
        stringBuffer.append( "<div class='rownum'>" ).append( rowNumber ).append( "</div>");
        currentRowCells = new ArrayList<>();
    }

    @Override
    public void writeCell( MapCellDto mapCellDto ) {
        currentRowCells.add( mapCellDto );
    }

    @Override
    public void endMapRow() {
        for ( MapCellDto mapCellDto : currentRowCells ) {
            String cellCSSClass = mapCellDto.cellCSSClass();
            stringBuffer.append( "<div class='" ).append( cellCSSClass ).append( "'>" );
            String[] compartmentStrings = mapCellDto.toCompartmentStrings();
            for ( int compartmentNumber = 0; compartmentNumber < currentNumberOfCompartments; compartmentNumber++ ) {
                String innerCellCSSClass = mapCellDto.innerCellCSSClass( compartmentNumber );
                stringBuffer.append( "<div class='" ).append( innerCellCSSClass ).append( "'>" ).
                             append( compartmentStrings[compartmentNumber] ).append( "</div>" );
            }
            stringBuffer.append( "</div>" );
        }
    }



    @Override
    public void flush() {
        OutputMessage outputMessage = new OutputMessage( stringBuffer.toString() );
        simpMessagingTemplate.convertAndSend("/topic/pushstatus", outputMessage );
        stringBuffer = new StringBuffer();
    }

}
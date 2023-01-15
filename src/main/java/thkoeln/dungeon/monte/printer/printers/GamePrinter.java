package thkoeln.dungeon.monte.printer.printers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.printer.devices.OutputDevice;
import thkoeln.dungeon.monte.printer.printables.GamePrintable;
import thkoeln.dungeon.monte.printer.finderservices.GameFinderService;

import java.util.List;

/**
 * OutputDevice class to output the current player status to console.
 */
@Service
public class GamePrinter {
    private Logger logger = LoggerFactory.getLogger( GamePrinter.class );
    private GameFinderService gameFinderService;
    private List<OutputDevice> outputDevices;


    @Autowired
    public GamePrinter( GameFinderService gameFinderService,
                        List<OutputDevice> outputDevices ) {
        this.gameFinderService = gameFinderService;
        this.outputDevices = outputDevices;
    }


    public void printStatus() {
        outputDevices.forEach(p -> p.header( "Game" ) );
        GamePrintable gamePrintable = gameFinderService.queryActiveGame();
        outputDevices.forEach(p -> p.startLine() );
        if ( gamePrintable == null ) {
            outputDevices.forEach(p -> p.write( "No active game found!" ) );
        }
        else {
            outputDevices.forEach(p -> p.write( gamePrintable.toString() ) );
        }
        outputDevices.forEach(p -> p.endLine() );
    }
}

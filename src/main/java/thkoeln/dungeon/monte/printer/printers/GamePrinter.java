package thkoeln.dungeon.monte.printer.printers;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.printer.devices.OutputDevice;
import thkoeln.dungeon.monte.printer.printables.GamePrintable;
import thkoeln.dungeon.monte.printer.finderservices.GameFinderService;
import thkoeln.dungeon.monte.printer.printables.TradingAccountPrintable;

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
        outputDevices.forEach(p -> p.startLine() );
        GamePrintable gamePrintable = gameFinderService.queryActiveGame();
        if ( gamePrintable == null ) {
            outputDevices.forEach(p -> p.write( "No active game found!" ) );
        }
        else {
            outputDevices.forEach(p -> p.write( gamePrintable.toString() + ". " ) );
        }
        TradingAccountPrintable tradingAccountPrintable = gameFinderService.tradingAccount();
        outputDevices.forEach(p -> p.write( tradingAccountPrintable.detailedDescription() + "." ) );
        outputDevices.forEach(p -> p.endLine() );
    }


}

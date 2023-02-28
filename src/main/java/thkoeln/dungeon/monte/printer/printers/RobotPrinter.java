package thkoeln.dungeon.monte.printer.printers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.printer.devices.OutputDevice;
import thkoeln.dungeon.monte.printer.finderservices.RobotFinderService;
import thkoeln.dungeon.monte.printer.printables.RobotPrintable;

import java.util.List;

/**
 * OutputDevice class to output the current player status to console.
 */
@Service
public class RobotPrinter {
    private RobotFinderService robotFinderService;
    private List<OutputDevice> outputDevices;

    @Autowired
    public RobotPrinter( RobotFinderService robotFinderService,
                         List<OutputDevice> outputDevices) {
        this.robotFinderService = robotFinderService;
        this.outputDevices = outputDevices;
    }



    /**
     * Print all currently alive robots, in a compact format suitable for the console.
     */
    public void printRobots() {
        List<? extends RobotPrintable> ownRobots = robotFinderService.allLivingOwnRobots();
        List<? extends RobotPrintable> enemyRobots = robotFinderService.allLivingEnemyRobots();
        printRobotList( ownRobots, "All my robots" );
        printRobotList( enemyRobots, "All enemy robots" );
    }



    private void printRobotList( List<? extends RobotPrintable> robotPrintables, String header ) {
        outputDevices.forEach(p -> p.header( header ) );
        outputDevices.forEach(p -> p.startBulletList() );
        for ( RobotPrintable robotPrintable : robotPrintables ) {
            outputDevices.forEach(p -> p.writeBulletItem( robotPrintable.detailedDescription() ) );
        }
        outputDevices.forEach(p -> p.endBulletList() );
    }


}

package thkoeln.dungeon.monte.robot.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.util.Printer;
import thkoeln.dungeon.monte.robot.domain.Robot;
import thkoeln.dungeon.monte.core.util.ConsolePrinter;

import java.util.List;

/**
 * Printer class to output the current player status to console.
 */
@Service
public class RobotPrinter {
    private RobotApplicationService robotApplicationService;
    private List<Printer> printers;

    @Autowired
    public RobotPrinter( RobotApplicationService robotApplicationService,
                         List<Printer> printers ) {
        this.robotApplicationService = robotApplicationService;
        this.printers = printers;
    }



    /**
     * @return Print all currently alive robots, in a compact format suitable for the console.
     */

    public void printRobotList() {
        printers.forEach( p -> p.header( "All my robots" ) );
        List<Robot> robots = robotApplicationService.allLivingRobots();
        printers.forEach( p -> p.startBulletList() );
        for ( Robot robot : robots ) {
            printers.forEach( p -> p.writeBulletItem( robot.toStringDetailed() ) );
        }
        printers.forEach( p -> p.endBulletList() );
    }


}

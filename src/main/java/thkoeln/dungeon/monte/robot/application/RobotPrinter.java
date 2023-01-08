package thkoeln.dungeon.monte.robot.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.robot.domain.Robot;
import thkoeln.dungeon.monte.core.util.ConsolePrinter;

import java.util.List;

/**
 * Printer class to output the current player status to console.
 */
@Service
public class RobotPrinter {
    private RobotApplicationService robotApplicationService;

    @Autowired
    public RobotPrinter( RobotApplicationService robotApplicationService ) {
        this.robotApplicationService = robotApplicationService;
    }



    /**
     * @return Print all currently alive robots, in a compact format suitable for the console.
     */
/*
    public void printRobotList() {
        writeLine( "All my robots:" );
        List<Robot> robots = robotApplicationService.allLivingRobots();
        for ( Robot robot : robots ) {
            writeLineIndent( robot.toStringDetailed() );
        }
    }
*/


}

package thkoeln.dungeon.monte.robot.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.printer.OutputDevice;
import thkoeln.dungeon.monte.robot.domain.Robot;

import java.util.List;

/**
 * OutputDevice class to output the current player status to console.
 */
@Service
public class RobotPrinter {
    private RobotApplicationService robotApplicationService;
    private List<OutputDevice> outputDevices;

    @Autowired
    public RobotPrinter( RobotApplicationService robotApplicationService,
                         List<OutputDevice> outputDevices) {
        this.robotApplicationService = robotApplicationService;
        this.outputDevices = outputDevices;
    }



    /**
     * @return Print all currently alive robots, in a compact format suitable for the console.
     */

    public void printRobotList() {
        outputDevices.forEach(p -> p.header( "All my robots" ) );
        List<Robot> robots = robotApplicationService.allLivingRobots();
        outputDevices.forEach(p -> p.startBulletList() );
        for ( Robot robot : robots ) {
            outputDevices.forEach(p -> p.writeBulletItem( robot.toStringDetailed() ) );
        }
        outputDevices.forEach(p -> p.endBulletList() );
    }


}

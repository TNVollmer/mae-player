package thkoeln.dungeon.monte.player.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.game.application.GamePrinter;
import thkoeln.dungeon.monte.planet.application.PlanetPrinter;
import thkoeln.dungeon.monte.robot.application.RobotPrinter;
import thkoeln.dungeon.monte.core.util.AbstractPrinter;

/**
 * Printer class to output the current player status to console.
 */
@Service
public class PlayerPrinter extends AbstractPrinter {
    private GamePrinter gamePrinter;
    private RobotPrinter robotPrinter;
    private PlanetPrinter planetPrinter;
    private MapPrinter mapPrinter;

    @Autowired
    public PlayerPrinter( GamePrinter gamePrinter,
                          RobotPrinter robotPrinter,
                          PlanetPrinter planetPrinter,
                          MapPrinter mapPrinter ) {
        this.gamePrinter = gamePrinter;
        this.robotPrinter = robotPrinter;
        this.mapPrinter = mapPrinter;
        this.planetPrinter = planetPrinter;
    }


    /**
     * @return Print the complete player status formatted for the console.
     */

    public void printStatus() {
        initializeOutput();
        write( RED );
        gamePrinter.printStatus();
        robotPrinter.printRobotList();
        planetPrinter.printPlanetList();
        mapPrinter.printMap();
        write( RESET );
        flush();
    }

}

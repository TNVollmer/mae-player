package thkoeln.dungeon.monte.player.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.util.Printer;
import thkoeln.dungeon.monte.game.application.GamePrinter;
import thkoeln.dungeon.monte.planet.application.PlanetPrinter;
import thkoeln.dungeon.monte.robot.application.RobotPrinter;
import thkoeln.dungeon.monte.core.util.ConsolePrinter;
import thkoeln.dungeon.monte.core.statusclient.OutputMessage;

import java.util.List;

/**
 * Printer class to output the current player status to console.
 */
@Service
public class PlayerPrinter {
    private GamePrinter gamePrinter;
    private RobotPrinter robotPrinter;
    private PlanetPrinter planetPrinter;
    private MapPrinter mapPrinter;
    private List<Printer> printers;

    @Autowired
    public PlayerPrinter( GamePrinter gamePrinter,
                          RobotPrinter robotPrinter,
                          PlanetPrinter planetPrinter,
                          MapPrinter mapPrinter,
                          SimpMessagingTemplate simpMessagingTemplate,
                          List<Printer> printers ) {
        this.gamePrinter = gamePrinter;
        this.robotPrinter = robotPrinter;
        this.mapPrinter = mapPrinter;
        this.planetPrinter = planetPrinter;
        this.printers = printers;
    }


    /**
     * @return Print the complete player status formatted for the console.
     */

    public void printStatus() {
        printers.forEach( p -> p.initializeOutput() );
        gamePrinter.printStatus();
        mapPrinter.printMap();
        robotPrinter.printRobotList();
        planetPrinter.printPlanetList();
        printers.forEach( p -> p.flush() );
    }

}

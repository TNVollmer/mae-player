package thkoeln.dungeon.monte.player.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.printer.OutputDevice;
import thkoeln.dungeon.monte.game.application.GamePrinter;
import thkoeln.dungeon.monte.planet.application.PlanetPrinter;
import thkoeln.dungeon.monte.robot.application.RobotPrinter;

import java.util.List;

/**
 * OutputDevice class to output the current player status to console.
 */
@Service
public class PlayerPrinter {
    private GamePrinter gamePrinter;
    private RobotPrinter robotPrinter;
    private PlanetPrinter planetPrinter;
    private MapPrinter mapPrinter;
    private List<OutputDevice> outputDevices;

    @Autowired
    public PlayerPrinter( GamePrinter gamePrinter,
                          RobotPrinter robotPrinter,
                          PlanetPrinter planetPrinter,
                          MapPrinter mapPrinter,
                          SimpMessagingTemplate simpMessagingTemplate,
                          List<OutputDevice> outputDevices) {
        this.gamePrinter = gamePrinter;
        this.robotPrinter = robotPrinter;
        this.mapPrinter = mapPrinter;
        this.planetPrinter = planetPrinter;
        this.outputDevices = outputDevices;
    }


    /**
     * @return Print the complete player status formatted for the console.
     */

    public void printStatus() {
        outputDevices.forEach(p -> p.initializeOutput() );
        gamePrinter.printStatus();
        mapPrinter.printMap();
        robotPrinter.printRobotList();
        planetPrinter.printPlanetList();
        outputDevices.forEach(p -> p.flush() );
    }

}

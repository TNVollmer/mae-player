package thkoeln.dungeon.monte.printer.finderservices;

import thkoeln.dungeon.monte.printer.printables.PlanetPrintable;
import thkoeln.dungeon.monte.printer.printables.RobotPrintable;

import java.util.List;

/**
 * Defines the set of "finder and query" methods needed from a "RobotApplicationService" class, in order to properly
 * render every relevant aspect.
 */
public interface RobotFinderService {
    /**
     * @return The list of all robots currently alive
     */
    public List<? extends RobotPrintable> allLivingRobots();


    /**
     * @return the list of all living robots on a given planet.
     */
    public List<? extends RobotPrintable> livingRobotsOnPlanet( PlanetPrintable planetPrintable );

}

package thkoeln.dungeon.monte.printer.finderservices;

import thkoeln.dungeon.monte.printer.printables.PlanetPrintable;
import thkoeln.dungeon.monte.printer.printables.RobotPrintable;
import thkoeln.dungeon.monte.robot.domain.Robot;

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
     * @return all OWN robots currently alive, sorted by type
     */
    public List<Robot> allLivingOwnRobots();


    /**
     * @return all ENEMY robots currently alive, sorted by enemy
     */
    public List<Robot> allLivingEnemyRobots();

    
    /**
     * @return the list of all living robots on a given planet.
     */
    public List<? extends RobotPrintable> livingRobotsOnPlanet( PlanetPrintable planetPrintable );

}

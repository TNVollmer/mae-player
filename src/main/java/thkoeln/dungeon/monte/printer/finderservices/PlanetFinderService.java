package thkoeln.dungeon.monte.printer.finderservices;

import thkoeln.dungeon.monte.printer.printables.PlanetPrintable;

import java.util.List;

/**
 * Defines the set of "finder and query" methods needed from a "PlanetApplicationService" class, in order to properly
 * render every relevant aspect.
 */
public interface PlanetFinderService {

    /**
     * @return the list of all currently known planets
     */
    public List<? extends PlanetPrintable> allPlanets();


    /**
     * @return the list of all known planets that have been visited
     */
    public List<? extends PlanetPrintable> allVisitedPlanets();

    /**
     * @return the list of all currently known planets that were spawn points (on which a new robot has been
     * spawned). This is important for the map cluster construction algorithm.
     */
    public List<? extends PlanetPrintable> allSpawnPoints();

}

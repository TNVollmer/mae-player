package thkoeln.dungeon.monte.printer.finderservices;

import thkoeln.dungeon.monte.printer.printables.GamePrintable;

/**
 * Defines the set of "finder" methods needed from a "GameApplicationService" class, in order to properly
 * render every relevant aspect.
 */
public interface GameFinderService {
    /**
     * @return The currently available active (CREATED or RUNNING) game, or null if there is no such game
     */
    public GamePrintable queryActiveGame();
}

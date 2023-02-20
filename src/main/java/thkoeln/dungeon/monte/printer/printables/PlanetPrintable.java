package thkoeln.dungeon.monte.printer.printables;

import thkoeln.dungeon.monte.printer.util.MapDirection;

import java.util.Map;

/**
 * Defines the methods that a planet implementation needs to provide, so that this module can print it on the
 * supported output devices
 */
public interface PlanetPrintable extends MapPrintable {
    /**
     * @return neighbouring PlanetPrintables in each direction. Only real planets count, no black holes.
     * Null / no entry means "no known neighbour in this direction".
     *
     * NOTE: Must be consistent with hardBorders(). If neighbours() has a planet in direction d,
     * then hardBorders() must have the value FALSE in this direction.
     */
    public Map<MapDirection, PlanetPrintable> neighbours();

    /**
     * @return Information for each direction if there is a hard border (the edges of the map,
     * or a black hole).
     * TRUE = there is a hard border
     * FALSE = there is no hard border, and we are sure of it => there is a planet there.
     * Null = we don't know yet, maybe hard border, maybe not.
     *
     * NOTE: Must be consistent with neighbours(). If neighbours() has a planet in direction d,
     * then hardBorders() must have the value FALSE in this direction.
     */
    public Map<MapDirection, Boolean> hardBorders();


    /**
     * @return The mineable resource printable, if this planet _has_ a resource. Otherwise, just return null.
     */
    public MineableResourcePrintable mineableResourcePrintable();

    /**
     * @return true if the planet has already been visited, false otherwise
     */
    public boolean hasBeenVisited();
}

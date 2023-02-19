package thkoeln.dungeon.monte.printer.printables;

import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.printer.util.MapDirection;

import java.util.Map;
import java.util.Objects;

/**
 * Defines the methods that a planet implementation needs to provide, so that this module can print it on the
 * supported output devices
 */
public interface PlanetPrintable extends MapPrintable {
    /**
     * @return A map with all neighbouring PlanetPrintables, in each direction.
     */
    public Map<MapDirection, PlanetPrintable> neighbourMap();

    /**
     * @return The mineable resource printable, if this planet _has_ a resource. Otherwise, just return null.
     */
    public MineableResourcePrintable mineableResourcePrintable();

    /**
     * @return true if the planet has already been visited, false otherwise
     */
    public boolean hasBeenVisited();


    /**
     * @return true if this planet is a black hole, false otherwise
     */
    public boolean isBlackHole();


    public boolean equals( Object o );

    @Override
    public int hashCode();

}

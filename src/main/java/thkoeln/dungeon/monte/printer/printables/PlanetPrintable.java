package thkoeln.dungeon.monte.printer.printables;

import thkoeln.dungeon.monte.printer.util.MapDirection;

import java.util.Map;

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

}

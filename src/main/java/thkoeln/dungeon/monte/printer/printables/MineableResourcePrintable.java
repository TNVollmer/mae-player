package thkoeln.dungeon.monte.printer.printables;

import thkoeln.dungeon.monte.printer.util.MapDirection;

import java.util.Map;

/**
 * Defines the methods that a mineable resource implementation needs to provide, so that it can be printed on the
 * supported output devices
 */
public interface MineableResourcePrintable extends MapPrintable {
    /**
     * @return The relative value of a mineable resource, as an int value between 1 and 5
     */
    public int relativeValue();

}

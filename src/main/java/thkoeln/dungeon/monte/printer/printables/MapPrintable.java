package thkoeln.dungeon.monte.printer.printables;

/**
 * "Abstract" interface for everything that may appear on a map. String representation must be <= 4 chars.
 */
public interface MapPrintable extends Printable {
    /**
     * @return The short name of a planet when printed on a map.
     * IMPORTANT: Name must be <= 4 chars, otherwise the layout breaks.
     */
    public String mapName();
}

package thkoeln.dungeon.monte.printer.util;

/**
 * Direction on a print map.
 * We don't use the domain primitive "CompassDirection" here, in order not to create dependencies. This way,
 * the print module can be used independently.
 */
public enum MapDirection {
    no, ea, so, we;
}

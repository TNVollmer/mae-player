package thkoeln.dungeon.monte.printer.printables;

/**
 * "Abstract" top level interface for every entity that needs to be printed for status (player, game, robot, player)
 */
public interface Printable {
    /**
     * @return Detailed description of a printable entity - should fit in one line, but no constraints otherwise.
     */
    public String detailedDescription();
}

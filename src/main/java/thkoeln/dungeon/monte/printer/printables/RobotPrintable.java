package thkoeln.dungeon.monte.printer.printables;

/**
 * Defines the methods that a robot implementation needs to provide, so that this module can print it on the
 * supported output devices
 */
public interface RobotPrintable extends MapPrintable {
    /**
     * @return true if this robot is an enemy robot, false if it is one of us
     */
    public boolean isEnemy();

    /**
     * Enemy players are identified by a capital char (A, B, ...), which is used for color coding in the client.
     * @return the char belonging to the robot's player, if it is an enemy robot. Null if it is our own robot.
     */
    public Character enemyChar();
}

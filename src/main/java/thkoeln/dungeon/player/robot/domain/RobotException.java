package thkoeln.dungeon.player.robot.domain;

import thkoeln.dungeon.player.DungeonPlayerRuntimeException;

public class RobotException extends DungeonPlayerRuntimeException {
    public RobotException(String message) {
        super(message);
    }
}

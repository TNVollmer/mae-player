package thkoeln.dungeon.monte.robot.domain;

import thkoeln.dungeon.monte.DungeonPlayerRuntimeException;

public class RobotException extends DungeonPlayerRuntimeException {
    public RobotException(String message ) {
        super( message );
    }
}

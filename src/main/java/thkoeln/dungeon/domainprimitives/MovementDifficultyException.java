package thkoeln.dungeon.domainprimitives;

import thkoeln.dungeon.DungeonPlayerRuntimeException;

public class MovementDifficultyException extends DungeonPlayerRuntimeException {
    public MovementDifficultyException(String message ) {
        super( message );
    }
}

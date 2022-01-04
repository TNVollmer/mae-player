package thkoeln.dungeon.game.domain;

import thkoeln.dungeon.DungeonPlayerRuntimeException;

public class GameException extends DungeonPlayerRuntimeException {
    public GameException(String message ) {
        super( message );
    }
}

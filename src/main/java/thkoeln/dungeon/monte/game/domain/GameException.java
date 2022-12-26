package thkoeln.dungeon.monte.game.domain;

import thkoeln.dungeon.monte.DungeonPlayerRuntimeException;

public class GameException extends DungeonPlayerRuntimeException {
    public GameException(String message ) {
        super( message );
    }
}

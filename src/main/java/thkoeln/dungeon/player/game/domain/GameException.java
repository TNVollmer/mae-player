package thkoeln.dungeon.player.game.domain;

import thkoeln.dungeon.player.DungeonPlayerRuntimeException;

public class GameException extends DungeonPlayerRuntimeException {
    public GameException( String message ) {
        super( message );
    }
}

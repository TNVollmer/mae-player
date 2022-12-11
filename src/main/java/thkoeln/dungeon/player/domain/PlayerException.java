package thkoeln.dungeon.player.domain;

import thkoeln.dungeon.DungeonPlayerRuntimeException;

public class PlayerException extends DungeonPlayerRuntimeException {
    public PlayerException(String message ) {
        super( message );
    }
}

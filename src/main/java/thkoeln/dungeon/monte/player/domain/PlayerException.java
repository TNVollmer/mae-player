package thkoeln.dungeon.monte.player.domain;

import thkoeln.dungeon.monte.DungeonPlayerRuntimeException;

public class PlayerException extends DungeonPlayerRuntimeException {
    public PlayerException(String message ) {
        super( message );
    }
}

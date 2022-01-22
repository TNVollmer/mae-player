package thkoeln.dungeon.player.domain;

import thkoeln.dungeon.DungeonPlayerRuntimeException;

public class PlayerDomainException extends DungeonPlayerRuntimeException {
    public PlayerDomainException(String message ) {
            super( message );
        }
}

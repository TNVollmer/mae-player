package thkoeln.dungeon.player.domain;

import thkoeln.dungeon.DungeonPlayerRuntimeException;

public class CannotRegisterPlayerException extends DungeonPlayerRuntimeException {
    public CannotRegisterPlayerException(String message ) {
            super( message );
        }
}

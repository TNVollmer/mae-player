package thkoeln.dungeon.monte.player.domain;

import thkoeln.dungeon.monte.DungeonPlayerRuntimeException;

public class CannotRegisterPlayerException extends DungeonPlayerRuntimeException {
    public CannotRegisterPlayerException(String message ) {
            super( message );
        }
}

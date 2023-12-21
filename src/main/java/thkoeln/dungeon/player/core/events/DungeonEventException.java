package thkoeln.dungeon.player.core.events;

import thkoeln.dungeon.player.DungeonPlayerRuntimeException;

public class DungeonEventException extends DungeonPlayerRuntimeException {
    public DungeonEventException(String message ) {
        super( message );
    }
}

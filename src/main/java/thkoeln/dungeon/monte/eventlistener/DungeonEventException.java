package thkoeln.dungeon.monte.eventlistener;

import thkoeln.dungeon.monte.DungeonPlayerRuntimeException;

public class DungeonEventException extends DungeonPlayerRuntimeException {
    public DungeonEventException(String message ) {
        super( message );
    }
}

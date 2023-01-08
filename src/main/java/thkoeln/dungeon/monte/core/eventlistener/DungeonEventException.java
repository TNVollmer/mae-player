package thkoeln.dungeon.monte.core.eventlistener;

import thkoeln.dungeon.monte.DungeonPlayerRuntimeException;

public class DungeonEventException extends DungeonPlayerRuntimeException {
    public DungeonEventException(String message ) {
        super( message );
    }
}

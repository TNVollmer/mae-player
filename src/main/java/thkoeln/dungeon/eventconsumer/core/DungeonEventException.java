package thkoeln.dungeon.eventconsumer.core;

import thkoeln.dungeon.DungeonPlayerRuntimeException;

public class DungeonEventException extends DungeonPlayerRuntimeException {
    public DungeonEventException(String message ) {
        super( message );
    }
}

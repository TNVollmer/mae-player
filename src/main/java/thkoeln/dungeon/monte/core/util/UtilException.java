package thkoeln.dungeon.monte.core.util;

import thkoeln.dungeon.monte.DungeonPlayerRuntimeException;

public class UtilException extends DungeonPlayerRuntimeException {
    public UtilException( String message ) {
        super( message );
    }
    public UtilException( Exception e ) {
        super( e.getMessage() );
    }
}

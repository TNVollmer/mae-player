package thkoeln.dungeon.restadapter.exceptions;

import thkoeln.dungeon.DungeonPlayerException;
import thkoeln.dungeon.DungeonPlayerRuntimeException;

/**
 * This exception is thrown by the REST adapter if a connection could be established, but the answer is
 * unexpected (= does not fit to the API descriptions). It must be assumed that the GameService is currently
 * in some error state; we have to deal with this and try again later.
 */
public class UnexpectedRESTException extends DungeonPlayerException {
    public UnexpectedRESTException( String message ) {
        super( message );
    }
}

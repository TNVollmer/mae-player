package thkoeln.dungeon.restadapter.exceptions;

import thkoeln.dungeon.DungeonPlayerException;
import thkoeln.dungeon.DungeonPlayerRuntimeException;

/**
 * The connection to GameService could not be established (network failure or GameService down). The player
 * business logic needs to deal with this and try again later.
 */
public class RESTConnectionFailureException extends DungeonPlayerException {
    public RESTConnectionFailureException( String endPoint, String message ) {
        super(  "Unexpected error in communication with GameService calling " + endPoint + " endpoint. Message: "
                + message + "\nNeed to retry later." );
    }
}

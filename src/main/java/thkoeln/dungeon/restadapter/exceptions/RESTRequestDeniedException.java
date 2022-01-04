package thkoeln.dungeon.restadapter.exceptions;

import thkoeln.dungeon.DungeonPlayerException;
import thkoeln.dungeon.DungeonPlayerRuntimeException;

/**
 * This exception is thrown if the REST adapter denies any kind of player request, but the answer is in
 * accordance with the API spec. Example: We try to register a player for a game, but the game is already full,
 * or we missed the registration time window.
 */
public class RESTRequestDeniedException extends DungeonPlayerException {
    public RESTRequestDeniedException(String message ) {
        super( message );
    }
}

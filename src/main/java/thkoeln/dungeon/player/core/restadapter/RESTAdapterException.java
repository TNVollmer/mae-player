package thkoeln.dungeon.player.core.restadapter;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import thkoeln.dungeon.player.DungeonPlayerRuntimeException;

/**
 * The connection to GameService could not be established (network failure or GameService down). The player
 * business logic needs to deal with this and try again later.
 */
@Getter
public class RESTAdapterException extends DungeonPlayerRuntimeException {
    private String endPoint;
    private HttpStatus returnValue;

    public RESTAdapterException( String message ) {
        super( message );
    }

    public RESTAdapterException( String endPoint, String message, HttpStatus returnValue ) {
        super( "Error in communication with GameService calling endpoint '" + endPoint + "'. Message:\n\t"
                + message + "\n\tReturn value: " + ((returnValue == null) ? "unknown" : returnValue));
        this.endPoint = endPoint;
        this.returnValue = returnValue;
    }

    public RESTAdapterException( String endPoint, RuntimeException originalException ) {
        super( "Error in communication with GameService calling endpoint '" + endPoint + "'. Message:\n\t"
                + originalException.getMessage() );
    }
}

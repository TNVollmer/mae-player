package thkoeln.dungeon.domainprimitives;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import thkoeln.dungeon.DungeonPlayerException;
import thkoeln.dungeon.DungeonPlayerRuntimeException;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Invalid coordinate data")
public class CoordinateException extends DungeonPlayerRuntimeException {
    public CoordinateException(String message ) {
        super( message );
    }
}

package thkoeln.dungeon.domainprimitives;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import thkoeln.dungeon.DungeonPlayerRuntimeException;

public class TwoDimDynamicArrayException extends DungeonPlayerRuntimeException {
    public TwoDimDynamicArrayException(String message ) {
        super( message );
    }
}

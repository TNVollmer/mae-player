package thkoeln.dungeon.monte.domainprimitives;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import thkoeln.dungeon.monte.DungeonPlayerRuntimeException;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Invalid domain primitive")
public class DomainPrimitiveException extends DungeonPlayerRuntimeException {
    public DomainPrimitiveException(String message ) {
        super( message );
    }
}

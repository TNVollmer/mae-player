package thkoeln.dungeon.domainprimitives;

import thkoeln.dungeon.DungeonPlayerRuntimeException;

public class MineableResourceException extends DungeonPlayerRuntimeException {
    public MineableResourceException(String message ) {
        super( message );
    }
}

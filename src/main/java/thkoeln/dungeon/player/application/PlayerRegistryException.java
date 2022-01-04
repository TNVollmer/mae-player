package thkoeln.dungeon.player.application;

import thkoeln.dungeon.DungeonPlayerRuntimeException;

public class PlayerRegistryException extends DungeonPlayerRuntimeException {
    public PlayerRegistryException(String message ) {
        super( message );
    }
}

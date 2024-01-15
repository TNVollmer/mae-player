package thkoeln.dungeon.player.player.domain;

import thkoeln.dungeon.player.DungeonPlayerRuntimeException;

public class PlayerException extends DungeonPlayerRuntimeException {
    public PlayerException(String message) {
        super(message);
    }
}

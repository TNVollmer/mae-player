package thkoeln.dungeon.monte.core.util;

import java.util.UUID;

/**
 * Used to obtain general player information that are needed e.g. for creating commands.
 * We use a Dependency Inversion pattern here: Modules can autowire this interface,
 * while the implementation is contributed by Player. Without Dependency Inversion,
 * this would cause cycles.
 */
public interface PlayerInformation {
    public UUID currentGameId();
    public UUID currentPlayerId();
}

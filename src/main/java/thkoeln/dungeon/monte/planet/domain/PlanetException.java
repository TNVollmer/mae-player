package thkoeln.dungeon.monte.planet.domain;

import thkoeln.dungeon.monte.DungeonPlayerRuntimeException;

public class PlanetException extends DungeonPlayerRuntimeException {
    public PlanetException( String message ) {
        super( message );
    }
}

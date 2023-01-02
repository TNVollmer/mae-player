package thkoeln.dungeon.monte.core.strategy;

import thkoeln.dungeon.monte.DungeonPlayerRuntimeException;

public class StrategyException extends DungeonPlayerRuntimeException {
    public StrategyException(String message ) {
        super( message );
    }
    public StrategyException(Exception e ) {
        super( e.getMessage() );
    }
}

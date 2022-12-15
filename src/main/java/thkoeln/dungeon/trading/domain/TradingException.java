package thkoeln.dungeon.trading.domain;

import thkoeln.dungeon.DungeonPlayerRuntimeException;

public class TradingException extends DungeonPlayerRuntimeException {
    public TradingException(String message ) {
        super( message );
    }
}

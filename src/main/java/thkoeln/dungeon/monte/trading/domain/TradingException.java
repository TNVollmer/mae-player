package thkoeln.dungeon.monte.trading.domain;

import thkoeln.dungeon.monte.DungeonPlayerRuntimeException;

public class TradingException extends DungeonPlayerRuntimeException {
    public TradingException(String message ) {
        super( message );
    }
}

package thkoeln.dungeon.monte.core.eventlistener;

import java.util.Arrays;

public enum EventType {
    GAME_STATUS( "game-status" ),
    BANK_INITIALIZED( "BankAccountInitialized" ),
    BANK_ACCOUNT_TRANSACTION_BOOKED( "BankAccountTransactionBooked" ),
    ROUND_STATUS( "round-status" ),
    TRADABLE_PRICES( "TradablePrices" ),
    ROBOT_SPAWNED( "RobotSpawned" ),
    ROBOT_MOVED( "RobotMoved" ),
    ROBOT_REGENERATED( "RobotRegenerated" ),
    ROBOT_REVEALED( "RobotsRevealed" ),
    ROBOT_RESOURCE_MINED( "RobotResourceMined" ),
    PLANET_DISCOVERED( "PlanetDiscovered" ),


    ERROR( "error" ),
    UNKNOWN( "UNKNOWN" );

    private final String stringValue;

    private EventType( String s ) {
        stringValue = s;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static EventType findByStringValue( final String stringValue ){
        return Arrays.stream(values()).filter(value -> value.getStringValue().equals( stringValue )).findFirst()
                .orElse( UNKNOWN );
    }

    public boolean isRobotRelated() {
        if ( this.equals( GAME_STATUS ) || this.equals( BANK_INITIALIZED ) || this.equals( ROUND_STATUS ) ||
                this.equals( TRADABLE_PRICES ) || this.equals(ROBOT_REVEALED) ||
                this.equals( BANK_ACCOUNT_TRANSACTION_BOOKED ) ||
                this.equals( UNKNOWN ) || this.equals( ERROR ) )
            return false;
        if ( isPlanetRelated() ) return false;
        return true;
    }

    public boolean isPlanetRelated() {
        if ( this.equals( PLANET_DISCOVERED ) ) return true;
        return false;
    }
}

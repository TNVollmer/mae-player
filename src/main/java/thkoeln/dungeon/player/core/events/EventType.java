package thkoeln.dungeon.player.core.events;

import java.util.Arrays;

public enum EventType {
    GAME_STATUS( "GameStatus" ),
    ROUND_STATUS( "RoundStatus" ),
    BANK_INITIALIZED( "BankAccountInitialized" ),
    BANK_ACCOUNT_TRANSACTION_BOOKED( "BankAccountTransactionBooked" ),
    BANK_ACCOUNT_CLEARED( "BankAccountCleared" ),
    ROBOT_SPAWNED( "RobotSpawned" ),
    ROBOT_MOVED( "RobotMoved" ),
    ROBOT_REGENERATED( "RobotRegenerated" ),
    ROBOT_REVEALED( "RobotsRevealed" ),
    ROBOT_RESOURCE_MINED( "RobotResourceMined" ),
    ROBOT_RESOURCE_REMOVED( "RobotResourceRemoved" ),
    ROBOT_UPGRADED( "RobotUpgraded" ),
    ROBOT_ATTACKED( "RobotAttacked" ),
    ROBOT_RESTORED_ATTRIBUTES( "RobotRestoredAttributes" ),
    PLANET_DISCOVERED( "PlanetDiscovered" ),
    TRADABLE_BOUGHT( "TradableBought" ),
    TRADABLE_SOLD( "TradableSold" ),
    TRADABLE_PRICES( "TradablePrices" ),
    RESOURCE_MINED( "ResourceMined" ),


    ERROR( "error" ),
    UNKNOWN( "UNKNOWN" );

    private final String stringValue;

    EventType( String s ) {
        stringValue = s;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static EventType findByStringValue( final String stringValue ) {
        return Arrays.stream( values() ).filter( value -> value.getStringValue().equals( stringValue ) ).findFirst()
                .orElse( UNKNOWN );
    }
}

package thkoeln.dungeon.eventlistener;

import java.util.Arrays;

public enum EventType {
    GAME_STATUS( "game-status" ),
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
        if ( this.equals( GAME_STATUS ) || this.equals( UNKNOWN ) ) return false;
        return true;
    }
}

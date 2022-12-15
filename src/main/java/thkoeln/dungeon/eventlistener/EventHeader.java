package thkoeln.dungeon.eventlistener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.UUID;

@Embeddable
@Getter
@Setter ( AccessLevel.PROTECTED )
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class EventHeader {
    public static final String EVENT_ID_KEY = "eventId";
    public static final String TRANSACTION_ID_KEY = "transactionId";
    public static final String PLAYER_ID_KEY = "playerId";

    // Player can be the string "public" instead of a real player ID; this means that the event goes to
    // all players (a broadcast), instead of just to one specific player.
    public static final String BROADCAST_EVENT_KEY = "public";
    public static final String TYPE_KEY = "type";
    public static final String VERSION_KEY = "version";
    public static final String TIMESTAMP_KEY = "timestamp";

    @Transient
    private Logger logger = LoggerFactory.getLogger( EventHeader.class );

    private UUID eventId = null;
    private UUID transactionId = null;
    private UUID playerId = null;
    private boolean broadcast;
    private EventType eventType = EventType.UNKNOWN;
    private String eventTypeString = null;
    private String version = null;
    private String timestampString = null;


    public EventHeader (
            String type, String eventIdStr, String playerIdStr, String transactionIdStr,
            String timestampStr, String version ) {
        try {
            if ( eventIdStr != null) setEventId( UUID.fromString( eventIdStr ) );
            if ( transactionIdStr != null) setTransactionId( UUID.fromString( transactionIdStr ) );
            if ( BROADCAST_EVENT_KEY.equals( playerIdStr ) ) {
                setBroadcast( true );
            }
            else {
                setBroadcast( false );
                if ( playerIdStr != null) setPlayerId( UUID.fromString( playerIdStr ) );
            }
        }
        catch ( IllegalArgumentException e ) {
            logger.error( "Unexpected error at converting UUIDs in event header: " +
                    eventIdStr + ", " + transactionIdStr, ", " + playerIdStr );
        }
        setTimestampString( timestampStr );
        setVersion( version );
        setEventType( EventType.findByStringValue( type ) );
        setEventTypeString( type );
        logger.info( "Created event " + this );
    }



    @Override
    public String toString() {
        return "Header:" +
                ", {eventType=" + eventType +
                ", eventTypeString=" + eventTypeString + "\n\t" +
                ", transactionId=" + transactionId +
                ", eventId=" + eventId +
                ", playerId=" + playerId +
                ", isBroadcast=" + isBroadcast() +
                ", eventType=" + eventType +
                ", eventTypeString=" + eventTypeString +
                ", version='" + version + '\'' +
                ", timestampString='" + timestampString + '\'' + "}";
    }


}

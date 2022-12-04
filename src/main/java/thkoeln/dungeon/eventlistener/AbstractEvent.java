package thkoeln.dungeon.eventlistener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.LongString;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public abstract class AbstractEvent {
    public static final String EVENT_ID_KEY = "eventId";
    public static final String TRANSACTION_ID_KEY = "transactionId";
    public static final String PLAYER_ID_KEY = "playerId";
    public static final String TYPE_KEY = "type";
    public static final String VERSION_KEY = "version";
    public static final String TIMESTAMP_KEY = "timestamp";
    public static final String MSG_BODY_KEY = "messageBody";

    @Transient
    private static final List<String> allKeys =
            List.of( EVENT_ID_KEY, TRANSACTION_ID_KEY, PLAYER_ID_KEY, TYPE_KEY, VERSION_KEY, TIMESTAMP_KEY );

    @Transient
    protected Logger logger = LoggerFactory.getLogger( AbstractEvent.class );

    @Id
    @Setter( AccessLevel.NONE )
    protected UUID localId = UUID.randomUUID();

    protected UUID eventId;
    protected UUID transactionId;
    protected UUID playerId;
    protected String type;
    protected String version;
    protected String timestampString;

    protected String messageBodyAsJson;

    @Getter ( AccessLevel.NONE ) // just because Lombok generates the ugly getProcessed()
    protected Boolean processed = Boolean.FALSE;
    public Boolean hasBeenProcessed() { return processed; }


    /**
     * @return true if the event was complete and consistent (enough) in order to be processed, false otherwise.
     * This is for the implementing concrete subclass to decide.
     */
    public abstract boolean isValid();


    public AbstractEvent( Delivery deliveredMessage ) {
        Map<String, String> properties = extractPropertiesAsStrings( deliveredMessage );
        try {
            setEventId( UUID.fromString( properties.get( EVENT_ID_KEY ) ) );
            setTransactionId( UUID.fromString( properties.get( TRANSACTION_ID_KEY ) ) );
            setPlayerId( UUID.fromString( properties.get( PLAYER_ID_KEY ) ) );
        }
        catch ( IllegalArgumentException e ) {
            logger.error( "AbstractEvent: encountered invalid UUID. " + e );
        }
        setType( properties.get( TYPE_KEY ) );
        setVersion( properties.get( VERSION_KEY ) );
        setTimestampString( properties.get( TIMESTAMP_KEY ) );
        setMessageBodyAsJson( properties.get( MSG_BODY_KEY ));
    }


    private Map<String, String> extractPropertiesAsStrings( Delivery deliveredMessage ) {
        HashMap<String, String> properties = new HashMap<>();
        BasicProperties messageProperties = deliveredMessage.getProperties();
        for ( String key : allKeys ) {
            byte[] bytes = valueOrMissing( messageProperties, key );
            properties.put( key, new String( bytes, StandardCharsets.UTF_8 ) );
        }
        properties.put( MSG_BODY_KEY, deliveredMessage.getBody().toString() );
        return properties;
    }


    private byte[] valueOrMissing(BasicProperties properties, String key) {
        try {
            return properties.getHeaders().get(key) == null ? ("Missing").getBytes(StandardCharsets.UTF_8) : ((byte[]) properties.getHeaders().get(key));
        } catch ( ClassCastException e1 ) {
            try {
                return properties.getHeaders().get(key) == null ? ("Missing").getBytes( StandardCharsets.UTF_8 ) :
                        ((LongString) properties.getHeaders().get(key)).getBytes();
            } catch ( ClassCastException e2 ) {
                logger.error( "AbstractEvent: Unexpected class cast exception: " + e2 );
                return ("Error").getBytes( StandardCharsets.UTF_8 );
            }
        }
    }


    protected void fillFromMessageBody( Class theConcreteEventSubclass ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
            objectMapper.readValue( messageBodyAsJson, theConcreteEventSubclass );
        }
        catch( JsonProcessingException conversionFailed ) {
            logger.error( theConcreteEventSubclass.getName() +
                    ": Error converting payload for event with messageBodyAsJson " + messageBodyAsJson );
        }
    }

    @Override
    public String toString() {
        return this.getClass().getName() +
                " {localId=" + localId +
                ", eventId=" + eventId +
                ", transactionId=" + transactionId +
                ", playerId=" + playerId +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", timestampString='" + timestampString + '\'' +
                ", processed=" + processed + "}";
    }
}

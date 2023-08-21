package thkoeln.dungeon.player.core.eventlistener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.*;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public abstract class AbstractEvent {
    @Id
    @Setter( AccessLevel.NONE )
    protected UUID localId = UUID.randomUUID();

    @Transient
    protected Logger logger = LoggerFactory.getLogger( AbstractEvent.class );

    @Embedded
    protected EventHeader eventHeader;
    protected String messageBodyAsJson;

    @Getter ( AccessLevel.NONE ) // just because Lombok generates the ugly getProcessed()
    protected Boolean processed = Boolean.FALSE;
    public Boolean hasBeenProcessed() { return processed; }

    /**
     * @return true if the event was complete and consistent (enough) in order to be processed, false otherwise.
     * This is for the implementing concrete subclass to decide.
     */
    public abstract boolean isValid();

    public void fillWithPayload( String jsonString ) {
        messageBodyAsJson = jsonString;
        try {
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
            objectMapper.readerForUpdating( this ).readValue( jsonString );
        }
        catch( JsonProcessingException conversionFailed ) {
            logger.warn( "Cannot convert payload for event with jsonString " + jsonString );
        }
    }


    public boolean isUnknown() {
        return ( eventHeader == null || (eventHeader.getEventType() == EventType.UNKNOWN) );
    }

    @Override
    public String toString() {
        String payloadString = "PAYLOAD: " + String.valueOf( messageBodyAsJson );
        // wrap lines after 150 chars
        payloadString = payloadString.replaceAll("(.{150})", "$1\n\t" );

        return this.getClass().getSimpleName() + " - " +
            String.valueOf( eventHeader ) + "\n" + payloadString;
    }

    public String toStringShort() {
        if ( isUnknown() ) return toString();
        return eventHeader.getEventType().toString();
    }
}

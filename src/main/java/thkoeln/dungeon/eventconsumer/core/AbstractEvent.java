package thkoeln.dungeon.eventconsumer.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessageHeaders;
import thkoeln.dungeon.player.application.PlayerApplicationService;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public abstract class AbstractEvent {
    @Id
    @Setter( AccessLevel.NONE )
    protected UUID id = UUID.randomUUID();
    protected UUID eventId;
    protected Long timestamp;
    protected UUID transactionId;

    @Transient
    protected Logger logger = LoggerFactory.getLogger(PlayerApplicationService.class);

    public static final String TRANSACTION_ID_KEY = "transactionId";

    public AbstractEvent( MessageHeaders messageHeaders ) {
        setEventId( messageHeaders.getId() );
        setTimestamp( messageHeaders.getTimestamp() ) ;
        try {
            setTransactionId(UUID.fromString(String.valueOf(messageHeaders.get(TRANSACTION_ID_KEY))));
        }
        catch ( IllegalArgumentException e ) {
            logger.warn( "Event " + eventId + " at time " + timestamp + " doesn't have a transactionId." );
        }
    }
}

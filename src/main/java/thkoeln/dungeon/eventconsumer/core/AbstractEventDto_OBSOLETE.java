package thkoeln.dungeon.eventconsumer.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessageHeaders;
import thkoeln.dungeon.game.domain.GameStatus;
import thkoeln.dungeon.player.application.PlayerApplicationService;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public abstract class AbstractEventDto_OBSOLETE {
    protected UUID eventId;
    protected Long timestamp;
    protected UUID transactionId;
    protected Logger logger = LoggerFactory.getLogger(PlayerApplicationService.class);

    private static final String TRANSACTION_ID_KEY = "transactionId";

    public AbstractEventDto_OBSOLETE(MessageHeaders messageHeaders ) {
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

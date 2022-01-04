package thkoeln.dungeon.eventconsumer.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.messaging.MessageHeaders;
import thkoeln.dungeon.eventconsumer.core.AbstractEventDto_OBSOLETE;
import thkoeln.dungeon.game.domain.GameStatus;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameStatusEventDto_OBSOLETE extends AbstractEventDto_OBSOLETE {
    private UUID gameId;
    private GameStatus gameStatus;

    private static final String TYPE_KEY = "type";
    private static final String GAME_ID_KEY = "gameId";

    public GameStatusEventDto_OBSOLETE(MessageHeaders messageHeaders ) {
        super( messageHeaders );
        try {
            setGameId( UUID.fromString( String.valueOf( messageHeaders.get( TYPE_KEY ) ) ) );
        }
        catch ( IllegalArgumentException e ) {
            logger.warn( "GameStatusEvent " + super.eventId + " at time " + super.timestamp + " doesn't have a gameId." );
        }
    }
}

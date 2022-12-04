package thkoeln.dungeon.game.application.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Delivery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.eventlistener.AbstractEvent;
import thkoeln.dungeon.game.domain.GameStatus;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor ( access = AccessLevel.PROTECTED )
public class GameStatusEvent extends AbstractEvent {
    private UUID gameId;
    private GameStatus status;

    public GameStatusEvent( Delivery deliveryMessage ) {
        super( deliveryMessage );
        super.fillFromMessageBody( this.getClass() );
    }

    public boolean isValid() {
        return ( gameId != null && status != null );
    }
}

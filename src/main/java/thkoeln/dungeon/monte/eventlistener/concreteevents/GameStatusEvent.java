package thkoeln.dungeon.monte.eventlistener.concreteevents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import thkoeln.dungeon.monte.eventlistener.AbstractEvent;
import thkoeln.dungeon.monte.game.domain.GameStatus;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GameStatusEvent extends AbstractEvent {
    private UUID gameId;
    private GameStatus status;

    public boolean isValid() {
        return ( gameId != null && status != null );
    }
}

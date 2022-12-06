package thkoeln.dungeon.eventlistener.gameevents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import thkoeln.dungeon.eventlistener.AbstractEvent;
import thkoeln.dungeon.game.domain.GameStatus;

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

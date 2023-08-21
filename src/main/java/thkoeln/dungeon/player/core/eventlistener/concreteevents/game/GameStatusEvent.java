package thkoeln.dungeon.player.core.eventlistener.concreteevents.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import thkoeln.dungeon.player.core.eventlistener.AbstractEvent;
import thkoeln.dungeon.player.game.domain.GameStatus;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GameStatusEvent extends AbstractEvent {
    private UUID gameId;
    private UUID gameworldId;
    private GameStatus status;

    @Override
    public boolean isValid() {
        return ( gameId != null && status != null );
    }

    @Override
    public String toStringShort() {
        return super.toStringShort() + " (" + String.valueOf( status ) + ")";
    }
}

package thkoeln.dungeon.eventlistener.concreteevents;

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
public class UnknownEvent extends AbstractEvent {
    public boolean isValid() {
        return true;
    }
}

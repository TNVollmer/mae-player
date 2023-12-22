package thkoeln.dungeon.player.core.events.concreteevents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.events.AbstractEvent;

@Getter
@Setter
@NoArgsConstructor
public class ErrorEvent extends AbstractEvent {
    public boolean isValid() {
        return true;
    }
}

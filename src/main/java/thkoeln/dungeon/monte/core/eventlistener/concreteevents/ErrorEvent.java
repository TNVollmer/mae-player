package thkoeln.dungeon.monte.core.eventlistener.concreteevents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.core.eventlistener.AbstractEvent;

@Getter
@Setter
@NoArgsConstructor
public class ErrorEvent extends AbstractEvent {
    public boolean isValid() {
        return true;
    }
}

package thkoeln.dungeon.monte.eventlistener.concreteevents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.eventlistener.AbstractEvent;

@Getter
@Setter
@NoArgsConstructor
public class ErrorEvent extends AbstractEvent {
    public boolean isValid() {
        return true;
    }
}

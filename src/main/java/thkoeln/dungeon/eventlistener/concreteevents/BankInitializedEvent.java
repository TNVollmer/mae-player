package thkoeln.dungeon.eventlistener.concreteevents;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.eventlistener.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BankInitializedEvent extends AbstractEvent {
    private UUID playerId;
    private Integer balance;

    public boolean isValid() {
        return ( playerId != null && balance != null );
    }
}

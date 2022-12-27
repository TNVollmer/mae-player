package thkoeln.dungeon.monte.eventlistener.concreteevents.trading;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.eventlistener.AbstractEvent;

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

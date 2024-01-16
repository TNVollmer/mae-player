package thkoeln.dungeon.player.core.events.concreteevents.trading;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.events.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BankAccountClearedEvent extends AbstractEvent {

    private UUID playerId;
    private Integer balance;

    @Override
    public boolean isValid() {
        if ( eventHeader == null ) return false;
        return ( playerId != null && balance != null );
    }

}

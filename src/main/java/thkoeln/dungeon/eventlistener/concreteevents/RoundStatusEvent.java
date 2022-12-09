package thkoeln.dungeon.eventlistener.concreteevents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.eventlistener.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RoundStatusEvent extends AbstractEvent {
    private UUID gameId;
    private UUID roundId;
    private Integer roundNumber;
    private String roundStatus;

    public boolean isValid() {
        return ( roundStatus != null && gameId != null );
    }
}

package thkoeln.dungeon.monte.eventlistener.concreteevents.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.eventlistener.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoundStatusEvent extends AbstractEvent {
    private UUID gameId;
    private UUID roundId;
    private Integer roundNumber;
    private RoundStatusType roundStatus;

    public boolean isValid() {
        return ( roundStatus != null && roundNumber != null && gameId != null );
    }
}

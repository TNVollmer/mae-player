package thkoeln.dungeon.__obsololete__eventconsumer.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.__obsololete__eventconsumer.core.AbstractEvent;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor ( access = AccessLevel.PROTECTED )
public class RoundStatusEvent extends AbstractEvent {
    private UUID gameId;
    private UUID roundId;
    private Integer roundNumber;
    private String status;

    public boolean isValid() {
        return ( gameId != null && roundNumber != null && status != null );
    }
}

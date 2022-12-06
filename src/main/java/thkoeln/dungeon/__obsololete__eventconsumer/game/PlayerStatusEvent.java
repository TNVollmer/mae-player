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
public class PlayerStatusEvent extends AbstractEvent {
    private UUID playerId;

    public boolean isValid() {
        return ( playerId != null );
    }
}

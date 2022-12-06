package thkoeln.dungeon.__obsololete__eventconsumer.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.__obsololete__eventconsumer.core.AbstractEvent;
import thkoeln.dungeon.game.domain.GameStatus;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor ( access = AccessLevel.PROTECTED )
public class __OBSOLETE_GameStatusEvent extends AbstractEvent {
    private UUID gameId;
    private GameStatus status;

    public boolean isValid() {
        return ( gameId != null && status != null );
    }
}

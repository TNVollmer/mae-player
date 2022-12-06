package thkoeln.dungeon.__obsololete__eventconsumer.trading;

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
public class BankCreatedEvent extends AbstractEvent {
    private UUID playerId;
    private Integer money;

    public boolean isValid() {
        return ( playerId != null && money != null );
    }
}

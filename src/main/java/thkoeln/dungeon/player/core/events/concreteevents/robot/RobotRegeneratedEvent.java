package thkoeln.dungeon.player.core.events.concreteevents.robot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.events.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RobotRegeneratedEvent extends AbstractEvent {
    private UUID robotId;
    private Integer availableEnergy;

    @Override
    public boolean isValid() {
        if ( eventHeader == null ) return false;
        return ( robotId != null && availableEnergy != null );
    }
}

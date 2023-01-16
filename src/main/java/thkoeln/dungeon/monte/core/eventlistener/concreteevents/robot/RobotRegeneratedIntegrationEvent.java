package thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.core.eventlistener.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RobotRegeneratedIntegrationEvent  extends AbstractEvent {
    private UUID robotId;
    private Integer availableEnergy;

    @Override
    public boolean isValid() {
        if ( eventHeader == null ) return false;
        return ( robotId != null && availableEnergy != null );
    }
}

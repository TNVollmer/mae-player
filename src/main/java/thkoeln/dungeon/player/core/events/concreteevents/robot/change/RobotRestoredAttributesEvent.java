package thkoeln.dungeon.player.core.events.concreteevents.robot.change;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.events.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RobotRestoredAttributesEvent extends AbstractEvent {
    private UUID robotId;
    private String restorationType;
    private Integer availableEnergy;
    private Integer availableHealth;

    @Override
    public boolean isValid() {
        if ( eventHeader == null ) return false;
        return ( robotId != null && availableEnergy != null && availableHealth != null && restorationType != null );
    }
}

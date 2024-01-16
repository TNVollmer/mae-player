package thkoeln.dungeon.player.core.events.concreteevents.robot.change;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.events.AbstractEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotDto;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RobotUpgradedEvent extends AbstractEvent {
    private UUID robotId;
    private Integer level;
    private String upgrade;
    private RobotDto robotDto;

    @Override
    public boolean isValid() {
        if ( eventHeader == null ) return false;
        return ( robotId != null && level != null && upgrade != null && robotDto != null );
    }
}

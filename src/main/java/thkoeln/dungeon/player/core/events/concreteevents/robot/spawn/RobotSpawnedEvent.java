package thkoeln.dungeon.player.core.events.concreteevents.robot.spawn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.events.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RobotSpawnedEvent extends AbstractEvent {
    private UUID playerId;
    @JsonProperty("robot")
    private RobotDto robotDto;

    @Override
    public boolean isValid() {
        if ( eventHeader == null ) return false;
        if ( robotDto == null ) return false;
        return robotDto.isValid();
    }
}

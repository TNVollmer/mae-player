package thkoeln.dungeon.eventlistener.concreteevents.robot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.eventlistener.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RobotSpawnedEvent extends AbstractEvent {
    private UUID playerId;
    private RobotDto robot;

    @Override
    public boolean isValid() {
        return true;
    }
}

package thkoeln.dungeon.player.core.events.concreteevents.robot.fight;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.events.AbstractEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotDto;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RobotAttackedEvent extends AbstractEvent {
    private RobotFightResultDto attacker;
    private RobotFightResultDto target;

    @Override
    public boolean isValid() {
        if (eventHeader == null) return false;
        return (attacker != null && target != null);
    }
}

package thkoeln.dungeon.player.core.events.concreteevents.robot.reveal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.events.AbstractEvent;

@Getter
@Setter
@NoArgsConstructor
public class RobotsRevealedEvent extends AbstractEvent {
    private RobotRevealedDto[] robots;

    @Override
    public boolean isValid() {
        if ( robots == null ) return true;
        for ( RobotRevealedDto robotRevealedDto : robots ) {
            if ( !robotRevealedDto.isValid() ) return false;
        }
        return true;
    }
}

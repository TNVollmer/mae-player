package thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.core.eventlistener.AbstractEvent;

@Getter
@Setter
@NoArgsConstructor
public class RobotsRevealedIntegrationEvent extends AbstractEvent {
    private RobotRevealedDto[] robots;

    @Override
    public boolean isValid() {
        for ( RobotRevealedDto robotRevealedDto : robots ) {
            if ( !robotRevealedDto.isValid() ) return false;
        }
        return true;
    }
}

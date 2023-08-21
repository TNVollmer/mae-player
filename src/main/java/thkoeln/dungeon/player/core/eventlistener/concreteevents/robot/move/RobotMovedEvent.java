package thkoeln.dungeon.player.core.eventlistener.concreteevents.robot.move;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.eventlistener.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RobotMovedEvent extends AbstractEvent {
    private UUID robotId;
    private Integer remainingEnergy;
    private RobotMovePlanetDto fromPlanet;
    private RobotMovePlanetDto toPlanet;

    @Override
    public boolean isValid() {
        if ( eventHeader == null ) return false;
        if ( remainingEnergy == null ) return false;
        if ( fromPlanet == null ) return false;
        if ( toPlanet == null ) return false;
        return ( fromPlanet.isValid() && toPlanet.isValid() );
    }
}

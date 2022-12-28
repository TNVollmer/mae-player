package thkoeln.dungeon.monte.eventlistener.concreteevents.planet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.eventlistener.AbstractEvent;
import thkoeln.dungeon.monte.eventlistener.concreteevents.robot.RobotDto;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlanetDiscoveredEvent extends AbstractEvent {
    @JsonProperty("planet")
    private UUID planetId;

    @JsonProperty("movement_difficulty")
    private Integer movementDifficulty;

    private PlanetNeighboursDto[] neighbours = new PlanetNeighboursDto[0];
    private PlanetResourceDto resource;

    @Override
    public boolean isValid() {
        if ( eventHeader == null ) return false;
        if ( planetId == null ) return false;
        if ( movementDifficulty == null ) return false;
        if ( movementDifficulty < 0 ) return false;
        if ( neighbours == null ) return false;
        for ( PlanetNeighboursDto neighbour : neighbours ) {
            if ( !neighbour.isValid() ) return false;
        }
        if ( resource != null && !resource.isValid() ) return false;
        return true;
    }
}

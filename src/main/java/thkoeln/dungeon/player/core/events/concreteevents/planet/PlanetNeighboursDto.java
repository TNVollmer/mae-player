package thkoeln.dungeon.player.core.events.concreteevents.planet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import thkoeln.dungeon.player.core.domainprimitives.location.CompassDirection;

import java.util.UUID;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanetNeighboursDto {
    private UUID id;
    private CompassDirection direction;

    public boolean isValid() {
        if (id == null) return false;
        return direction != null;
    }
}

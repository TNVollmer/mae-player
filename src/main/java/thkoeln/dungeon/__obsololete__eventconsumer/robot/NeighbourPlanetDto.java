package thkoeln.dungeon.__obsololete__eventconsumer.robot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.domainprimitives.CompassDirection;

import javax.persistence.Embeddable;
import java.util.UUID;

/**
 * Planet information in the "neighbours" event sent by Robot service
 */
@Embeddable
@Setter
@Getter
@NoArgsConstructor
public class NeighbourPlanetDto {
    private UUID planetId;
    private Integer movementDifficulty;
    private CompassDirection direction;

    @Override
    public String toString() {
        return direction + ": " + planetId + ", " + movementDifficulty + ", ";
    }
}

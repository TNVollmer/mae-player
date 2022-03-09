package thkoeln.dungeon.eventconsumer.robot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.util.UUID;

/**
 * Planet information in the "movement" event sent by Robot service
 */
@Embeddable
@Setter
@Getter
@NoArgsConstructor
public class MovedToPlanetDto {
    private UUID planetId;
    private Integer movementDifficulty;
    private String planetType;
    private String resourceType;
}

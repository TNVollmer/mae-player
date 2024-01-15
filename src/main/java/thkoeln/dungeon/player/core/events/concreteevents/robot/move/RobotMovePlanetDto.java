package thkoeln.dungeon.player.core.events.concreteevents.robot.move;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RobotMovePlanetDto {
    private UUID id;
    private Integer movementDifficulty;

    public boolean isValid() {
        return (id != null && movementDifficulty != null && movementDifficulty >= 0);
    }
}

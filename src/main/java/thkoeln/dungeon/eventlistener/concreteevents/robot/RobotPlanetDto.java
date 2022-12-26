package thkoeln.dungeon.eventlistener.concreteevents.robot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RobotPlanetDto {
    private UUID planetId;
    private UUID gameWorldId;
    private Integer movementDifficulty;
    private String resourceType;
}

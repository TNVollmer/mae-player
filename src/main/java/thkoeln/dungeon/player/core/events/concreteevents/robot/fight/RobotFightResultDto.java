package thkoeln.dungeon.player.core.events.concreteevents.robot.fight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RobotFightResultDto {
    private UUID robotId;
    private Boolean alive;
    private Integer availableEnergy;
    private Integer availableHealth;

    public boolean isValid() {
        return ( robotId != null && alive != null && availableEnergy != null && availableHealth != null );
    }
}

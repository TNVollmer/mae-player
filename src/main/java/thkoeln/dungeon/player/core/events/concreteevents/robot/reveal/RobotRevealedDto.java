package thkoeln.dungeon.player.core.events.concreteevents.robot.reveal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RobotRevealedDto {
    private UUID robotId;
    private UUID planetId;
    private String playerNotion;
    private Integer health;
    private Integer energy;
    private RobotRevealedLevelDto levels;

    public static final Integer DEFAULT_STRENGTH = 10;


    public boolean isValid() {
        return (robotId != null && planetId != null && playerNotion != null);
    }
}

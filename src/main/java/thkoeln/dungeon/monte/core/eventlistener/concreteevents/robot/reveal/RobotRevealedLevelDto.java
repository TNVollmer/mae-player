package thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RobotRevealedLevelDto {
    private Integer healthLevel;
    private Integer damageLevel;
    private Integer miningSpeedLevel;
    private Integer miningLevel;
    private Integer energyLevel;
    private Integer energyRegenLevel;
    private Integer storageLevel;
}

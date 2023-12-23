package thkoeln.dungeon.player.core.events.concreteevents.robot.reveal;

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

    public static final Integer DEFAULT_LEVEL = 2;

    /**
     * Factory method with default values for testing purposes
     */
    public static RobotRevealedLevelDto defaults() {
        RobotRevealedLevelDto levelDto = new RobotRevealedLevelDto();
        levelDto.setHealthLevel(DEFAULT_LEVEL);
        levelDto.setDamageLevel(DEFAULT_LEVEL);
        levelDto.setMiningSpeedLevel(DEFAULT_LEVEL);
        levelDto.setMiningLevel(DEFAULT_LEVEL);
        levelDto.setEnergyLevel(DEFAULT_LEVEL);
        levelDto.setEnergyRegenLevel(DEFAULT_LEVEL);
        levelDto.setStorageLevel(DEFAULT_LEVEL);
        return levelDto;
    }
}

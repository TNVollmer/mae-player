package thkoeln.dungeon.player.robot.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "robot")
@Setter
@Getter
public class RobotProperties {

    private String defaultType;

    private Integer minerMaxCount;
    private Integer scoutMaxCount;
    private Integer warriorMaxCount;

    private Integer minerPercentage;
    private Integer scoutPercentage;
    private Integer warriorPercentage;

    private Integer createMinersAfter;
    private Integer createScoutsAfter;
    private Integer createWarriorsAfter;

    private String minerUpgradeOrder;
    private String scoutUpgradeOrder;
    private String warriorUpgradeOrder;

}

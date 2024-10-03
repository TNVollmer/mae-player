package thkoeln.dungeon.player.robot.domain;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.CapabilityType;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;

import java.util.ArrayList;
import java.util.List;

@Component
public class RobotPropertiesInitializer {

    private final RobotProperties robotProperties;

    @Autowired
    public RobotPropertiesInitializer(RobotProperties robotProperties) {
        this.robotProperties = robotProperties;
    }

    @PostConstruct
    public void init() {
        RobotType.setDefaultType(RobotType.valueOf(robotProperties.getDefaultType()));
        RobotType.setMaxCounts(robotProperties.getMinerMaxCount(), robotProperties.getScoutMaxCount(), robotProperties.getWarriorMaxCount());
        RobotType.setPercentages(robotProperties.getMinerPercentage(), robotProperties.getScoutPercentage(), robotProperties.getWarriorPercentage());
        RobotType.setCreateAfter(robotProperties.getCreateMinersAfter(), robotProperties.getCreateScoutsAfter(), robotProperties.getCreateWarriorsAfter());

        List<CapabilityType> minerUpgradeOrderList = new ArrayList<>();
        List<CapabilityType> scoutUpgradeOrderList = new ArrayList<>();
        List<CapabilityType> warriorUpgradeOrderList = new ArrayList<>();

        for (String upgrade : robotProperties.getMinerUpgradeOrder().split(",")) {
            minerUpgradeOrderList.add(CapabilityType.valueOf(upgrade));
        }

        for (String upgrade : robotProperties.getScoutUpgradeOrder().split(",")) {
            minerUpgradeOrderList.add(CapabilityType.valueOf(upgrade));
        }

        for (String upgrade : robotProperties.getWarriorUpgradeOrder().split(",")) {
            minerUpgradeOrderList.add(CapabilityType.valueOf(upgrade));
        }

        RobotType.setUpgradeOrder(minerUpgradeOrderList, scoutUpgradeOrderList, warriorUpgradeOrderList);
    }

}

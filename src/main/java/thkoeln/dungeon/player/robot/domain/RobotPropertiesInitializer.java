package thkoeln.dungeon.player.robot.domain;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.CapabilityType;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
        log.info("Set default robot type to: {}", RobotType.getDefaultType());

        RobotType.setMaxCounts(robotProperties.getMinerMaxCount(), robotProperties.getScoutMaxCount(), robotProperties.getWarriorMaxCount());
        log.info("Set max count per robot type to: MINER: {}, SCOUT: {}, WARRIOR: {}",
                robotProperties.getMinerMaxCount(), robotProperties.getScoutMaxCount(), robotProperties.getWarriorMaxCount());
        RobotType.setPercentages(robotProperties.getMinerPercentage(), robotProperties.getScoutPercentage(), robotProperties.getWarriorPercentage());
        log.info("Set percentages per robot type to: MINER: {}, SCOUT: {}, WARRIOR: {}",
                robotProperties.getMinerPercentage(), robotProperties.getScoutPercentage(), robotProperties.getWarriorPercentage());
        RobotType.setCreateAfter(robotProperties.getCreateMinersAfter(), robotProperties.getCreateScoutsAfter(), robotProperties.getCreateWarriorsAfter());
        log.info("Set create after per robot type to: MINER: {}, SCOUT: {}, WARRIOR: {}",
                robotProperties.getCreateMinersAfter(), robotProperties.getCreateScoutsAfter(), robotProperties.getCreateWarriorsAfter());

        List<CapabilityType> minerUpgradeOrderList = new ArrayList<>();
        List<CapabilityType> scoutUpgradeOrderList = new ArrayList<>();
        List<CapabilityType> warriorUpgradeOrderList = new ArrayList<>();

        for (String upgrade : robotProperties.getMinerUpgradeOrder().split(",")) {
            minerUpgradeOrderList.add(CapabilityType.valueOf(upgrade));
        }

        for (String upgrade : robotProperties.getScoutUpgradeOrder().split(",")) {
            scoutUpgradeOrderList.add(CapabilityType.valueOf(upgrade));
        }

        for (String upgrade : robotProperties.getWarriorUpgradeOrder().split(",")) {
            warriorUpgradeOrderList.add(CapabilityType.valueOf(upgrade));
        }

        RobotType.setUpgradeOrder(minerUpgradeOrderList, scoutUpgradeOrderList, warriorUpgradeOrderList);
        log.info("Upgrade Order for MINER: {}", minerUpgradeOrderList);
        log.info("Upgrade Order for SCOUT: {}", scoutUpgradeOrderList);
        log.info("Upgrade Order for WARRIOR: {}", warriorUpgradeOrderList);
    }

}

package thkoeln.dungeon.player.robot.domain;

import thkoeln.dungeon.player.core.domainprimitives.purchasing.CapabilityType;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;

import java.util.List;

public class RobotDecisionMaker {

    private final static Integer maxScouts = 1;
    private final static Integer minerPercentage = 70;

    private static Integer totalRobotCount = 0;
    private static Integer scoutCount = 0;
    private static Integer minerCount = 0;

    public static RobotType getNextRobotType() {
        if (scoutCount < maxScouts) return RobotType.Scout;

        Integer percentage = (int) (((float) minerCount / (float) totalRobotCount) * 100);
        if (percentage < minerPercentage) return RobotType.Miner;
        return RobotType.Warrior;
    }

    public static void addRobot(RobotType type) {
        totalRobotCount++;
        if (type == RobotType.Scout)
            scoutCount++;
        else if (type == RobotType.Miner)
            minerCount++;
    }

    public static void clear() {
        totalRobotCount = 0;
        scoutCount = 0;
        minerCount = 0;
    }

    public static List<CapabilityType> getUpgradePriorities(RobotType type){
        return switch (type) {
            case Scout -> List.of(
                    CapabilityType.ENERGY_REGEN,
                    CapabilityType.MAX_ENERGY
            );
            case Miner -> List.of(
                    CapabilityType.MINING,
                    CapabilityType.MINING_SPEED,
                    CapabilityType.STORAGE,
                    CapabilityType.ENERGY_REGEN,
                    CapabilityType.MAX_ENERGY,
                    CapabilityType.HEALTH);
            case Warrior -> List.of(
                    CapabilityType.DAMAGE,
                    CapabilityType.ENERGY_REGEN,
                    CapabilityType.MAX_ENERGY);
        };
    }

}

package thkoeln.dungeon.player.robot.domain;

import thkoeln.dungeon.player.core.domainprimitives.purchasing.CapabilityType;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;
import thkoeln.dungeon.player.robot.domain.strategies.MinerTaskSelection;
import thkoeln.dungeon.player.robot.domain.strategies.ScoutTaskSelection;
import thkoeln.dungeon.player.robot.domain.strategies.TaskSelection;
import thkoeln.dungeon.player.robot.domain.strategies.WarriorTaskSelection;

import java.util.List;

public class RobotDecisionMaker {

    private final static Integer maxScouts = 1;
    private final static Integer minerPercentage = 60;

    public static RobotType getNextRobotType(Iterable<Robot> robots) {
        int totalRobotCount = 1;
        int scoutCount = 0;
        int minerCount = 0;

        for (Robot robot : robots) {
            totalRobotCount += 1;
            if (robot.getRobotType() == RobotType.Scout) scoutCount += 1;
            if (robot.getRobotType() == RobotType.Miner) minerCount += 1;
        }

        if (scoutCount < maxScouts) return RobotType.Scout;

        int percentage = (int) (((float) minerCount / (float) totalRobotCount) * 100);
        if (percentage < minerPercentage) return RobotType.Miner;
        return RobotType.Warrior;
    }

    public static TaskSelection getTaskSelectionByRobotType(RobotType type) {
        return switch (type) {
            case Scout -> new ScoutTaskSelection();
            case Miner -> new MinerTaskSelection();
            case Warrior -> new WarriorTaskSelection();
        };
    }

    public static List<CapabilityType> getUpgradePriorities(RobotType type){
        return switch (type) {
            case Scout -> List.of(
                    CapabilityType.ENERGY_REGEN,
                    CapabilityType.MAX_ENERGY,
                    CapabilityType.HEALTH
            );
            case Miner -> List.of(
                    CapabilityType.MINING_SPEED,
                    CapabilityType.MINING,
                    CapabilityType.STORAGE,
                    CapabilityType.ENERGY_REGEN,
                    CapabilityType.MAX_ENERGY,
                    CapabilityType.HEALTH);
            case Warrior -> List.of(
                    CapabilityType.DAMAGE,
                    CapabilityType.ENERGY_REGEN,
                    CapabilityType.MAX_ENERGY,
                    CapabilityType.HEALTH);
        };
    }

}

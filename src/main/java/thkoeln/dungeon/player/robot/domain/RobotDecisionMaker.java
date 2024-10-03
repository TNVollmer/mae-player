package thkoeln.dungeon.player.robot.domain;

import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;
import thkoeln.dungeon.player.robot.domain.strategies.MinerTaskSelection;
import thkoeln.dungeon.player.robot.domain.strategies.ScoutTaskSelection;
import thkoeln.dungeon.player.robot.domain.strategies.TaskSelection;
import thkoeln.dungeon.player.robot.domain.strategies.WarriorTaskSelection;

public class RobotDecisionMaker {

    public static TaskSelection getTaskSelectionByRobotType(RobotType type) {
        return switch (type) {
            case SCOUT -> new ScoutTaskSelection();
            case MINER -> new MinerTaskSelection();
            case WARRIOR -> new WarriorTaskSelection();
        };
    }
}

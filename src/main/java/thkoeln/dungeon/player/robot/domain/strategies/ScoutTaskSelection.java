package thkoeln.dungeon.player.robot.domain.strategies;

import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotDecisionMaker;

public class ScoutTaskSelection implements TaskSelection {
    @Override
    public void queueNextTask(Robot robot) {
        if (!robot.moveToNextUnexploredPlanet())
            robot.setRobotType(RobotDecisionMaker.getNextRobotType());
    }
}

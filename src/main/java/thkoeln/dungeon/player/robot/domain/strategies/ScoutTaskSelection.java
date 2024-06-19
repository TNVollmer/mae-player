package thkoeln.dungeon.player.robot.domain.strategies;

import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;
import thkoeln.dungeon.player.robot.domain.Robot;

public class ScoutTaskSelection implements TaskSelection {
    @Override
    public void queueNextTask(Robot robot) {
        if (!robot.moveToNextUnexploredPlanet()) {
            robot.setRobotType(RobotType.Warrior);
        }
    }
}

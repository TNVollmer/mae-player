package thkoeln.dungeon.player.robot.domain.strategies;

import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;
import thkoeln.dungeon.player.robot.domain.Robot;

public class ScoutTaskSelection implements TaskSelection {
    @Override
    public void queueNextTask(Robot robot) {
        if (!robot.moveToNextUnexploredPlanet())
            robot.setRobotType(RobotType.Warrior);
        if (robot.canNotMove())
            robot.queueCommand(Command.createRegeneration(robot.getRobotId(), robot.getPlayer().getGameId(), robot.getPlayer().getPlayerId()));
    }

    @Override
    public void onAttackAction(Robot robot) {

    }
}

package thkoeln.dungeon.player.robot.domain.strategies;

import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.robot.domain.Robot;

public class MinerTaskSelection implements TaskSelection {
    @Override
    public void queueNextTask(Robot robot) {
        if (robot.canMine() && !robot.canMineBetterResources()) {
            robot.mine();
        } else {
            if (!robot.moveToNearestPlanetWithBestMineableResources())
                robot.moveToNextUnexploredPlanet();
            if (!robot.canMove())
                robot.queueCommand(Command.createRegeneration(robot.getRobotId(), robot.getPlayer().getGameId(), robot.getPlayer().getPlayerId()));
            if (robot.canMine() && robot.canMineBetterResources() && !robot.getInventory().isEmpty()) {
                robot.queueCommand(Command.createSelling(robot.getRobotId(), robot.getPlayer().getGameId(), robot.getPlayer().getPlayerId(), robot.getInventory().getResources().get(0)));
            }
        }
    }
}

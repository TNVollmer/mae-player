package thkoeln.dungeon.player.robot.domain.strategies;

import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.robot.domain.Robot;

import java.util.List;
import java.util.Random;

public class MinerTaskSelection implements TaskSelection {
    @Override
    public void queueNextTask(Robot robot) {
        if (robot.canMine()) {
            if (robot.canMineBetterResources() && robot.moveToNearestPlanetWithBestMineableResources())
                robot.sellInventory();
            else
                robot.mine();
        } else {
            if (robot.canNotMove())
                robot.queueCommand(Command.createRegeneration(robot.getRobotId(), robot.getPlayer().getGameId(), robot.getPlayer().getPlayerId()));
            else
                robot.moveToNextUnexploredPlanet();
        }
    }

    @Override
    public void onAttackAction(Robot robot) {
        robot.clearQueue();
        List<Planet> planets = robot.getPlanet().getNeighbors();
        if (planets.isEmpty()) return;
        Planet random = planets.get(new Random().nextInt(planets.size()));
        robot.queueCommand(Command.createMove(robot.getRobotId(), random.getPlanetId(), robot.getPlayer().getGameId(), robot.getPlayer().getPlayerId()));
    }
}

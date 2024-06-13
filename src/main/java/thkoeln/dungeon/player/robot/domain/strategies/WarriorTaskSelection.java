package thkoeln.dungeon.player.robot.domain.strategies;

import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.robot.domain.Robot;

import java.util.List;
import java.util.Random;

public class WarriorTaskSelection implements TaskSelection {
    @Override
    public void queueNextTask(Robot robot) {
        if (robot.canNotMove())
            robot.queueFirst(
                    Command.createRegeneration(robot.getRobotId(), robot.getPlayer().getGameId(), robot.getPlayer().getPlayerId())
            );
        else {
            List<Planet> neighbours = robot.getPlanet().getNeighbors();
            Planet random = neighbours.get(new Random().nextInt(neighbours.size()));
            robot.queueCommand(
                    Command.createMove(robot.getRobotId(), random.getPlanetId(), robot.getPlayer().getGameId(), robot.getPlayer().getPlayerId())
            );
        }
    }
}

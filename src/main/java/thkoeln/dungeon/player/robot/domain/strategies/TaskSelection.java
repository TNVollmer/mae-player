package thkoeln.dungeon.player.robot.domain.strategies;

import thkoeln.dungeon.player.robot.domain.Robot;

public interface TaskSelection {

    void queueNextTask(Robot robot);

}

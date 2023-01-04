package thkoeln.dungeon.monte.robot.domain;

import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.strategy.AccountInformation;

public interface RobotBehavior {

    public Command regenerateIfLowAndNotAttacked();

    public Command fleeIfAttacked();

    public Command mineIfNotMinedLastRound();

    public Command mine();

    public Command move();

    public Command upgrade( AccountInformation accountInformation );

    public Command attack();
}

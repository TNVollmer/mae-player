package thkoeln.dungeon.monte.robot.domain;

import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.strategy.AccountInformation;
import thkoeln.dungeon.monte.core.strategy.Actionable;

public interface ActionableRobot extends Actionable {

    public Command regenerateIfLowAndNotAttacked();

    public Command regenerate();

    public Command fleeIfAttacked();

    public Command mineIfNotMinedLastRound();

    public Command mine();

    public Command move();

    public Command upgrade( AccountInformation accountInformation );

    public Command attack();
}

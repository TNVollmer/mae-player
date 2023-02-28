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

    public Command sellMineableResources();

    public Command moveRandomlyToUnexploredPlanet();

    public Command moveRandomly();

    public Command moveIfNotOnFittingResource();

    public Command moveIfOptimalResourceNearby();

    public Command moveIfOpponentNearby();

    public Command upgrade( AccountInformation accountInformation );

    public Command attack();
}

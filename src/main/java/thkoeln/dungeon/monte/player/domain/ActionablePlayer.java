package thkoeln.dungeon.monte.player.domain;

import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.strategy.AccountInformation;
import thkoeln.dungeon.monte.core.strategy.Actionable;

public interface ActionablePlayer extends Actionable {
    public Command buyRobots( AccountInformation accountInformation );
}

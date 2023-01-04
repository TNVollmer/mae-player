package thkoeln.dungeon.monte.player.domain;

import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.strategy.AccountInformation;

public interface PlayerBehavior {
    public Command buyRobots( AccountInformation accountInformation );
}

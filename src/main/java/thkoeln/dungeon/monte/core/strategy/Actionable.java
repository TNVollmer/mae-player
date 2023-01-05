package thkoeln.dungeon.monte.core.strategy;

import thkoeln.dungeon.monte.core.domainprimitives.command.Command;

/**
 * Marker interface for those entities that are able to issue commands
 */
public interface Actionable {
    public Command decideNextCommand( AccountInformation accountInformation );
}

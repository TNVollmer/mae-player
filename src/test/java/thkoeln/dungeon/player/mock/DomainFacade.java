package thkoeln.dungeon.player.mock;

import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.player.game.domain.Game;

public interface DomainFacade {

    /**
     * @param game
     * @return the round status of the currently active round in the given game.
     */
    public RoundStatusType getRoundStatusForCurrentRound(Game game);

}

package thkoeln.dungeon.player.mock;

import thkoeln.dungeon.player.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.player.game.domain.Game;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DomainFacade {

    /**
     * @param game
     * @return the round status of the currently active round in the given game.
     */
    public RoundStatusType getRoundStatusForCurrentRound(Game game);

    /**
     * @param id
     * @param <T>
     * @return the planet object by its planet id
     */
    public <T> T getPlanetByPlanetId(UUID id);

    /**
     * @param planet
     * @param <T>
     * @return a map of all neighbours of the given planet mapped by its compass direction relative to the given planet
     *
     * to visualise (the * in the center representing the planet):
     *
     *                                (compass direction = NORTH)
     *                                            *
     *                                            |
     *           (compass direction = WEST)   * - * - *   (compass direction = EAST)
     *                                            |
     *                                            *
     *                                (compass direction = SOUTH)
     */
    public <T> Map<CompassDirection, T> getNeighboursOfPlanet(T planet);

    /**
     * @param planet
     * @param <T>
     * @return the movement difficulty of the given planet
     */
    public <T> int getMovementDifficultyForPlanet(T planet);

    /**
     * @param planet
     * @param <T>
     * @return the resource type of the resource of this planet or 'null' if the planet does not contain a resource
     */
    public <T> MineableResourceType getResourceTypeOfPlanet(T planet);

    /**
     * @param planet
     * @param <T>
     * @return the current amount of the resource of this planet or 'null' if the planet does not contain a resource
     */
    public <T> Integer getCurrentResourceAmountOfPlanet(T planet);

    /**
     * @param planet
     * @param <T>
     * @return the maximum amount of the resource of this planet or 'null' if the planet does not contain a resource
     */
    public <T> Integer getMaxResourceAmountOfPlanet(T planet);

}

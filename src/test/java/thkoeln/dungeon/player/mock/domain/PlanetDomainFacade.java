package thkoeln.dungeon.player.mock.domain;

import thkoeln.dungeon.player.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;

import java.util.Map;
import java.util.UUID;

public interface PlanetDomainFacade {

    /**
     * @param <T>
     * @return a newly created planet
     */
    public <T> T createNewPlanet();

    /**
     * Persist the given planet
     * @param planet
     * @param <T>
     */
    public <T> void savePlanet(T planet);



    /**
     * @param id
     * @param <T>
     * @return the planet object by its planet id or 'null' if no such planet exists
     */
    public <T> T getPlanetByPlanetId(UUID id);

    /**
     * @param planet
     * @param <T>
     * @return the planet id of the given planet
     */
    public <T> UUID getPlanetIdOfPlanet(T planet);

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

    /**
     * @param planet
     * @param <T>
     * @return the x coordinate of the given planet
     */
    public <T> int getXCoordOfPlanet(T planet);

    /**
     * @param planet
     * @param <T>
     * @return the y coordinate of the given planet
     */
    public <T> int getYCoordOfPlanet(T planet);

    /**
     * @param planet
     * @param <T>
     * @return the any random existing neighbour planet of the given planet
     */
    public <T> T getRandomNeighbourOfPlanet(T planet);



    /**
     * Set the planet id for the given planet
     * @param planet
     * @param planetId
     * @param <T>
     */
    public <T> void setPlanetIdForPlanet(T planet, UUID planetId);

    /**
     * Set the mineable resource type for the given planet
     * @param planet
     * @param type
     * @param <T>
     */
    public <T> void setResourceTypeForPlanet(T planet, MineableResourceType type);

    /**
     * Set the currently available resource amount for the given planet
     * @param planet
     * @param currentAmount
     * @param <T>
     */
    public <T> void setCurrentResourceAmountForPlanet(T planet, int currentAmount);

    /**
     * Set the max available resource amount for the given planet
     * @param planet
     * @param maxAmount
     * @param <T>
     */
    public <T> void setMaxResourceAmountForPlanet(T planet, int maxAmount);

    /**
     * Set the coordinates for the given planet
     *
     *
     *     0     1     2     3     4     5
     *
     *     1     *     *     *     *     *
     *
     *     2     *     *     *     *     *
     *
     *     3     *     +     *     *     *
     *
     *     4     *     *     *     *     *
     *
     *     5     *     *     *     *     *
     *
     *
     * The map is structured as visualized above. The coordinate origin is in the top left corner.
     * You need to assign the coordinates of the planet accordingly.
     *
     * As an example, the planet symbolized as +
     * would have the coordinates (2, 3), while its northern neighbour would have the coordinates (2, 2).
     *
     *
     * @param planet
     * @param xCoord
     * @param yCoord
     * @param <T>
     */
    public <T> void setCoordinatesForPlanet(T planet, int xCoord, int yCoord);

    /**
     * Set the movement difficulty for the given planet
     * @param planet
     * @param movementDifficulty
     * @param <T>
     */
    public <T> void setMovementDifficultyForPlanet(T planet, int movementDifficulty);

}

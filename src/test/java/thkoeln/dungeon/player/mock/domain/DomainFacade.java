package thkoeln.dungeon.player.mock.domain;

import thkoeln.dungeon.player.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.TradeableType;
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
     * @return the planet object by its planet id or 'null' if no such planet exists
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

    /**
     * @param <T>
     * @return a newly created planet
     */
    public <T> T createNewPlanet();

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
     * Persist the given planet via your planet repository
     * @param planet
     * @param <T>
     */
    public <T> void savePlanet(T planet);

    /**
     * @param robotId
     * @param <T>
     * @return the robot object by its robot id or 'null' if no such robot exists
     */
    public <T> T getRobotByRobotId(UUID robotId);

    /**
     * @param robot
     * @param <T>
     * @return the remaining health of the given robot
     */
    public <T> Integer getHealthOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the remaining energy of the given robot
     */
    public <T> Integer getEnergyOfRobot(T robot);

    /**
     * @param <T>
     * @return a newly created robot
     */
    public <T> T createNewRobot();

    /**
     * Set the robot id for the given robot
     * @param robot
     * @param robotId
     * @param <T>
     */
    public <T> void setRobotIdForRobot(T robot, UUID robotId);

    /**
     * Set the remaining health for the given robot
     * @param robot
     * @param health
     * @param <T>
     */
    public <T> void setHealthForRobot(T robot, int health);

    /**
     * Set the remaining energy for the given robot
     * @param robot
     * @param energy
     * @param <T>
     */
    public <T> void setEnergyForRobot(T robot, int energy);

    /**
     * Persist the given robot
     * @param robot
     * @param <T>
     */
    public <T> void saveRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the health level of the given robot
     */
    public <T> Integer getHealthLevelOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the damage level of the given robot
     */
    public <T> Integer getDamageLevelOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the mining speed level of the given robot
     */
    public <T> Integer getMiningSpeedLevelOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the mining level of the given robot
     */
    public <T> Integer getMiningLevelOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the energy level of the given robot
     */
    public <T> Integer getEnergyLevelOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the energy regen level of the given robot
     */
    public <T> Integer getEnergyRegenLevelOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the storage level of the given robot
     */
    public <T> Integer getStorageLevelOfRobot(T robot);

    /**
     * Set the health level for the given robot
     * @param robot
     * @param healthLevel
     * @param <T>
     */
    public <T> void setHealthLevelForRobot(T robot, int healthLevel);

    /**
     * Set the energy level for the given robot
     * @param robot
     * @param energyLevel
     * @param <T>
     */
    public <T> void setEnergyLevelForRobot(T robot, int energyLevel);

    /**
     * Set the energy regen level for the given robot
     * @param robot
     * @param energyRegenLevel
     * @param <T>
     */
    public <T> void setEnergyRegenLevelForRobot(T robot, int energyRegenLevel);

    /**
     * Set the damage level for the given robot
     * @param robot
     * @param damageLevel
     * @param <T>
     */
    public <T> void setDamageLevelForRobot(T robot, int damageLevel);

    /**
     * Set the mining level for the given robot
     * @param robot
     * @param miningLevel
     * @param <T>
     */
    public <T> void setMiningLevelForRobot(T robot, int miningLevel);

    /**
     * Set the mining speed level for the given robot
     * @param robot
     * @param miningSpeedLevel
     * @param <T>
     */
    public <T> void setMiningSpeedLevelForRobot(T robot, int miningSpeedLevel);

    /**
     * @param robot
     * @param <T>
     * @return the living status of the given robot, signifying whether the robot is still alive or already destroyed
     */
    public <T> boolean getAliveStatusOfRobot(T robot);

    /**
     * Set the coal amount for the given robot
     * @param robot
     * @param coalAmount
     * @param <T>
     */
    public <T> void setCoalAmountForRobot(T robot, int coalAmount);

    /**
     * @param robot
     * @param <T>
     * @return the coal amount of the given robot
     */
    public <T> Integer getCoalAmountOfRobot(T robot);

    /**
     * Set the planet the robot is located at for the given robot
     * @param robot
     * @param planet
     * @param <T>
     */
    public <T, E> void setPlanetLocationForRobot(T robot, E planet);

    /**
     * @param robot
     * @param <T>
     * @return the planet the robot is located at of the given robot
     */
    public <T, E> E getPlanetLocationOfRobot(T robot);

    /**
     * @param planet
     * @param <T>
     * @return the planet id of the given planet
     */
    public <T> UUID getPlanetIdOfPlanet(T planet);

    /**
     * @param robot
     * @param <T>
     * @return the mining speed of the given robot
     */
    public <T> Integer getMiningSpeedOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the health cap of the given robot
     */
    public <T> Integer getMaxHealthOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the energy cap of the given robot
     */
    public <T> Integer getMaxEnergyOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the energy regeneration speed of the given robot
     */
    public <T> Integer getEnergyRegenOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the attack damage of the given robot
     */
    public <T> Integer getAttackDamageOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the full status of the resource inventory of the given robot, whether it is completely full or not
     */
    public <T> boolean getInventoryFullStateOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the unit amount of resources currently being held inside the robots inventory of the given robot
     */
    public <T> Integer getInventoryUsedStorageOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the resource carrying cap of the inventory of the given robot
     */
    public <T> Integer getInventoryMaxStorageOfRobot(T robot);

    /**
     * Set the money balance for the given player
     * @param player
     * @param balance
     * @param <T>
     */
    public <T> void setBalanceForPlayer(T player, int balance);

    /**
     * @param player
     * @param <T>
     * @return the money balance of the given player
     */
    public <T> Integer getBalanceOfPlayer(T player);

    /**
     * @param <T>
     * @return a list of all robots
     */
    public <T> List<T> getAllRobots();

    /**
     * @param <T>
     * @return a list of all tradable items (usually obtained at the beginning of a game through the tradable prices event)
     */
    public <T> List<T> getAllTradableItems();

    /**
     * @param name
     * @param <T>
     * @return the tradable item having the given name, or null if you dont find any
     */
    public <T> T getTradableItemByName(String name);

    /**
     * @param tradableItem
     * @param <T>
     * @return the money price of the given tradable
     */
    public <T> Integer getPriceOfTradableItem(T tradableItem);

    /**
     * @param tradableItem
     * @param <T>
     * @return the tradable type of the given tradable
     */
    public <T> TradeableType getTradableTypeOfTradableItem(T tradableItem);

    /**
     * Bring the application into a clean state. Remove all entities in the database. Reset all state variables to their
     * original state (if there are any). Basically, perform a cleanup.
     */
    public void resetEverything();

    /**
     * The same as the above, just without cleaning up the player entity / entities inside the player table
     * of the database.
     */
    public void resetEverythingExceptPlayer();

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

    /**
     * @param planet
     * @param <T>
     * @return the any random existing neighbour planet of the given planet
     */
    public <T> T getRandomNeighbourOfPlanet(T planet);

    /**
     * @param robot
     * @param <T>
     * @return the robot id of the given robot
     */
    public <T> UUID getRobotIdOfRobot(T robot);

}

package thkoeln.dungeon.player.mock;

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

    public <T> boolean getAliveStatusOfRobot(T robot);

    public <T> void setCoalAmountForRobot(T robot, int coalAmount);

    public <T> Integer getCoalAmountOfRobot(T robot);

    public <T, E> void setPlanetLocationForRobot(T robot, E planet);

    public <T, E> E getPlanetLocationOfRobot(T robot);

    public <T> UUID getPlanetIdOfPlanet(T planet);

    public <T> Integer getMiningSpeedOfRobot(T robot);

    public <T> Integer getMaxHealthOfRobot(T robot);

    public <T> Integer getMaxEnergyOfRobot(T robot);

    public <T> Integer getEnergyRegenOfRobot(T robot);

    public <T> Integer getAttackDamageOfRobot(T robot);

    public <T> boolean getInventoryFullStateOfRobot(T robot);

    public <T> Integer getInventoryUsedStorageOfRobot(T robot);

    public <T> Integer getInventoryMaxStorageOfRobot(T robot);

    public <T> void setBalanceForPlayer(T player, int balance);

    public <T> Integer getBalanceOfPlayer(T player);

    public <T> List<T> getAllRobots();

    public <T> List<T> getAllTradableItems();

    public <T> T getTradableItemByName(String name);

    public <T> Integer getPriceOfTradableItem(T tradableItem);

    public <T> TradeableType getTradableTypeOfTradableItem(T tradableItem);

    public void resetEverything();

    public void resetEverythingExceptPlayer();

}

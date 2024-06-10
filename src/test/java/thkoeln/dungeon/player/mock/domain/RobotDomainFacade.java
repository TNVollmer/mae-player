package thkoeln.dungeon.player.mock.domain;

import java.util.List;
import java.util.UUID;

public interface RobotDomainFacade {

    /**
     * @param <T>
     * @return a newly created robot
     */
    public <T> T createNewRobot();

    /**
     * Persist the given robot
     * @param robot
     * @param <T>
     */
    public <T> void saveRobot(T robot);



    /**
     * @param <T>
     * @return a list of all robots
     */
    public <T> List<T> getAllRobots();

    /**
     * @param robotId
     * @param <T>
     * @return the robot object by its robot id or 'null' if no such robot exists
     */
    public <T> T getRobotByRobotId(UUID robotId);

    /**
     * @param robot
     * @param <T>
     * @return the robot id of the given robot
     */
    public <T> UUID getRobotIdOfRobot(T robot);

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
     * @param robot
     * @param <T>
     * @return the living status of the given robot, signifying whether the robot is still alive or already destroyed
     */
    public <T> boolean getAliveStatusOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the coal amount of the given robot
     */
    public <T> Integer getCoalAmountOfRobot(T robot);

    /**
     * @param robot
     * @param <T>
     * @return the planet the robot is located at of the given robot
     */
    public <T, E> E getPlanetLocationOfRobot(T robot);

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
     * Set the coal amount for the given robot
     * @param robot
     * @param coalAmount
     * @param <T>
     */
    public <T> void setCoalAmountForRobot(T robot, int coalAmount);

    /**
     * Set the planet the robot is located at for the given robot
     * @param robot
     * @param planet
     * @param <T>
     */
    public <T, E> void setPlanetLocationForRobot(T robot, E planet);

}

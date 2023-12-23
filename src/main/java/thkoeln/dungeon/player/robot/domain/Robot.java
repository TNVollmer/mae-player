package thkoeln.dungeon.player.robot.domain;


import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotInventory;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotDto;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Robot {
    @Transient
    private Logger logger = LoggerFactory.getLogger(Robot.class);

    @Id
    private final UUID id = UUID.randomUUID();

    private UUID robotId;
    private String name = "Robot";

    private boolean isAlive = true;
    private int maxHealth;
    private int health;

    private int maxEnergy;
    private int energy;
    private int energyRegen;

    private int healthLevel;
    private int energyLevel;
    private int energyRegenLevel;

    private int attackDamage;
    private int miningSpeed;
    private int miningSpeedLevel;
    private int miningLevel;

    private String strategyStatus = "idle";

    @Embedded
    private RobotInventory robotInventory = RobotInventory.emptyInventory();

    @Embedded
    private RobotPlanet robotPlanet = RobotPlanet.nullPlanet();

    public static Robot of(RobotDto robotDto, String name) {
        Robot robot = new Robot();
        robot.setRobotId(robotDto.getId());
        robot.setName(name);
        robot.setAlive(robotDto.getAlive());
        robot.setMaxHealth(robotDto.getMaxHealth());
        robot.setHealth(robotDto.getMaxHealth());
        robot.setMaxEnergy(robotDto.getMaxEnergy());
        robot.setEnergy(robotDto.getEnergy());
        robot.setEnergyRegen(robotDto.getEnergyRegen());
        robot.setHealthLevel(robotDto.getHealthLevel());
        robot.setEnergyLevel(robotDto.getEnergyLevel());
        robot.setEnergyRegenLevel(robotDto.getEnergyRegenLevel());
        robot.setAttackDamage(robotDto.getAttackDamage());
        robot.setMiningSpeed(robotDto.getMiningSpeed());
        robot.setMiningSpeedLevel(robotDto.getMiningSpeedLevel());
        robot.setMiningLevel(robotDto.getMiningLevel());
        robot.setRobotPlanet(RobotPlanet.planetWithoutNeighbours(robotDto.getPlanet().getPlanetId()));
        robot.setRobotInventory(RobotInventory.fromStorageLevelAndMaxStorage(robotDto.getInventory().getStorageLevel(), robotDto.getInventory().getMaxStorage()));
        return robot;
    }


    @Override
    public String toString() {
        String result = ("Robot: " + name + " | RobotId: " + robotId + " | Strategy: "+ strategyStatus + " | Health: " + health + "/" + maxHealth + " | Energy: " + energy + "/" + maxEnergy + " | Energy Regen: " + energyRegen + " | Attack Damage: " + attackDamage + " | Mining Speed: " + miningSpeed);
        result += (" Robot Inventory: " + robotInventory.toString());
        return result;
    }
}

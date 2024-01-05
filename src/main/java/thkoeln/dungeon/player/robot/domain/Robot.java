package thkoeln.dungeon.player.robot.domain;


import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotInventory;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotRevealedDto;
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
    private Boolean playerOwned = false;

    private boolean isAlive = true;
    private int maxHealth;
    private int health;

    private int maxEnergy;
    private int energy;
    private int energyRegen;


    private int healthLevel = 0;
    private int energyLevel = 0;
    private int energyRegenLevel = 0;

    private int attackDamage = 0;
    private int damageLevel = 0;

    private int miningSpeed = 2;
    private int miningSpeedLevel = 0;
    private int miningLevel = 0;

    private String strategyStatus = "idle";
    private String pendingUpgrade = null;

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
        robot.setHealth(robotDto.getHealth());
        robot.setMaxEnergy(robotDto.getMaxEnergy());
        robot.setEnergy(robotDto.getEnergy());
        robot.setEnergyRegen(robotDto.getEnergyRegen());
        robot.setHealthLevel(robotDto.getHealthLevel());
        robot.setEnergyLevel(robotDto.getEnergyLevel());
        robot.setEnergyRegenLevel(robotDto.getEnergyRegenLevel());
        robot.setAttackDamage(robotDto.getAttackDamage());
        robot.setDamageLevel(robotDto.getDamageLevel());
        robot.setMiningSpeed(robotDto.getMiningSpeed());
        robot.setMiningSpeedLevel(robotDto.getMiningSpeedLevel());
        robot.setMiningLevel(robotDto.getMiningLevel());
        robot.setRobotPlanet(RobotPlanet.planetWithoutNeighbours(robotDto.getPlanet().getPlanetId()));
        robot.setRobotInventory(RobotInventory.fromStorageLevelAndMaxStorage(robotDto.getInventory().getStorageLevel(), robotDto.getInventory().getMaxStorage()));
        return robot;
    }

    public static Robot ofEnemy(RobotRevealedDto robotRevealedDto, String name) {
        Robot robot = new Robot();
        robot.setRobotId(robotRevealedDto.getRobotId());
        robot.setName(name);
        robot.setAlive(true);
        robot.setHealth(robotRevealedDto.getHealth());
        robot.setEnergy(robotRevealedDto.getEnergy());
        robot.setHealthLevel(robotRevealedDto.getLevels().getHealthLevel());
        robot.setEnergyLevel(robotRevealedDto.getLevels().getEnergyLevel());
        robot.setEnergyRegenLevel(robotRevealedDto.getLevels().getEnergyRegenLevel());
        robot.setDamageLevel(robotRevealedDto.getLevels().getDamageLevel());
        robot.setMiningSpeedLevel(robotRevealedDto.getLevels().getMiningSpeedLevel());
        robot.setMiningLevel(robotRevealedDto.getLevels().getMiningLevel());

        robot.setRobotPlanet(RobotPlanet.planetWithoutNeighbours(robotRevealedDto.getPlanetId()));
        return robot;
    }


    @Override
    public String toString() {
        String result = ("Robot: " + name + " | RobotId: " + robotId + " | Strategy: " + strategyStatus + " | Health: " + health + "/" + maxHealth + " | Energy: " + energy + "/" + maxEnergy + " | Energy Regen: " + energyRegen + " | Attack Damage: " + attackDamage + " | Mining Speed: " + miningSpeed);
        result += (" | Health Level: " + healthLevel + " | Energy Level: " + energyLevel + " | Energy Regen Level: " + energyRegenLevel + " | Damage Level: " + damageLevel + " | Mining Speed Level: " + miningSpeedLevel + " | Mining Level: " + miningLevel);
        result += (" Robot Inventory: " + robotInventory.toString());
        return result;
    }
}

package thkoeln.dungeon.player.robot.domain;


import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotInventory;

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

    private String strategyStatus = "idle";

    @Embedded
    private RobotInventory robotInventory = RobotInventory.emptyInventory();

    @Embedded
    private RobotPlanet robotPlanet = RobotPlanet.nullPlanet();

    public Robot(UUID robotId, String name, UUID planetId) {
        if (robotId == null || planetId == null) {
            logger.error("Robot or planet id is null");
            throw new IllegalArgumentException("Robot or planet id is null");
        }
        this.name = name;
        this.robotId = robotId;
        this.robotPlanet = RobotPlanet.planetWithoutNeighbours(planetId);
    }

    public static Robot of(UUID robotId, String name, UUID planetId) {
        return new Robot(robotId, name, planetId);
    }


    @Override
    public String toString() {
        String result = ("Robot: " + name + " | RobotId: " + robotId + " | Strategy: "+ strategyStatus + " | Health: " + health + "/" + maxHealth + " | Energy: " + energy + "/" + maxEnergy + " | Energy Regen: " + energyRegen + " | Attack Damage: " + attackDamage + " | Mining Speed: " + miningSpeed);
        result += ("Robot Inventory: " + robotInventory.toString());
        return result;
    }
}

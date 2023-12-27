package thkoeln.dungeon.player.robot.domain;


import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Capability;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.CapabilityType;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotInventory;
import thkoeln.dungeon.player.core.domainprimitives.status.Energy;
import thkoeln.dungeon.player.core.domainprimitives.status.Health;
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
    @Embedded
    private Health maxHealth;
    @Embedded
    private Health health;

    @Embedded
    @AttributeOverride(name = "energy_amount", column = @Column(name = "max_energy_amount"))
    private Energy maxEnergy;

    @Embedded
    @AttributeOverride(name = "energy_amount", column = @Column(name = "energy_amount"))
    private Energy energy;

    private int energyRegen;

    @Embedded
    @AttributeOverride(name = "type", column = @Column(name = "health_type"))
    @AttributeOverride(name = "level", column = @Column(name = "health_level"))
    private Capability healthLevel = Capability.baseForType(CapabilityType.HEALTH);
    @Embedded
    @AttributeOverride(name = "type", column = @Column(name = "energy_type"))
    @AttributeOverride(name = "level", column = @Column(name = "energy_level"))
    private Capability energyLevel = Capability.baseForType(CapabilityType.MAX_ENERGY);
    @Embedded
    @AttributeOverride(name = "type", column = @Column(name = "energy_regen_type"))
    @AttributeOverride(name = "level", column = @Column(name = "energy_regen_level"))
    private Capability energyRegenLevel = Capability.baseForType(CapabilityType.ENERGY_REGEN);

    private int attackDamage;
    @Embedded
    @AttributeOverride(name = "type", column = @Column(name = "damage_type"))
    @AttributeOverride(name = "level", column = @Column(name = "damage_level"))
    private Capability damageLevel = Capability.baseForType(CapabilityType.DAMAGE);

    private int miningSpeed;
    @Embedded
    @AttributeOverride(name = "type", column = @Column(name = "mining_speed_type"))
    @AttributeOverride(name = "level", column = @Column(name = "mining_speed_level"))
    private Capability miningSpeedLevel = Capability.baseForType(CapabilityType.MINING_SPEED);
    @Embedded
    @AttributeOverride(name = "type", column = @Column(name = "mining_type"))
    @AttributeOverride(name = "level", column = @Column(name = "mining_level"))
    private Capability miningLevel = Capability.baseForType(CapabilityType.MINING);

    private String strategyStatus = "idle";
    @Embedded
    @AttributeOverride(name = "type", column = @Column(name = "pending_upgrade_type"))
    @AttributeOverride(name = "level", column = @Column(name = "pending_upgrade_level"))
    private Capability pendingUpgrade = null;

    @Embedded
    private RobotInventory robotInventory = RobotInventory.emptyInventory();

    @Embedded
    private RobotPlanet robotPlanet = RobotPlanet.nullPlanet();

    public static Robot of(RobotDto robotDto, String name) {
        Robot robot = new Robot();
        robot.setRobotId(robotDto.getId());
        robot.setName(name);
        robot.setAlive(robotDto.getAlive());
        robot.setMaxHealth(Health.from(robotDto.getMaxHealth()));
        robot.setHealth(Health.from(robotDto.getHealth()));
        robot.setMaxEnergy(Energy.from(robotDto.getMaxEnergy()));
        robot.setEnergy(Energy.from(robotDto.getEnergy()));
        robot.setEnergyRegen(robotDto.getEnergyRegen());
        robot.setHealthLevel(Capability.forTypeAndLevel(CapabilityType.HEALTH, robotDto.getHealthLevel()));
        robot.setEnergyLevel(Capability.forTypeAndLevel(CapabilityType.MAX_ENERGY, robotDto.getEnergyLevel()));
        robot.setEnergyRegenLevel(Capability.forTypeAndLevel(CapabilityType.ENERGY_REGEN, robotDto.getEnergyRegenLevel()));
        robot.setAttackDamage(robotDto.getAttackDamage());
        robot.setDamageLevel(Capability.forTypeAndLevel(CapabilityType.DAMAGE, robotDto.getDamageLevel()));
        robot.setMiningSpeed(robotDto.getMiningSpeed());
        robot.setMiningSpeedLevel(Capability.forTypeAndLevel(CapabilityType.MINING_SPEED, robotDto.getMiningSpeedLevel()));
        robot.setMiningLevel(Capability.forTypeAndLevel(CapabilityType.MINING, robotDto.getMiningLevel()));
        robot.setRobotPlanet(RobotPlanet.planetWithoutNeighbours(robotDto.getPlanet().getPlanetId()));
        robot.setRobotInventory(RobotInventory.fromStorageLevelAndMaxStorage(robotDto.getInventory().getStorageLevel(), robotDto.getInventory().getMaxStorage()));
        return robot;
    }

    public static Robot ofEnemy(RobotRevealedDto robotRevealedDto, String name) {
        Robot robot = new Robot();
        robot.setRobotId(robotRevealedDto.getRobotId());
        robot.setName(name);
        robot.setAlive(true);
        robot.setHealth(Health.from(robotRevealedDto.getHealth()));
        robot.setEnergy(Energy.from(robotRevealedDto.getEnergy()));
        robot.setHealthLevel(Capability.forTypeAndLevel(CapabilityType.HEALTH, robotRevealedDto.getLevels().getHealthLevel()));
        robot.setEnergyLevel(Capability.forTypeAndLevel(CapabilityType.MAX_ENERGY, robotRevealedDto.getLevels().getEnergyLevel()));
        robot.setEnergyRegenLevel(Capability.forTypeAndLevel(CapabilityType.ENERGY_REGEN, robotRevealedDto.getLevels().getEnergyRegenLevel()));
        robot.setDamageLevel(Capability.forTypeAndLevel(CapabilityType.DAMAGE, robotRevealedDto.getLevels().getDamageLevel()));
        robot.setMiningSpeedLevel(Capability.forTypeAndLevel(CapabilityType.MINING_SPEED, robotRevealedDto.getLevels().getMiningSpeedLevel()));
        robot.setMiningLevel(Capability.forTypeAndLevel(CapabilityType.MINING, robotRevealedDto.getLevels().getMiningLevel()));

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

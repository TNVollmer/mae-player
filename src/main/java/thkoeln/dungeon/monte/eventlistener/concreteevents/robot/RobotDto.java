package thkoeln.dungeon.monte.eventlistener.concreteevents.robot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

import static java.lang.Boolean.TRUE;
import static thkoeln.dungeon.monte.domainprimitives.Capability.MIN_LEVEL;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RobotDto {
    private UUID player;
    private RobotPlanetDto planet;
    private UUID id;
    private Boolean alive = TRUE;
    private RobotInventoryDto inventory;
    private Integer health;
    private Integer energy;
    private Integer healthLevel = MIN_LEVEL;
    private Integer damageLevel = MIN_LEVEL;
    private Integer miningSpeedLevel = MIN_LEVEL;
    private Integer miningLevel = MIN_LEVEL;
    private Integer energyLevel = MIN_LEVEL;
    private Integer energyRegenLevel = MIN_LEVEL;
    private Integer miningSpeed;
    private Integer maxHealth;
    private Integer maxEnergy;
    private Integer energyRegen;
    private Integer attackDamage;
}
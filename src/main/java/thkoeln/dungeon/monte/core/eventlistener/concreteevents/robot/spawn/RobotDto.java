package thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.spawn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

import static java.lang.Boolean.TRUE;
import static thkoeln.dungeon.monte.core.domainprimitives.purchasing.Capability.MIN_LEVEL;

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

    public boolean isValid() {
        if ( getId() == null ) return false;
        if ( getHealth() == null ) return false;
        if ( getHealth() <= 0 ) return false;
        if ( getMaxHealth() == null ) return false;
        if ( getMaxHealth() <= 0 ) return false;
        if ( getEnergy() == null ) return false;
        if ( getEnergy() <= 0 ) return false;
        if ( getMiningSpeed() == null ) return false;
        if ( getMiningSpeed() <= 0 ) return false;
        if ( getMaxEnergy() == null ) return false;
        if ( getMaxEnergy() <= 0 ) return false;
        if ( getEnergyRegen() == null ) return false;
        if ( getEnergyRegen() <= 0 ) return false;
        if ( getAttackDamage() == null ) return false;
        if ( getAttackDamage() <= 0 ) return false;
        if ( inventory == null ) return false;
        return inventory.isValid();
    }    
}
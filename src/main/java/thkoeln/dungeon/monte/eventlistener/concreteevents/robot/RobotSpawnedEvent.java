package thkoeln.dungeon.monte.eventlistener.concreteevents.robot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.eventlistener.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RobotSpawnedEvent extends AbstractEvent {
    private UUID playerId;
    private RobotDto robot;

    @Override
    public boolean isValid() {
        if ( eventHeader == null ) return false;
        if ( robot == null ) return false;
        if ( robot.getInventory() == null ) return false;
        if ( robot.getInventory().getMaxStorage() == null ) return false;
        if ( robot.getInventory().getMaxStorage() <= 0 ) return false;
        if ( robot.getId() == null ) return false;
        if ( robot.getHealth() == null ) return false;
        if ( robot.getHealth() <= 0 ) return false;
        if ( robot.getMaxHealth() == null ) return false;
        if ( robot.getMaxHealth() <= 0 ) return false;
        if ( robot.getEnergy() == null ) return false;
        if ( robot.getEnergy() <= 0 ) return false;
        if ( robot.getMiningSpeed() == null ) return false;
        if ( robot.getMiningSpeed() <= 0 ) return false;
        if ( robot.getMaxEnergy() == null ) return false;
        if ( robot.getMaxEnergy() <= 0 ) return false;
        if ( robot.getEnergyRegen() == null ) return false;
        if ( robot.getEnergyRegen() <= 0 ) return false;
        if ( robot.getAttackDamage() == null ) return false;
        if ( robot.getAttackDamage() <= 0 ) return false;
        return true;
    }
}

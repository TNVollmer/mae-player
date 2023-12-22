package thkoeln.dungeon.player.core.domainprimitives.robot;

import jakarta.persistence.Embeddable;
import lombok.*;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotInventoryDto;

import java.util.Objects;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
@Getter
@EqualsAndHashCode
@Embeddable
public class RobotInventory {
    private Integer storageLevel = 0;
    private Integer usedStorage = 0;
    private RobotInventoryResources resources;
    private Boolean full = Boolean.FALSE;
    private Integer maxStorage;

    public static RobotInventory fromStorageLevelAndMaxStorage( Integer storageLevel, Integer maxStorage ) {
        if ( storageLevel == null ) throw new DomainPrimitiveException( "StorageLevel cannot be null!" );
        if ( storageLevel < 0 ) throw new DomainPrimitiveException( "StorageLevel must be >= 0!" );
        if ( maxStorage == null ) throw new DomainPrimitiveException( "MaxStorage cannot be null!" );
        if ( maxStorage <= 0 ) throw new DomainPrimitiveException( "MaxStorage must be > 0!" );
        return new RobotInventory( storageLevel, 0, RobotInventoryResources.empty(), Boolean.FALSE, maxStorage );
    }

    public RobotInventory add( RobotInventory additionalInventory ) {
        if ( additionalInventory == null ) throw new DomainPrimitiveException( "additionalInventory cannot be null!" );
        if ( additionalInventory.isEmpty() ) return this;
        if ( this.isEmpty() ) return additionalInventory;
        if (!Objects.equals(this.storageLevel, additionalInventory.storageLevel)) throw new DomainPrimitiveException( "Cannot add inventories of different storage levels!" );
        return new RobotInventory( this.storageLevel, this.usedStorage + additionalInventory.usedStorage, this.resources.add( additionalInventory.resources ), this.full || additionalInventory.full, this.maxStorage );
    }

    public RobotInventory updateInventory(RobotInventoryDto robotInventoryDto){
        this.storageLevel = robotInventoryDto.getStorageLevel();
        this.usedStorage = robotInventoryDto.getUsedStorage();
        this.resources = this.resources.updateResources(robotInventoryDto.getResources());
        this.full = robotInventoryDto.getFull();
        this.maxStorage = robotInventoryDto.getMaxStorage();
        return this;
    }

    private boolean isEmpty() {
        return ( usedStorage == 0 );
    }
}

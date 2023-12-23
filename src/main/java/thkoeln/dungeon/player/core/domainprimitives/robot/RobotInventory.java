package thkoeln.dungeon.player.core.domainprimitives.robot;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotInventoryDto;

import java.util.UUID;


@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RobotInventory {
    private Integer storageLevel = 0;
    private Integer usedStorage = 0;

    @Embedded
    private RobotInventoryResources resources;

    private Boolean isCapped = Boolean.FALSE;
    private Integer maxStorage;


    public static RobotInventory emptyInventory() {
        return new RobotInventory(0, 0, RobotInventoryResources.empty(), false, 0);
    }


    public static RobotInventory fromStorageLevelAndMaxStorage(Integer storageLevel, Integer maxStorage) {
        if (storageLevel == null) throw new DomainPrimitiveException("StorageLevel cannot be null!");
        if (storageLevel < 0) throw new DomainPrimitiveException("StorageLevel must be >= 0!");
        if (maxStorage == null) throw new DomainPrimitiveException("MaxStorage cannot be null!");
        if (maxStorage <= 0) throw new DomainPrimitiveException("MaxStorage must be > 0!");
        return new RobotInventory(storageLevel, 0, RobotInventoryResources.empty(), false, maxStorage);
    }

    public void updateResource(MineableResource mineableResource) {
        this.resources.updateResource(mineableResource);
        this.usedStorage = this.resources.getUsedStorage();
        if (this.usedStorage >= this.maxStorage) {
            this.isCapped = true;
        }
    }

    public boolean isEmpty() {
        return (usedStorage == 0);
    }

    @Override
    public String toString() {
        return "RobotInventory{" +
                "storageLevel= " + storageLevel +
                ", usedStorage= " + usedStorage +
                ", maxStorage= " + maxStorage +
                ", resources= " + resources.toString() +
                '}';
    }
}

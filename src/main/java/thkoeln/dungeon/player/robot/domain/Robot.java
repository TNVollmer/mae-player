package thkoeln.dungeon.player.robot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Capability;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.CapabilityType;
import thkoeln.dungeon.player.core.domainprimitives.robot.Inventory;
import thkoeln.dungeon.player.core.domainprimitives.status.Activity;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.player.domain.Player;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Robot {

    @Id
    private UUID id = UUID.randomUUID();

    private UUID robotId;

    @ManyToOne
    private Player player;

    @ManyToOne
    private Planet planet;

    //TODO: add inventory and stats
    @Embedded
    private Inventory inventory = Inventory.fromCapacity(0);

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Capability> stats = Capability.allBaseCapabilities();

    private boolean isAlive = true;
    private Activity currentActivity = Activity.IDLE;


    public Robot(UUID robotId, Player player, Planet planet) {
        this.robotId = robotId;
        this.player = player;
        this.planet = planet;
    }

    public void changeInventorySize(Integer size) {
        this.inventory = Inventory.fromCapacityAndResources(size, this.inventory.getResources());
    }

    public void storeResources(MineableResource resource) {
        this.inventory = this.inventory.addMineableResource(resource);
    }

    public void move(CompassDirection direction) {
        planet = planet.getNeighbor(direction);
    }

    public boolean canMine() {
        return planet.hasResources() && planet.getResources().getType().getNeededMiningLevel().equals(getLevel(CapabilityType.MINING));
    }

    public boolean isFull() {
        return this.inventory.isFull();
    }

    public Integer getLevel(CapabilityType type) {
        for (Capability capability : stats) {
            if (capability.getType() == type) return  capability.getLevel();
        }

        throw new DomainPrimitiveException("Stat missing");
    }

}

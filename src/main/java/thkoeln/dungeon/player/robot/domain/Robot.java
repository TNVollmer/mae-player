package thkoeln.dungeon.player.robot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.domainprimitives.command.CommandType;
import thkoeln.dungeon.player.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Capability;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.CapabilityType;
import thkoeln.dungeon.player.core.domainprimitives.robot.Inventory;
import thkoeln.dungeon.player.core.domainprimitives.robot.TaskQueue;
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

    private Integer energy;
    private Integer health;

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
        this.inventory = inventory.addMineableResource(resource);
    }

    public void removeResources(MineableResource resource) {
        this.inventory = inventory.removeMineableResource(resource);
    }

    public CommandType choseNextCommand() {
        if (canMine()) return mine();

        //TODO : buy upgrade for mining check
        //TODO : move to next mineable planet
        //TODO : move to next unexplored planet

        return null;
    }

    public void moveToNextUnexploredPlanet() {
        List<Planet> planets = planet.getUnexploredNeighbors();
        if (!planets.isEmpty()) {
            planet = planets.get(0);
        }
    }

    public boolean canMove() {
        return energy >= planet.getMovementDifficulty();
    }

    public void move(CompassDirection direction) {
        planet = planet.getNeighbor(direction);
    }

    public void move(Planet planet) {
        this.planet = planet;
    }

    public boolean canMine() {
        return planet.hasResources() && planet.getResources().getType().getNeededMiningLevel().equals(getLevel(CapabilityType.MINING));
    }

    public CommandType mine() {
        this.currentActivity = Activity.MINING;
        return CommandType.MINING;
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

package thkoeln.dungeon.player.robot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.domainprimitives.command.CommandType;
import thkoeln.dungeon.player.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Capability;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.CapabilityType;
import thkoeln.dungeon.player.core.domainprimitives.robot.CommandQueue;
import thkoeln.dungeon.player.core.domainprimitives.robot.Inventory;
import thkoeln.dungeon.player.core.domainprimitives.status.Activity;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.player.domain.Player;

import java.util.*;

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

    @Embedded
    private CommandQueue queue = CommandQueue.emptyQueue();

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

    public boolean hasEmptyQueue() {
        return queue.isEmpty();
    }

    public CommandType getNextCommandType() {
        return queue.getNextType();
    }

    public Command fetchNextCommand() {
        Command command = queue.getCommand();
        this.queue = queue.getPolledQueue();
        return command;
    }

    public void queueCommand(Command command) {
        this.queue = queue.queueCommand(command);
    }

    public void queueAsFirstCommand(Command command) {
        this.queue = queue.queueAsFirstCommand(command);
    }

    public void moveToNextUnexploredPlanet() {
        for (Planet p : planet.getPathToNearestUnexploredPlanet()) {
            queueCommand(Command.createMove(robotId, p.getPlanetId(), player.getGameId(), player.getPlayerId()));
        }
    }

    public boolean canMove() {
        return energy >= planet.getMovementDifficulty();
    }

    public void escape() {
        List<Planet> planets = planet.getNeighbors();
        Planet random = planets.get(new Random().nextInt(planets.size()));
        queueAsFirstCommand(Command.createMove(this.getRobotId(), random.getPlanetId(), this.player.getGameId(), this.player.getPlayerId()));
    }

    public void move(CompassDirection direction) {
        planet = planet.getNeighbor(direction);
    }

    public void move(Planet planet) {
        this.planet = planet;
    }

    public void mine() {
        if (isFull()) {
            queueCommand(Command.createSelling(robotId, player.getGameId(), player.getPlayerId(), inventory.getResources().get(0)));
        }
        //TODO cancel condition for higher value resources?
        queueCommand(Command.createMining(robotId, planet.getPlanetId(), player.getGameId(), player.getPlayerId()));
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

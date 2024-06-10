package thkoeln.dungeon.player.robot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.domainprimitives.command.CommandType;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Capability;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.CapabilityType;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.player.core.domainprimitives.robot.Inventory;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;
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
    private Integer maxEnergy;
    private Integer health;
    private Integer maxHealth;

    private RobotType robotType;

    @Embedded
    private Command nextCommand;

    @Embedded
    private Inventory inventory;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Capability> stats = Capability.allBaseCapabilities();

    @Embedded
    private Capability nextUpgrade;

    private boolean isAlive = true;


    public Robot(UUID robotId, Player player, Planet planet, Integer inventorySize, Integer energy) {
        this.robotId = robotId;
        this.player = player;
        this.planet = planet;

        this.inventory = Inventory.fromCapacity(inventorySize);
        this.nextCommand = null;

        this.energy = energy;

        this.robotType = RobotType.Miner;
        chooseNextUpgrade();
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

    public void chooseNextCommand() {
        //TODO: Check health and heal if necessary
        if (canMine() && !canMineBetterResources()) {
            mine();
        } else {
            if (!moveToNearestPlanetWithBestMineableResources())
                moveToNextUnexploredPlanet();
            if (!canMove())
                setNextCommand(Command.createRegeneration(getRobotId(), player.getGameId(), player.getPlayerId()));
            if (canMine() && canMineBetterResources() && !inventory.isEmpty()) {
                setNextCommand(Command.createSelling(robotId, player.getGameId(), player.getPlayerId(), inventory.getResources().get(0)));
            }
        }
    }

    public boolean hasCommand() {
        return nextCommand != null;
    }

    public CommandType getCommandType() {
        return hasCommand() ? nextCommand.getCommandType() : null;
    }

    public Command getNextCommand() {
        Command command = nextCommand;
        nextCommand = null;
        return command;
    }

    public boolean canBuyUpgrade(Money budget) {
        return budget.greaterEqualThan(nextUpgrade.getUpgradePrice());
    }

    public Capability buyUpgrade() {
        Capability upgrade = nextUpgrade.nextLevel();
        chooseNextUpgrade();
        return upgrade;
    }

    private void chooseNextUpgrade() {
        List<CapabilityType> priorities = getUpgradePriorities();
        Capability selected = null;

        for (CapabilityType type : priorities) {
            Capability check = getCapability(type);
            if (selected == check || check.isMaximumLevel()) continue;
            if (selected == null || selected.getLevel() > check.getLevel())
                selected = check;
        }

        if (selected == null) {
            List<CapabilityType> types = new ArrayList<>(List.of(CapabilityType.values()));
            types.removeAll(priorities);

            for (CapabilityType type : types) {
                Capability capability = getCapability(type);
                if (selected == null || selected.getLevel() > capability.getLevel())
                    selected = capability;
            }
        }

        nextUpgrade = selected;
    }

    public boolean moveToNearestPlanetWithBestMineableResources() {
        return moveToNearestPlanetWithResource(MineableResourceType.getBestType(getLevel(CapabilityType.MINING)));
    }

    public boolean moveToNearestPlanetWithResource(MineableResourceType type) {
        List<Planet> path = planet.getPathToNearestPlanetWithResource(type);
        if (path.isEmpty()) return false;
        setMoveCommand(path.get(0));
        return true;
    }

    public boolean moveToNextUnexploredPlanet() {
        List<Planet> path = planet.getPathToNearestUnexploredPlanet();
        if (path.isEmpty()) return false;
        setMoveCommand(path.get(0));
        return true;
    }

    private void setMoveCommand(Planet toPlanet) {
        nextCommand = Command.createMove(robotId, toPlanet.getPlanetId(), player.getGameId(), player.getPlayerId());
    }

    public boolean canMove() {
        return (energy - 10) > planet.getMovementDifficulty();
    }

    public void escape() {
        List<Planet> planets = planet.getNeighbors();
        Planet random = planets.get(new Random().nextInt(planets.size()));
        nextCommand = Command.createMove(this.getRobotId(), random.getPlanetId(), this.player.getGameId(), this.player.getPlayerId());
    }

    public void move(Planet planet) {
        this.planet = planet;
    }

    public void mine() {
        if (isFull())
            nextCommand = Command.createSelling(robotId, player.getGameId(), player.getPlayerId(), inventory.getResources().get(0));
        else
            nextCommand = Command.createMining(robotId, planet.getPlanetId(), player.getGameId(), player.getPlayerId());
    }

    public boolean canMine() {
        return planet.hasResources() && planet.getResources().getType().canMineBeMinedBy(getLevel(CapabilityType.MINING));
    }

    public boolean canMineBetterResources() {
        return planet.getResources().getType().canMineBetterResources(getLevel(CapabilityType.MINING));
    }

    public boolean isFull() {
        return this.inventory.isFull();
    }

    public Integer getLevel(CapabilityType type) {
        return getCapability(type).getLevel();
    }

    public Capability getCapability(CapabilityType type) {
        for (Capability capability : stats) {
            if (capability.getType() == type) return  capability;
        }
        throw new DomainPrimitiveException("Stat missing");
    }

    public void upgradeCapability(CapabilityType type) {
        Capability toChange = null;
        for (Capability capability : stats) {
            if (capability.getType().equals(type)) {
                toChange = capability;
                break;
            }
        }
        if (toChange == null) return;
        stats.remove(toChange);
        stats.add(toChange.nextLevel());
    }

    public List<CapabilityType> getUpgradePriorities(){
        return switch (robotType) {
            case Scout -> List.of(
                    CapabilityType.ENERGY_REGEN,
                    CapabilityType.MAX_ENERGY
            );
            case Miner -> List.of(
                    CapabilityType.MINING,
                    CapabilityType.MINING_SPEED,
                    CapabilityType.STORAGE,
                    CapabilityType.ENERGY_REGEN,
                    CapabilityType.MAX_ENERGY,
                    CapabilityType.HEALTH);
            case Warrior -> List.of(
                    CapabilityType.DAMAGE,
                    CapabilityType.ENERGY_REGEN,
                    CapabilityType.MAX_ENERGY);
        };
    }
}

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
import thkoeln.dungeon.player.core.domainprimitives.purchasing.ItemType;
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
    private Integer damage;

    private Integer energyReserve = 10;

    private RobotType robotType;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Command> commandQueue = new ArrayList<>();

    @Embedded
    private Inventory inventory;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Capability> stats = Capability.allBaseCapabilities();

    @Embedded
    private Capability nextUpgrade;
    private Money upgradePrice;

    private boolean isAlive = true;


    public Robot(UUID robotId, Player player, Planet planet, RobotType robotType, Integer inventorySize, Integer maxEnergy, Integer maxHealth) {
        this.robotId = robotId;
        this.player = player;
        this.planet = planet;

        this.inventory = Inventory.fromCapacity(inventorySize);

        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.maxHealth = maxHealth;
        this.health = maxHealth;

        this.robotType = robotType;

        chooseNextUpgrade();
    }

    public void changeInventorySize(Integer size) {
        this.inventory = Inventory.fromCapacityAndResources(size, this.inventory.getResources());
    }

    public void setResourceInInventory(MineableResource resource) {
        this.inventory = inventory.setMineableResource(resource);
    }

    public void chooseNextCommand() {
        //TODO: move to a strategy
        if (health < (maxHealth/2)) {
            queueFirst(Command.createItemPurchase(ItemType.HEALTH_RESTORE, 1, robotId, player.getGameId(), player.getPlayerId()));
        } else if (robotType == RobotType.Miner) {
            if (canMine() && !canMineBetterResources()) {
                mine();
            } else {
                if (!moveToNearestPlanetWithBestMineableResources())
                    moveToNextUnexploredPlanet();
                if (canNotMove())
                    queueCommand(Command.createRegeneration(getRobotId(), player.getGameId(), player.getPlayerId()));
                if (canMine() && canMineBetterResources() && !inventory.isEmpty()) {
                    queueSellingResources();
                }
            }
        } else if (robotType == RobotType.Scout) {
            if (!moveToNextUnexploredPlanet())
                setRobotType(RobotType.Warrior);
        } else {
            if (canNotMove())
                queueFirst(Command.createRegeneration(getRobotId(), getPlayer().getGameId(), getPlayer().getPlayerId()));
            else {
                List<Planet> neighbours = getPlanet().getNeighbors();
                if (neighbours.isEmpty()) return;
                Planet random = neighbours.get(new Random().nextInt(neighbours.size()));
                queueCommand(Command.createMove(getRobotId(), random.getPlanetId(), getPlayer().getGameId(), getPlayer().getPlayerId()));
            }
        }
    }

    public void queueFirst(Command command) {
        List<Command> commands = new ArrayList<>();
        commands.add(command);
        if (!commandQueue.isEmpty())
            commands.addAll(commandQueue);
        commandQueue = commands;
    }

    public void queueCommand(Command command) {
        commandQueue.add(command);
    }

    public boolean hasCommand() {
        return !commandQueue.isEmpty();
    }

    public CommandType getCommandType() {
        return hasCommand() ? commandQueue.get(0).getCommandType() : null;
    }

    public Command getNextCommand() {
        if (!hasCommand()) return null;
        Command command = commandQueue.get(0);
        commandQueue.remove(0);
        return command;
    }

    public boolean canBuyUpgrade(Money budget) {
        if (upgradePrice == null) return false;
        return budget.greaterEqualThan(upgradePrice);
    }

    public Capability getQueuedUpgrade() {
        return nextUpgrade.nextLevel();
    }

    public void chooseNextUpgrade() {
        List<CapabilityType> priorities = RobotDecisionMaker.getUpgradePriorities(robotType);
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

        if (selected != null) {
            nextUpgrade = selected.nextLevel();
            upgradePrice = Shop.getPriceForItem(nextUpgrade.toStringForUpgrade());
        } else {
            nextUpgrade = null;
            upgradePrice = null;
        }
    }

    public boolean moveToNearestPlanetWithBestMineableResources() {
        return moveToNearestPlanetWithResource(MineableResourceType.getBestType(getLevel(CapabilityType.MINING)));
    }

    public boolean moveToNearestPlanetWithResource(MineableResourceType type) {
        List<Planet> path = planet.getPathToNearestPlanetWithResource(type);
        if (path.isEmpty()) return false;
        queueMovements(path);
        return true;
    }

    public boolean moveToNextUnexploredPlanet() {
        List<Planet> path = planet.getPathToNearestUnexploredPlanet();
        if (path.isEmpty()) return false;
        queueMovements(path);
        return true;
    }

    private void queueMovements(List<Planet> path) {
        for (Planet toPlanet : path) {
            queueCommand(Command.createMove(robotId, toPlanet.getPlanetId(), player.getGameId(), player.getPlayerId()));
        }
    }

    public boolean canNotMove() {
        return (energy - energyReserve) <= planet.getMovementDifficulty();
    }

    public void executeOnAttackBehaviour() {
        //TODO: use onAttackStrategie?
        if (robotType == RobotType.Miner) {
            commandQueue.clear();
            List<Planet> planets = planet.getNeighbors();
            if (planets.isEmpty()) return;
            Planet random = planets.get(new Random().nextInt(planets.size()));
            queueCommand(Command.createMove(this.getRobotId(), random.getPlanetId(), this.player.getGameId(), this.player.getPlayerId()));
        }
    }

    public void move(Planet planet) {
        this.planet = planet;
    }

    public void mine() {
        if (isFull())
            queueSellingResources();
        else
            queueCommand(Command.createMining(robotId, planet.getPlanetId(), player.getGameId(), player.getPlayerId()));
    }

    private void queueSellingResources() {
        for (MineableResource resource : inventory.getResources()) {
            queueCommand(Command.createSelling(robotId, player.getGameId(), player.getPlayerId(), resource));
        }
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
}

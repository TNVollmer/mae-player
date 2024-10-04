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
import thkoeln.dungeon.player.core.domainprimitives.robot.CommandQueue;
import thkoeln.dungeon.player.core.domainprimitives.robot.Inventory;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.robot.domain.strategies.TaskSelection;
import thkoeln.dungeon.player.trading.domain.Shop;

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
    private Integer sellPercentage = 50;

    private RobotType robotType;

    private CommandQueue commandQueue = CommandQueue.emptyQueue();

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

    /**
     * Chooses the next upgrade based on the robot type
     */
    public void chooseNextUpgrade() {
        List<CapabilityType> priorities = robotType.upgradeOrder();
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

    public boolean moveToNextUnexploredPlanet() {
        List<Planet> path = planet.getPathToNearestUnexploredPlanet();
        if (path.isEmpty()) return false;
        queueMovements(path);
        return true;
    }

    /**
     * Chooses the next command(s) based on the robot type but prioritizes healing if health is below half
     */
    public void chooseNextCommand() {
        if (health < (maxHealth/2)) {
            queueFirst(Command.createItemPurchase(ItemType.HEALTH_RESTORE, 1, robotId, player.getGameId(), player.getPlayerId()));
        } else {
            RobotDecisionMaker.getTaskSelectionByRobotType(robotType).queueNextTask(this);
        }
    }

    public void executeOnAttackBehaviour() {
        RobotDecisionMaker.getTaskSelectionByRobotType(robotType).onAttackAction(this);
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

    /**
     * Queues mining as next command unless sell threshold is reached
     */
    public void mine() {
        if (shouldSell())
            queueSellingResources();
        else
            queueCommand(Command.createMining(robotId, planet.getPlanetId(), player.getGameId(), player.getPlayerId()));
    }

    public void sellInventory() {
        queueSellingResources();
    }

    /**
     * Queues selling all resources in the inventory as next command(s)
     */
    private void queueSellingResources() {
        for (MineableResource resource : inventory.getResources()) {
            if (!resource.isEmpty())
                queueFirst(Command.createSelling(robotId, player.getGameId(), player.getPlayerId(), resource));
        }
    }

    /**
     * Queues movement commands based on the given path
     * @param path a list of planets which makes a movement path
     */
    private void queueMovements(List<Planet> path) {
        for (Planet toPlanet : path) {
            queueCommand(Command.createMove(robotId, toPlanet.getPlanetId(), player.getGameId(), player.getPlayerId()));
        }
    }

    public boolean canBuyUpgrade(Money budget) {
        if (upgradePrice == null) return false;
        return budget.greaterEqualThan(upgradePrice);
    }

    public boolean hasCommand() {
        return !commandQueue.isEmpty();
    }

    public CommandType getCommandType() {
        return commandQueue.getNextType();
    }

    public Command getNextCommand() {
        return commandQueue.getCommand();
    }

    public Integer getQueueSize() {
        return commandQueue.getSize();
    }

    public void queueFirst(Command command) {
        commandQueue = commandQueue.queueAsFirstCommand(command);
    }

    public void queueCommand(Command command) {
        commandQueue = commandQueue.queueCommand(command);
    }

    public void removeCommand() {
        commandQueue = commandQueue.getPolledQueue();
    }

    public void clearQueue() {
        commandQueue = CommandQueue.emptyQueue();
    }

    public Capability getQueuedUpgrade() {
        return nextUpgrade;
    }

    public boolean canNotMove() {
        return (energy - energyReserve) <= planet.getMovementDifficulty();
    }

    public void move(Planet planet) {
        this.planet = planet;
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

    public boolean shouldSell() {
        return this.inventory.getUsedCapacity() * 100 / this.inventory.getCapacity() > this.sellPercentage;
    }

    public void changeInventorySize(Integer size) {
        this.inventory = Inventory.fromCapacityAndResources(size, this.inventory.getResources());
    }

    //to ensure the inventory is correct resources are always completely replaced instead of calculating the new amount
    public void setResourceInInventory(MineableResource resource) {
        this.inventory = inventory.setMineableResource(resource);
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
}

package thkoeln.dungeon.player.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Capability;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.CapabilityType;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.ItemType;
import thkoeln.dungeon.player.core.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.player.game.application.GameApplicationService;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotException;

import java.util.Arrays;
import java.util.UUID;

@Service
public class RobotApplicationService {

    private final Logger logger = LoggerFactory.getLogger(RobotApplicationService.class);
    private final GameServiceRESTAdapter gameServiceRESTAdapter;
    private final PlayerApplicationService playerApplicationService;
    private final GameApplicationService gameApplicationService;

    @Autowired
    public RobotApplicationService(GameServiceRESTAdapter gameServiceRESTAdapter, PlayerApplicationService playerApplicationService, GameApplicationService gameApplicationService) {
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.playerApplicationService = playerApplicationService;
        this.gameApplicationService = gameApplicationService;
    }

    public void buyRobot(int amount) {
        UUID[] ids = getGameAndPlayerId();
        logger.info("Found game id: " + ids[0] + " and player id: " + ids[1]);
        Command buyRobotCommand = Command.createRobotPurchase(amount, ids[0], ids[1]);
        logger.info("Buying " + amount + " robots");
        gameServiceRESTAdapter.sendPostRequestForCommand(buyRobotCommand);
    }

    public void letRobotMove(Robot robot) {
        UUID neighbourPlanetId = robot.getRobotPlanet().randomNonNullNeighbourId();
        if (neighbourPlanetId == null) {
            logger.error("Robot " + robot.getRobotId() + " has no neighbours");
            logger.info("Planets neighbours: " + Arrays.toString(robot.getRobotPlanet().getNeighbours()));
            return;
        }
        Command moveRobotCommand = Command.createMove(robot.getRobotId(), neighbourPlanetId, getGameAndPlayerId()[0], getGameAndPlayerId()[1]);
        logger.info("Moving robot: " + robot.getRobotId() + " from planet: " + robot.getRobotPlanet().getPlanetId() + " to planet: " + neighbourPlanetId);
        gameServiceRESTAdapter.sendPostRequestForCommand(moveRobotCommand);
    }

    public void letRobotMine(Robot robot) {
        if (robot.getRobotPlanet().getMineableResource() == null) {
            logger.info("Robot " + robot.getRobotId() + " has no mineable resource");
            return;
        }
        Command mineCommand = Command.createMining(robot.getRobotId(), robot.getRobotPlanet().getPlanetId(), getGameAndPlayerId()[0], getGameAndPlayerId()[1]);
        logger.info("Robot " + robot.getRobotId() + " is mining");
        gameServiceRESTAdapter.sendPostRequestForCommand(mineCommand);
    }

    public void letRobotRegenerate(Robot robot) {
        Command regenerateCommand = Command.createRegeneration(robot.getRobotId(), getGameAndPlayerId()[0], getGameAndPlayerId()[1]);
        logger.info("Robot " + robot.getRobotId() + " is regenerating");
        gameServiceRESTAdapter.sendPostRequestForCommand(regenerateCommand);
    }

    public void letRobotSell(Robot robot) {
        MineableResource resourceToSell = robot.getRobotInventory().getResources().getHighestMinedResource();
        if (resourceToSell == null) {
            logger.error("Robot " + robot.getRobotId() + " has no resources to sell");
            throw new RobotException("Robot " + robot.getRobotId() + " has no resources to sell");
        }
        Command sellCommand = Command.createSelling(robot.getRobotId(), getGameAndPlayerId()[0], getGameAndPlayerId()[1], resourceToSell);
        logger.info("Robot " + robot.getRobotId() + " is selling: " + resourceToSell.getType() + " with amount: " + resourceToSell.getAmount());
        gameServiceRESTAdapter.sendPostRequestForCommand(sellCommand);
    }

    public void letRobotUpgrade(Robot robot) {
        //Capability capabilityToUpgrade = Capability.forTypeAndLevel(CapabilityType.valueOf(robot.getPendingUpgradeName()), robot.getPendingUpgradeLevel());
        Capability capabilityToUpgrade = Capability.forTypeAndLevel(CapabilityType.MINING, 5);
        Command upgradeCommand = Command.createUpgrade(capabilityToUpgrade, robot.getRobotId(), getGameAndPlayerId()[0], getGameAndPlayerId()[1]);
        logger.info("Robot " + robot.getRobotId() + " is upgrading: " + robot.getPendingUpgradeName() + " to level: " + robot.getPendingUpgradeLevel());
        UUID transactionId = gameServiceRESTAdapter.sendPostRequestForCommand(upgradeCommand);
        logger.warn("Sent upgrade command: " + upgradeCommand + "Response: " + transactionId);
    }

    public void letRobotFight(Robot robot, Robot target) {
        Command fightCommand = Command.createFight(robot.getRobotId(), getGameAndPlayerId()[0], getGameAndPlayerId()[1], target.getRobotId());
        logger.info("Robot " + robot.getRobotId() + " is fighting");
        gameServiceRESTAdapter.sendPostRequestForCommand(fightCommand);
    }

    public void letRobotBuyHealthRestoration(Robot robot) {
        Command buyHealthRestorationCommand = Command.createItemPurchase(ItemType.HEALTH_RESTORE, 1, robot.getRobotId(), getGameAndPlayerId()[0], getGameAndPlayerId()[1]);
        logger.info("Robot " + robot.getRobotId() + " is buying health restore");
        gameServiceRESTAdapter.sendPostRequestForCommand(buyHealthRestorationCommand);
    }

    public UUID[] getGameAndPlayerId() {
        UUID[] ids = new UUID[2];
        ids[0] = gameApplicationService.queryAndIfNeededFetchRemoteGame().getGameId();
        ids[1] = playerApplicationService.queryAndIfNeededCreatePlayer().getPlayerId();
        return ids;
    }


}

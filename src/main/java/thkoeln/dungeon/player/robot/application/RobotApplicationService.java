package thkoeln.dungeon.player.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.player.game.application.GameApplicationService;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;
import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotException;

import java.util.Arrays;
import java.util.UUID;

@Service
public class RobotApplicationService {

    private final Logger logger = LoggerFactory.getLogger(RobotApplicationService.class);
    private final GameServiceRESTAdapter gameServiceRESTAdapter;
    private final PlayerRepository playerRepository;
    private final PlayerApplicationService playerApplicationService;
    private final GameApplicationService gameApplicationService;

    @Autowired
    public RobotApplicationService(GameServiceRESTAdapter gameServiceRESTAdapter, PlayerRepository playerRepository, PlayerApplicationService playerApplicationService, GameApplicationService gameApplicationService) {
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.playerRepository = playerRepository;
        this.playerApplicationService = playerApplicationService;
        this.gameApplicationService = gameApplicationService;
    }

    public void buyRobot(int amount) {
        Command buyRobotCommand = Command.createRobotPurchase(amount, getGameAndPlayerId()[0], getGameAndPlayerId()[1]);
        logger.info("Buying " + amount + " robots");
        gameServiceRESTAdapter.sendPostRequestForCommand(buyRobotCommand);
    }

    public void moveAllRobots() {
        Player player = playerRepository.findAll().get(0);
        for (Robot robot : player.getRobots()) {
            UUID neighbourPlanetId = robot.getRobotPlanet().randomNonNullNeighbourId();
            if (neighbourPlanetId == null) {
                logger.info("Robot " + robot.getRobotId() + " has no neighbours");
                continue;
            }
            Command moveRobotCommand = Command.createMove(robot.getRobotId(), neighbourPlanetId, getGameAndPlayerId()[0], getGameAndPlayerId()[1]);
            logger.info("Moving robot: " + robot.getRobotId() + " to planet: " + neighbourPlanetId);
            gameServiceRESTAdapter.sendPostRequestForCommand(moveRobotCommand);
        }
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

    public void letRobotFight(Robot robot) {
        UUID planetId = robot.getRobotPlanet().getPlanetId();
        Command fightCommand = Command.createFight(robot.getRobotId(), planetId, getGameAndPlayerId()[0], getGameAndPlayerId()[1]);
        logger.info("Robot " + robot.getRobotId() + " is fighting");
        gameServiceRESTAdapter.sendPostRequestForCommand(fightCommand);
    }

    public UUID[] getGameAndPlayerId() {
        UUID[] ids = new UUID[2];
        ids[0] = gameApplicationService.queryAndIfNeededFetchRemoteGame().getGameId();
        ids[1] = playerApplicationService.queryAndIfNeededCreatePlayer().getPlayerId();
        return ids;
    }
}

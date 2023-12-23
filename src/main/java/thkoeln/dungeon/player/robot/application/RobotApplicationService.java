package thkoeln.dungeon.player.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.player.game.application.GameApplicationService;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;
import thkoeln.dungeon.player.robot.domain.Robot;
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
        gameServiceRESTAdapter.sendPostRequestForCommand(buyRobotCommand);
    }

    public void moveRobots() {
        Player player = playerRepository.findAll().get(0);
        for (Robot robot : player.getRobots()) {
            UUID neighbourPlanetId = robot.getRobotPlanet().randomNonNullNeighbourId();
            if (neighbourPlanetId == null) {
                logger.info("Robot " + robot.getId() + " has no neighbours");
                continue;
            }
            Command moveRobotCommand = Command.createMove(robot.getId(), neighbourPlanetId, getGameAndPlayerId()[0], getGameAndPlayerId()[1]);
            logger.info("Moving robot: " + robot.getId() + " to planet: " + neighbourPlanetId);
            gameServiceRESTAdapter.sendPostRequestForCommand(moveRobotCommand);
        }
    }

    public void letRobotMine() {
        UUID planetId = playerRepository.findAll().get(0).getRobots().get(0).getRobotPlanet().getPlanetId();
        Player player = playerRepository.findAll().get(0);
        for (Robot robot : player.getRobots()) {
            Command mineCommand = Command.createMining(robot.getId(), planetId, getGameAndPlayerId()[0], getGameAndPlayerId()[1]);
            logger.info("Robot " + robot.getId() + " is mining");
            gameServiceRESTAdapter.sendPostRequestForCommand(mineCommand);
        }
    }

    public UUID[] getGameAndPlayerId() {
        UUID[] ids = new UUID[2];
        ids[0] = gameApplicationService.queryAndIfNeededFetchRemoteGame().getGameId();
        ids[1] = playerApplicationService.queryAndIfNeededCreatePlayer().getPlayerId();
        return ids;
    }
}

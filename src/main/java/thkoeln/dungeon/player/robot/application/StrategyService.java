package thkoeln.dungeon.player.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

import java.util.List;

@Service
public class StrategyService {

    private final PlayerApplicationService playerApplicationService;
    private final RobotApplicationService robotApplicationService;

    private final RobotRepository robotRepository;
    private final Logger logger = LoggerFactory.getLogger(StrategyService.class);

    private final String loggerName = "StrategyService --> ";

    Player player;
    Money hypotheticalPlayerBalance = Money.zero();


    @Autowired
    public StrategyService(PlayerApplicationService playerApplicationService, RobotApplicationService robotApplicationService, RobotRepository robotRepository) {
        this.playerApplicationService = playerApplicationService;
        this.robotApplicationService = robotApplicationService;
        this.robotRepository = robotRepository;
    }

    @EventListener(RoundStatusEvent.class)
    public void runCommands(RoundStatusEvent roundStatusEvent) {
        if (!roundStatusEvent.getRoundStatus().equals(RoundStatusType.STARTED)) {
            return;
        }
        player = playerApplicationService.queryAndIfNeededCreatePlayer();
        hypotheticalPlayerBalance = player.getBalance();
        int round = roundStatusEvent.getRoundNumber();
        if (round == 2) {
            startOfGame();
        }
        List<Robot> robots = robotRepository.findAll();
        for (Robot robot : robots) {
            try {
                switch (robot.getStrategyStatus()) {
                    case "idle":
                        standardIdleStrategy(robot);
                        break;
                    case "mining":
                        standardMinerStrategy(robot);
                        break;
                    case "fighting":
                        logger.info(loggerName + "Fighting not yet implemented");
                        break;
                    default:
                        logger.info(loggerName + "No strategy found for robot: " + robot.getId());
                }
            } catch (Exception e) {
                logger.info(loggerName + "Exception: " + e);
            }
        }
        logger.info(loggerName + "Owned robots: " + robotRepository.findAll().size());
        for (Robot robot : robots) {
            if (roundStatusEvent.getRoundNumber() % 3 == 0) {
                logger.info(loggerName + "Detailed Robot Analysis: " + robot);
            } else {
                logger.info(loggerName + "Robot: " + robot.getId() + " is " + robot.getStrategyStatus());
            }
        }
    }

    private void startOfGame() {
        robotApplicationService.buyRobot(4);
        hypotheticalPlayerBalance.decreaseBy(Money.from(400));
    }

    private void standardIdleStrategy(Robot robot) {
        if (robot.getRobotPlanet().getMineableResource() == null || robot.getRobotPlanet().getMineableResource().isEmpty()) {
            if (robot.getRobotInventory().isEmpty()) {
                robotApplicationService.letRobotMove(robot);
                robot.setStrategyStatus("idle");
                robotRepository.save(robot);
            } else {
                robotApplicationService.letRobotSell(robot);
                robot.setStrategyStatus("idle");
                robotRepository.save(robot);
            }
        } else if (!robot.getRobotPlanet().getMineableResource().isEmpty()) {
            robotApplicationService.letRobotMine(robot);
            robot.setStrategyStatus("mining");
            robotRepository.save(robot);
        }
    }

    private void standardMinerStrategy(Robot robot) {
        if (robot.getRobotInventory().getIsCapped()) {
            robotApplicationService.letRobotSell(robot);
            robot.setStrategyStatus("idle");
            robotRepository.save(robot);
        } else {
            robotApplicationService.letRobotMine(robot);
            robot.setStrategyStatus("mining");
            robotRepository.save(robot);
        }
    }

}

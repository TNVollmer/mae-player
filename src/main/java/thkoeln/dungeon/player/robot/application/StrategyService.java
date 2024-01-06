package thkoeln.dungeon.player.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.TradeableItem;
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

        //Hier tritt immer der Fehler auf. Es wird versucht, die Liste der TradeableItems zu bekommen, aber es wird immer etwas zurückgegeben, dass das Programm crashen lässt.
        /* Gerne das try-catch entfernen, um das Programm zu terminieren */
        try {
            List<TradeableItem> priceList = player.getPriceList();
            logger.info(loggerName + "PriceList: " + priceList);
        } catch (Exception e) {
            logger.error(loggerName + "ERROR HERE: --> Exception: " + e);
        }

        List<Robot> robots = robotRepository.findByPlayerOwned(true);

        for (Robot robot : robots) {
            try {
                //TODO: Periodically check if robot is still alive, Remove robot from database if not
                //TODO: Periodically upgrade health, mining speed and damage, so Robots can survive longer and mine faster
                if (robot.getEnergy() <= 3) {
                    robotApplicationService.letRobotRegenerate(robot);
                    robot.setStrategyStatus("regenerating");
                    robotRepository.save(robot);
                    break;
                }
                switch (robot.getStrategyStatus()) {
                    case "idle":
                        standardIdleStrategy(robot);
                        break;
                    case "mining":
                        standardMinerStrategy(robot);
                        break;
                    case "fighting":
                        //TODO: Fighting needs to be implemented
                        logger.info(loggerName + "Fighting not yet implemented");
                        break;
                    case "regenerating":
                        standardRegenerationStrategy(robot);
                        break;
                    default:
                        logger.info(loggerName + "No strategy found for robot: " + robot.getRobotId() + " Setting strategy to idle.");
                        robot.setStrategyStatus("idle");
                }
            } catch (Exception e) {
                logger.error(loggerName + "Exception: " + e);
            }
        }
        logger.info(loggerName + "Owned robots: " + robotRepository.findByPlayerOwned(true).size());
        for (Robot robot : robots) {
            if (roundStatusEvent.getRoundNumber() % 4 == 0) {
                logger.info(loggerName + "Detailed Robot Analysis: " + robot);
            } else {
                logger.info(loggerName + "Robot: " + robot.getRobotId() + " is " + robot.getStrategyStatus());
            }
        }
    }

    private void standardRegenerationStrategy(Robot robot) {
        if (robot.getEnergy() >= robot.getMaxEnergy()) {
            logger.info(loggerName + "Robot: " + robot.getRobotId() + " is fully regenerated: " + robot.getEnergy() + "/" + robot.getMaxEnergy() + " Setting strategy to idle.");
            robotApplicationService.letRobotMove(robot);
            robot.setStrategyStatus("idle");
        } else {
            robotApplicationService.letRobotRegenerate(robot);
            robot.setStrategyStatus("regenerating");
        }
        robotRepository.save(robot);
    }

    private void startOfGame() {
        robotApplicationService.buyRobot(4);
        hypotheticalPlayerBalance.decreaseBy(Money.from(400));
    }

    private void standardIdleStrategy(Robot robot) {
        if (robot.getRobotPlanet().getMineableResource() == null || robot.getRobotPlanet().getMineableResource().isEmpty()) {
            if (robot.getRobotInventory().isEmpty()) {
                robotApplicationService.letRobotMove(robot);
            } else {
                robotApplicationService.letRobotSell(robot);
            }
            robot.setStrategyStatus("idle");
            robotRepository.save(robot);
        } else if (!robot.getRobotPlanet().getMineableResource().isEmpty()) {
            robotApplicationService.letRobotMine(robot);
            robot.setStrategyStatus("mining");
            robotRepository.save(robot);
        }
    }

    private void standardMinerStrategy(Robot robot) {
        //TODO: Miner needs to check, if he is able to mine resource. If not, he either needs to move to another planet or upgrade his mining level
        //TODO: Therefore a prioritization of upgrades is needed, as a way to determine which upgrade is the most important or which robot needs to move to another planet
        if (robot.getRobotInventory().getIsCapped()) {
            robotApplicationService.letRobotSell(robot);
            robot.setStrategyStatus("idle");
            robotRepository.save(robot);
        } else {
            if (!checkIfRobotCanMine(robot)) {
                if (checkReasonableMiningUpgrade(robot)) {
                    robot.setPendingUpgradeName("MINING");
                    robot.setPendingUpgradeLevel(robot.getMiningLevel() + 1);
                    robot.setPendingUpgradePriority(1);
                    robot.setStrategyStatus("mining");
                    checkUpgradePossibility(robot);
                } else {
                    robotApplicationService.letRobotMove(robot);
                    robot.setStrategyStatus("idle");
                }
            } else {
                robotApplicationService.letRobotMine(robot);
                robot.setStrategyStatus("mining");
            }
            robotRepository.save(robot);
        }
    }

    private void checkUpgradePossibility(Robot robot) {
        List<TradeableItem> priceList = player.getPriceList();
        Money pendingUpgradePrice = Money.zero();
        for (TradeableItem tradeableItem : priceList) {
            if (tradeableItem.getName().equals(robot.getPendingUpgradeName() + "_" + robot.getPendingUpgradeLevel())) {
                pendingUpgradePrice = tradeableItem.getPrice();
                break;
            }
        }
        List<Robot> robotsWithSameUpgradePriority = robotRepository.findByPendingUpgradePriority(robot.getPendingUpgradePriority());
        if (robotsWithSameUpgradePriority.isEmpty() && hypotheticalPlayerBalance.decreaseBy(pendingUpgradePrice).greaterEqualThan(Money.zero())) {
            robotApplicationService.letRobotUpgrade(robot);
            robot.setPendingUpgradeName(null);
            robot.setPendingUpgradeLevel(0);
            robot.setPendingUpgradePriority(0);
            robotRepository.save(robot);
        }
    }

    private boolean checkIfRobotCanMine(Robot robot) {
        return switch (robot.getRobotPlanet().getMineableResource().getType()) {
            case COAL -> true;
            case IRON -> robot.getMiningLevel() >= 1;
            case GEM -> robot.getMiningLevel() >= 2;
            case GOLD -> robot.getMiningLevel() >= 3;
            case PLATIN -> robot.getMiningLevel() >= 4;
        };
    }

    private boolean checkReasonableMiningUpgrade(Robot robot) {
        return switch (robot.getRobotPlanet().getMineableResource().getType()) {
            case COAL -> false;
            case IRON -> robot.getMiningLevel() == 0;
            case GEM -> robot.getMiningLevel() == 1;
            case GOLD -> robot.getMiningLevel() == 2;
            case PLATIN -> robot.getMiningLevel() == 3;
        };
    }

}

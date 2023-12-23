package thkoeln.dungeon.player.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

@Service
public class StrategyService {

    private PlayerApplicationService playerApplicationService;
    private RobotApplicationService robotApplicationService;

    private RobotRepository robotRepository;
    private final Logger logger = LoggerFactory.getLogger(StrategyService.class);

    private final String loggerName = "StrategyService --> ";


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
        int numberOfCommands = 0;
        int round = roundStatusEvent.getRoundNumber();
        if (round == 2) {
            logger.info(loggerName + ": Buying robot to start the game");
            robotApplicationService.buyRobot(1);
            numberOfCommands++;
        }
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
        logger.info(loggerName + "Owned robots: " + robotRepository.findAll().size());
        logger.info(loggerName + "Sent " + numberOfCommands + " commands");
    }
}

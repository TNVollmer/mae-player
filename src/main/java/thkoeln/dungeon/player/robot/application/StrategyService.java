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

@Service
public class StrategyService {

    private PlayerApplicationService playerApplicationService;
    private RobotApplicationService robotApplicationService;
    private final Logger logger = LoggerFactory.getLogger(StrategyService.class);

    private final String loggerName = "StrategyService --> ";


    @Autowired
    public StrategyService(PlayerApplicationService playerApplicationService, RobotApplicationService robotApplicationService) {
        this.playerApplicationService = playerApplicationService;
        this.robotApplicationService = robotApplicationService;
    }

    @EventListener(RoundStatusEvent.class)
    public void runCommands(RoundStatusEvent roundStatusEvent){
        if (!roundStatusEvent.getRoundStatus().equals(RoundStatusType.STARTED)){return;}
        int round = roundStatusEvent.getRoundNumber();
        if (round == 2){
            logger.info(loggerName + ": Buying robot to start the game");
            robotApplicationService.buyRobot(1);
        }
        int numberOfCommands = 0;
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
        logger.info(loggerName + "Sent " + numberOfCommands + " commands");
    }
}

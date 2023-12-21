package thkoeln.dungeon.player.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.game.GameStatusEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.robot.reveal.RobotsRevealedEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.player.core.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.player.game.domain.GameRepository;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.player.domain.PlayerRepository;
import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

import java.util.Arrays;
import java.util.UUID;

@Service
public class RobotApplicationService {

    private Logger logger = LoggerFactory.getLogger(PlayerApplicationService.class);
    private GameServiceRESTAdapter gameServiceRESTAdapter;
    private RobotRepository robotRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;

    @Autowired
    public RobotApplicationService(GameServiceRESTAdapter gameServiceRESTAdapter, RobotRepository robotRepository, GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.robotRepository = robotRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    @EventListener(RobotsRevealedEvent.class)
    public void displayRobotData(RobotsRevealedEvent robotsRevealedEvent) {
        logger.info("Robot data: " + Arrays.toString(robotsRevealedEvent.getRobots()));
    }

    @EventListener(RobotSpawnedEvent.class)
    public void saveNewRobot(RobotSpawnedEvent robotSpawnedEvent) {
        //TODO: Hier müssen die Daten aus dem JSON-String in ein Robot-Objekt umgewandelt werden
        Robot newRobot = new Robot(/* TODO: Hier müssen dann die umgewandelten Daten rein */);
        robotRepository.save(newRobot);
    }

    @EventListener(RoundStatusEvent.class)
    public void buyRobot(){
        Command buyRobotCommand = Command.createRobotPurchase(1, getGameAndPlayerId()[0], getGameAndPlayerId()[1]);
        gameServiceRESTAdapter.sendPostRequestForCommand(buyRobotCommand);
    }

    public UUID[] getGameAndPlayerId(){
        UUID[] ids = new UUID[2];
        ids[0] = gameRepository.findAll().get(0).getId();
        ids[1] = playerRepository.findAll().get(0).getId();
        return ids;
    }
}

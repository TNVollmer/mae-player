package thkoeln.dungeon.player.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.robot.reveal.RobotsRevealedEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.player.core.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

@Service
public class RobotApplicationService {

    private Logger logger = LoggerFactory.getLogger(PlayerApplicationService.class);
    private GameServiceRESTAdapter gameServiceRESTAdapter;

    private RobotRepository robotRepository;

    @Autowired
    public RobotApplicationService(GameServiceRESTAdapter gameServiceRESTAdapter, RobotRepository robotRepository) {
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.robotRepository = robotRepository;
    }

    @EventListener(RobotsRevealedEvent.class)
    public void displayRobotData(RobotsRevealedEvent robotsRevealedEvent) {
        logger.info("Robot data: " + robotsRevealedEvent.getRobots());
    }

    @EventListener(RobotSpawnedEvent.class)
    public void saveNewRobot(RobotSpawnedEvent robotSpawnedEvent) {
        //TODO: Hier müssen die Daten aus dem JSON-String in ein Robot-Objekt umgewandelt werden
        Robot newRobot = new Robot(/* TODO: Hier müssen dann die umgewandelten Daten rein */);
        robotRepository.save(newRobot);
    }
}

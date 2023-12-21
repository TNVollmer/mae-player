package thkoeln.dungeon.player.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;

@Service
public class RobotApplicationService {

    private Logger logger = LoggerFactory.getLogger(PlayerApplicationService.class);
    private GameServiceRESTAdapter gameServiceRESTAdapter;

    @Autowired
    public RobotApplicationService(GameServiceRESTAdapter gameServiceRESTAdapter) {
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
    }

    public void displayRobotData(String dataAsJSON) {
        logger.info("Robot data: " + dataAsJSON);
    }
}

package thkoeln.dungeon.player.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetNeighboursDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotsRevealedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.trading.BankInitializedEvent;
import thkoeln.dungeon.player.core.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.player.game.domain.GameRepository;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;
import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotPlanet;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class RobotApplicationService {

    private final Logger logger = LoggerFactory.getLogger(PlayerApplicationService.class);
    private final GameServiceRESTAdapter gameServiceRESTAdapter;
    private final RobotRepository robotRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

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
        RobotDto robotDto = robotSpawnedEvent.getRobotDto();
        Robot newRobot = new Robot(robotDto.getId(), robotDto.getPlanet().getPlanetId());
        robotRepository.save(newRobot);
        logger.info("Robot spawned: " + newRobot.toString());
        Player player = playerRepository.findAll().get(0);
        player.getRobots().add(newRobot);
        playerRepository.save(player);
    }

    public void saveRobot(Robot robot){
        robotRepository.save(robot);
    }


    //TODO: Später muss das hier im StrategyService aufgerufen werden und muss variable Mengen an Robots kaufen können
    @EventListener(BankInitializedEvent.class)
    public void buyRobot() {
        Command buyRobotCommand = Command.createRobotPurchase(1, getGameAndPlayerId()[0], getGameAndPlayerId()[1]);
        gameServiceRESTAdapter.sendPostRequestForCommand(buyRobotCommand);
    }

    public void moveRobots(){
        Player player = playerRepository.findAll().get(0);
        for(Robot robot: player.getRobots()){
            UUID neighbourPlanetId = robot.getRobotPlanet().randomNonNullNeighbourId();
            if (neighbourPlanetId == null){
                logger.info("Robot " + robot.getId() + " has no neighbours");
                continue;
            }
            Command moveRobotCommand = Command.createMove(robot.getId(), neighbourPlanetId, getGameAndPlayerId()[0], getGameAndPlayerId()[1]);
            logger.info("Moving robot: " + robot.getId() + " to planet: " + neighbourPlanetId);
            gameServiceRESTAdapter.sendPostRequestForCommand(moveRobotCommand);
        }
    }

    @EventListener(PlanetDiscoveredEvent.class)
    public void savePlanet(PlanetDiscoveredEvent planetDiscoveredEvent){
        List<Robot> robotsOnPlanet = robotRepository.findByRobotPlanetPlanetId(planetDiscoveredEvent.getPlanetId());
        if (robotsOnPlanet.isEmpty()){
            logger.info("No robots on planet: " + planetDiscoveredEvent.getPlanetId());
            return;
        }
        PlanetNeighboursDto[] planetNeighbours = planetDiscoveredEvent.getNeighbours();
        for (Robot robot: robotsOnPlanet){
            RobotPlanet updatedRobotPlanet = RobotPlanet.planetWithNeighbours(planetDiscoveredEvent.getPlanetId(), planetNeighbours);
            robot.setRobotPlanet(updatedRobotPlanet);
            robotRepository.save(robot);
            logger.info("Updated robot: " + robot.getId() + " with planet: " + updatedRobotPlanet.toString());
        }
    }

    public UUID[] getGameAndPlayerId() {
        UUID[] ids = new UUID[2];
        ids[0] = gameRepository.findAll().get(0).getId();
        ids[1] = playerRepository.findAll().get(0).getId();
        return ids;
    }
}

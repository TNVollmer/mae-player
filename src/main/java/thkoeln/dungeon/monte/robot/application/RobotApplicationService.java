package thkoeln.dungeon.monte.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.RobotRegeneratedEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal.RobotRevealedDto;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal.RobotsRevealedEvent;
import thkoeln.dungeon.monte.core.util.PlayerInformation;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.player.application.RobotDtoMapper;
import thkoeln.dungeon.monte.robot.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static thkoeln.dungeon.monte.robot.domain.RobotType.*;

@Service
public class RobotApplicationService {
    private Logger logger = LoggerFactory.getLogger( RobotApplicationService.class );
    private RobotRepository robotRepository;
    private PlayerInformation playerInformation;
    private PlanetApplicationService planetApplicationService;
    private RobotDtoMapper robotDtoMapper;

    @Autowired
    public RobotApplicationService( RobotRepository robotRepository,
                                    PlayerInformation playerInformation,
                                    PlanetApplicationService planetApplicationService,
                                    RobotDtoMapper robotDtoMapper ) {
        this.robotRepository = robotRepository;
        this.playerInformation = playerInformation;
        this.planetApplicationService = planetApplicationService;
        this.robotDtoMapper = robotDtoMapper;
    }


    public void cleanupAfterFinishingGame() {
        robotRepository.deleteAll();
    }



    /**
     * Add new robot, as result of an event. The robot type is decided according to current quotas.
     * @return the new robot
     */
    public Robot addNewOwnRobot( UUID robotId, Planet planet ) {
        if ( robotId == null || planet == null ) throw new RobotException( "robotId == null || planet == null" );
        if ( robotRepository.existsByRobotId( robotId ) )
            throw new RobotException( "Robot ID " + robotId + " already there!" );
        logger.info( "Add new robot for id " + robotId + " on planet " + planet );
        RobotType robotType = nextRobotTypeAccordingToQuota();
        Robot robot = Robot.of( robotId, robotType, playerInformation.currentGameId(), playerInformation.currentPlayerId() );
        robot.moveToPlanet( planet );
        robotRepository.save( robot );
        logger.debug( "Added robot " + robot );
        return robot;
    }


    /**
     * Search for a robot (enemy or own), and create it new if not found
     * @param robotId
     * @param enemyChar - the char denoting an enemy player, or null if it is an own robot
     * @return the found (or newly added) robot
     */
    public Robot queryAndIfNeededAddRobot( UUID robotId, Character enemyChar ) {
        if ( robotId == null ) throw new RobotException( "robotId == null" );
        logger.info( "Query for " + (enemyChar != null ? "enemy " + enemyChar : "") + " robot with ID " + robotId );
        Optional<Robot> perhapsRobot = robotRepository.findByRobotId( robotId );
        Robot robot;
        if ( perhapsRobot.isPresent() ) {
            robot = perhapsRobot.get();
            logger.debug( "Robot already there: " + robot );
            if ( robot.isEnemy() != ( enemyChar != null ) ) throw new RobotException( "Expected enemy = " +
                    (enemyChar != null) + ", found " + robot.isEnemy() + "!" );
            if ( robot.isEnemy() && ( enemyChar != robot.enemyChar() ) )
                throw new RobotException( "Enemy robot has char " + robot.enemyChar() + ", expected " + enemyChar );
            return robot;
        }
        if ( enemyChar == null ) {
            RobotType robotType = nextRobotTypeAccordingToQuota();
            robot = Robot.of( robotId, robotType, playerInformation.currentGameId(), playerInformation.currentPlayerId());
        }
        else {
            robot = Robot.of( robotId, playerInformation.currentGameId() );
            robot.setEnemyChar( enemyChar );
        }
        robotRepository.save( robot );
        logger.debug( "Added robot " + robot );
        return robot;
    }


    /**
     * Search for a robot (enemy or own), and create it new if not found
     */
    public Robot queryAndIfNeededAddRobot( UUID robotId ) {
        return queryAndIfNeededAddRobot( robotId, null );
    }


    /**
     * Move a robot to a new planet as a result of a movement event
     * @param robotId
     * @param updatedEnergy
     * @param planet
     * @return
     */
    public Robot moveRobotToNewPlanet( UUID robotId, Planet planet, Energy updatedEnergy ) {
        if ( robotId == null || planet == null || updatedEnergy == null )
            throw new RobotException( "robotId == null || planet == null || updatedEnergy == null" );
        logger.info( "Move robot to new planet ..." );
        Optional<Robot> perhapsRobot = robotRepository.findByRobotId( robotId );
        if ( !perhapsRobot.isPresent() ) {
            logger.warn( "Robot with ID " + robotId + " is unknown!" );
            return null;
        }
        Robot robot = perhapsRobot.get();
        robot.verifyAndIfNeededUpdate( planet, updatedEnergy );
        robotRepository.save( robot );
        planetApplicationService.save( robot.getLocation() );
        return robot;
    }


    /**
     * Move a robot to a new planet as a result of a movement event
     * @param event
     */
    public Robot regenerateRobotFromExternalEvent( RobotRegeneratedEvent event ) {
        logger.info( "Regenerate robot as reaction to event ..." );
        Optional<Robot> perhapsRobot = robotRepository.findByRobotId( event.getRobotId() );
        if ( !perhapsRobot.isPresent() ) {
            logger.warn( "Robot with ID " + event.getRobotId() + " is unknown!" );
            return null;
        }
        Robot robot = perhapsRobot.get();
        robot.updateEnergy( Energy.from( event.getAvailableEnergy() ) );
        robotRepository.save( robot );
        return robot;
    }



    public void updateRobotsFromExternalEvent( RobotsRevealedEvent event ) {
        logger.info( "Update all robots from event ..." );
        for ( RobotRevealedDto robotRevealedDto : event.getRobots() ) {
            Robot robot = queryAndIfNeededAddRobot( robotRevealedDto.getRobotId(), robotRevealedDto.getEnemyChar() );
            boolean warnIfDiff = ( robotRevealedDto.getEnemyChar() == null );
            robotDtoMapper.updateRobot( robot, robotRevealedDto, robotRevealedDto.getEnemyChar() == null );
            robotRepository.save( robot );
        }
    }


    public void robotHasMined( UUID robotId, MineableResource minedResource, MineableResource updatedInventory ) {
        if ( robotId == null ) throw new RobotException( "robotId == null" );
        Robot robot = queryAndIfNeededAddRobot( robotId );
        logger.info( "Robot " + robot + " has mined " + minedResource + " and now has " + updatedInventory );
        robot.updateInventoryAfterMining( minedResource, updatedInventory );
        robotRepository.save( robot );
    }

    /**
     * @return The RobotType the next purchased robot should be assigned, according to quota
     */
    RobotType nextRobotTypeAccordingToQuota() {
        long numOfRobots = robotRepository.countAllByEnemyCharIsNullAndAliveIs( true );
        long numOfWarriors = robotRepository.countAllByTypeIs( WARRIOR );
        if ( numOfRobots == 0 || (numOfWarriors * 100 / numOfRobots) < WARRIOR.quota() ) return WARRIOR;

        Long numOfScouts = robotRepository.countAllByTypeIs( SCOUT );
        if ( numOfRobots == 0 || (numOfScouts * 100 / numOfRobots) < SCOUT.quota() ) return SCOUT;

        return MINER;
    }


    /**
     * @return all robots currently alive (including enemies)
     */
    public List<Robot> allLivingRobots() {
        List<Robot> robots = robotRepository.findAllByAliveEquals( true );
        return robots;
    }


    /**
     * @return all OWN robots currently alive, sorted by type
     */
    public List<Robot> allLivingOwnRobots() {
        List<Robot> robots = robotRepository.findAllByEnemyCharIsNullAndAliveEqualsOrderByType( true );
        return robots;
    }


    /**
     * @return all ENEMY robots currently alive, sorted by enemy
     */
    public List<Robot> allLivingEnemyRobots() {
        List<Robot> robots = robotRepository.findAllByEnemyCharIsNotNullAndAliveEqualsOrderByEnemyChar( true );
        return robots;
    }



    /**
     * @return Find the robots on a specific planet (including enemies)
     */
    public List<Robot> livingRobotsOnPlanet( Planet planet ) {
        if ( planet == null ) return new ArrayList<>();
        List<Robot> robotsOnPlanet = robotRepository.findAllByLocationIsAndAliveIsTrue( planet );
        return robotsOnPlanet;
    }

}

package thkoeln.dungeon.monte.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.RobotRegeneratedIntegrationEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal.RobotRevealedDto;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal.RobotsRevealedIntegrationEvent;
import thkoeln.dungeon.monte.core.strategy.AbstractStrategy;
import thkoeln.dungeon.monte.core.util.PlayerInformation;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.player.application.RobotDtoMapper;
import thkoeln.dungeon.monte.printer.finderservices.RobotFinderService;
import thkoeln.dungeon.monte.printer.printables.PlanetPrintable;
import thkoeln.dungeon.monte.robot.domain.*;
import thkoeln.dungeon.monte.trading.application.TradingAccountApplicationService;
import thkoeln.dungeon.monte.trading.domain.TradingAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static thkoeln.dungeon.monte.robot.domain.RobotType.*;

@Service
public class RobotApplicationService implements RobotFinderService {
    private Logger logger = LoggerFactory.getLogger( RobotApplicationService.class );
    private RobotRepository robotRepository;
    private PlayerInformation playerInformation;
    private PlanetApplicationService planetApplicationService;
    private TradingAccountApplicationService tradingAccountApplicationService;
    private RobotDtoMapper robotDtoMapper;

    private AbstractRobotStrategy warriorStrategy, scoutStrategy, minerStrategy;

    @Autowired
    public RobotApplicationService( RobotRepository robotRepository,
                                    PlayerInformation playerInformation,
                                    TradingAccountApplicationService tradingAccountApplicationService,
                                    PlanetApplicationService planetApplicationService,
                                    RobotDtoMapper robotDtoMapper,
                                    @Qualifier( "warriorStrategy" ) AbstractRobotStrategy warriorStrategy,
                                    @Qualifier( "scoutStrategy" ) AbstractRobotStrategy scoutStrategy,
                                    @Qualifier( "minerStrategy" ) AbstractRobotStrategy minerStrategy ) {
        this.robotRepository = robotRepository;
        this.playerInformation = playerInformation;
        this.tradingAccountApplicationService = tradingAccountApplicationService;
        this.planetApplicationService = planetApplicationService;
        this.robotDtoMapper = robotDtoMapper;
        this.warriorStrategy = warriorStrategy;
        this.scoutStrategy = scoutStrategy;
        this.minerStrategy = minerStrategy;
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
        return withStrategy( robot );
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
            return withStrategy( robot );
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
        return withStrategy( robot );
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
        return withStrategy( robot );
    }


    /**
     * Move a robot to a new planet as a result of a movement event
     * @param event
     */
    public Robot regenerateRobotFromExternalEvent( RobotRegeneratedIntegrationEvent event ) {
        logger.info( "Regenerate robot as reaction to event ..." );
        Optional<Robot> perhapsRobot = robotRepository.findByRobotId( event.getRobotId() );
        if ( !perhapsRobot.isPresent() ) {
            logger.warn( "Robot with ID " + event.getRobotId() + " is unknown!" );
            return null;
        }
        Robot robot = perhapsRobot.get();
        robot.updateEnergy( Energy.from( event.getAvailableEnergy() ) );
        robotRepository.save( robot );
        return withStrategy( robot );
    }



    public void updateRobotsFromExternalEvent( RobotsRevealedIntegrationEvent event ) {
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
    protected RobotType nextRobotTypeAccordingToQuota() {
        long numOfRobots = robotRepository.countAllByEnemyCharIsNullAndAliveIs( true );
        long numOfWarriors = robotRepository.countAllByTypeIs( WARRIOR );
        if ( numOfRobots == 0 || (numOfWarriors * 100 / numOfRobots) < WARRIOR.quota() ) return WARRIOR;

        Long numOfMiners = robotRepository.countAllByTypeIs( MINER );
        if ( numOfRobots == 0 || (numOfMiners * 100 / numOfRobots) < MINER.quota() ) return MINER;

        return SCOUT;
    }


    /**
     * @return all robots currently alive (including enemies)
     */
    public List<Robot> allLivingRobots() {
        List<Robot> robots = robotRepository.findAllByAliveEquals( true );
        robots.stream().forEach( robot -> { robot.setStrategy( getStrategyFor( robot ) ); } );
        return robots;
    }


    public List<Robot> allLivingOwnRobots() {
        List<Robot> robots = robotRepository.findAllByEnemyCharIsNullAndAliveEquals( true );
        robots.stream().forEach( robot -> { robot.setStrategy( getStrategyFor( robot ) ); } );
        return robots;
    }


    public List<Robot> allLivingEnemyRobots() {
        List<Robot> robots = robotRepository.findAllByEnemyCharIsNotNullAndAliveEquals( true );
        robots.stream().forEach( robot -> { robot.setStrategy( getStrategyFor( robot ) ); } );
        return robots;
    }



    /**
     * @return Find the robots on a specific planet (including enemies)
     */
    @Override
    public List<Robot> livingRobotsOnPlanet( PlanetPrintable planetPrintable ) {
        if ( planetPrintable == null ) return new ArrayList<>();
        Planet planet = (Planet) planetPrintable;
        List<Robot> robotsOnPlanet = robotRepository.findAllByLocationIsAndAliveIsTrue( planet );
        robotsOnPlanet.stream().forEach( robot -> { robot.setStrategy( getStrategyFor( robot ) ); } );
        return robotsOnPlanet;
    }


    public void decideAllRobotCommands() {
        List<Robot> robots = allLivingRobots();
        TradingAccount tradingAccount = tradingAccountApplicationService.queryAndIfNeededCreateTradingAccount();
        AbstractStrategy.findNextCommandsForGroup( robots, tradingAccount );
        for ( Robot robot : robots ) {
            robotRepository.save( robot );
            planetApplicationService.save( robot.getLocation() );
        }
        tradingAccountApplicationService.save( tradingAccount );
    }


    public List<Command> currentRobotCommands() {
        List<Robot> robots = allLivingRobots();
        List<Command> commands = new ArrayList<>();
        for ( Robot robot : robots ) {
            if ( robot.getRecentCommand() != null ) commands.add( robot.getRecentCommand() );
        }
        return commands;
    }


    private AbstractRobotStrategy getStrategyFor( Robot robot ) {
        if ( robot == null || robot.isEnemy() ) return null;
        RobotType robotType = robot.getType();
        AbstractRobotStrategy strategy = SCOUT.equals( robotType ) ? scoutStrategy :
                MINER.equals( robotType ) ? minerStrategy : warriorStrategy;
        return strategy;
    }

    private Robot withStrategy( Robot robot ) {
        if ( robot == null ) throw new RobotException( "robot == null" );
        AbstractRobotStrategy strategy = getStrategyFor( robot );
        robot.setStrategy( strategy );
        return robot;
    }

}

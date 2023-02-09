package thkoeln.dungeon.monte.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.RobotRegeneratedIntegrationEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal.RobotsRevealedIntegrationEvent;
import thkoeln.dungeon.monte.core.strategy.AbstractStrategy;
import thkoeln.dungeon.monte.core.util.PlayerInformation;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.move.RobotMovedIntegrationEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.printer.finderservices.RobotFinderService;
import thkoeln.dungeon.monte.printer.printables.PlanetPrintable;
import thkoeln.dungeon.monte.robot.domain.*;
import thkoeln.dungeon.monte.trading.application.TradingAccountApplicationService;
import thkoeln.dungeon.monte.trading.domain.TradingAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Boolean.TRUE;
import static thkoeln.dungeon.monte.robot.domain.RobotType.*;

@Service
public class RobotApplicationService implements RobotFinderService {
    private Logger logger = LoggerFactory.getLogger( RobotApplicationService.class );
    private RobotRepository robotRepository;
    private PlayerInformation playerInformation;
    private PlanetApplicationService planetApplicationService;
    private TradingAccountApplicationService tradingAccountApplicationService;

    private AbstractRobotStrategy warriorStrategy, scoutStrategy, minerStrategy;
    @Autowired @Qualifier( "warriorStrategy" )
    public void setWarriorStrategy( AbstractRobotStrategy warriorStrategy ) {
        this.warriorStrategy = warriorStrategy;
    }
    @Autowired @Qualifier( "scoutStrategy" )
    public void setScoutStrategy( AbstractRobotStrategy scoutStrategy ) {
        this.scoutStrategy = scoutStrategy;
    }
    @Autowired @Qualifier( "minerStrategy" )
    public void setMinerStrategy(AbstractRobotStrategy minerStrategy) {
        this.minerStrategy = minerStrategy;
    }

    @Autowired
    public RobotApplicationService( RobotRepository robotRepository,
                                    PlayerInformation playerInformation,
                                    TradingAccountApplicationService tradingAccountApplicationService,
                                    PlanetApplicationService planetApplicationService ) {
        this.robotRepository = robotRepository;
        this.playerInformation = playerInformation;
        this.tradingAccountApplicationService = tradingAccountApplicationService;
        this.planetApplicationService = planetApplicationService;
    }


    /**
     * Add new robot as result of an RobotSpawnedEvent. The robot type is decided according to current quotas.
     * @param robotSpawnedEvent
     * @return the new robot
     */
    public Robot addNewRobotFromEvent( RobotSpawnedEvent robotSpawnedEvent ) {
        Planet planet = planetApplicationService.addOrUpdatePlanet( robotSpawnedEvent.getRobotDto().getPlanet(), TRUE );
        if ( robotSpawnedEvent == null || !robotSpawnedEvent.isValid() || planet == null )
            throw new RobotException( "robotSpawnedEvent == null || !robotSpawnedEvent.isValid() || planet == null" );
        logger.info( "About to add new robot for RobotSpawnedEvent ...");
        RobotType robotType = nextRobotTypeAccordingToQuota();
        Robot robot = Robot.of( robotSpawnedEvent.getRobotDto().getId(), robotType,
                                playerInformation.currentGameId(), playerInformation.currentPlayerId() );
        robot.setStrategy( getStrategyFor( robot ) );
        robot.moveToPlanet( planet );
        robotRepository.save( robot );
        planetApplicationService.save( planet );
        logger.info( "Added robot " + robot );
        return robot;
    }


    /**
     * Move a robot to a new planet as a result of a movement event
     * @param event
     */
    public void moveRobotFromEvent( RobotMovedIntegrationEvent event ) {
        logger.info( "Move robot to new planet as reaction to event ..." );
        Planet updatedLocation = planetApplicationService.addOrUpdatePlanet( event.getToPlanet().getId(),
                Energy.from( event.getToPlanet().getMovementDifficulty() ), null );
        Energy updatedEnergy = Energy.from(event.getRemainingEnergy() );
        Optional<Robot> perhapsRobot = findRobotById( event.getRobotId() );
        if ( !perhapsRobot.isPresent() ) {
            logger.warn( "Robot with ID " + event.getRobotId() + " is unknown!" );
            return;
        }
        Robot robot = perhapsRobot.get();
        robot.verifyAndIfNeededUpdate( updatedLocation, updatedEnergy );
        robotRepository.save( robot );
        planetApplicationService.save( robot.getLocation() );
    }


    /**
     * Move a robot to a new planet as a result of a movement event
     * @param event
     */
    public void regenerateRobotFromEvent( RobotRegeneratedIntegrationEvent event ) {
        logger.info( "Regenerate robot as reaction to event ..." );
        Optional<Robot> perhapsRobot = findRobotById( event.getRobotId() );
        if ( !perhapsRobot.isPresent() ) {
            logger.warn( "Robot with ID " + event.getRobotId() + " is unknown!" );
            return;
        }
        Robot robot = perhapsRobot.get();
        robot.updateEnergy( Energy.from( event.getAvailableEnergy() ) );
        robotRepository.save( robot );
    }


    /**
     * Move a robot to a new planet as a result of a movement event
     * @param event
     */
/*
    public void updateRobotPositions( RobotsRevealedIntegrationEvent event ) {
        logger.info( "Update all robot positions ..." );
        Optional<Robot> perhapsRobot = findRobotById( event.getRobotId() );
        if ( !perhapsRobot.isPresent() ) {
            logger.warn( "Robot with ID " + event.getRobotId() + " is unknown!" );
            return;
        }
        Robot robot = perhapsRobot.get();
        robot.updateEnergy( Energy.from( event.getAvailableEnergy() ) );
        robotRepository.save( robot );
    }

*/


    /**
     * @return The RobotType the next purchased robot should be assigned, according to quota
     */
    protected RobotType nextRobotTypeAccordingToQuota() {
        long numOfRobots = robotRepository.countAllByAliveIs( true );
        long numOfWarriors = robotRepository.countAllByTypeIs( WARRIOR );
        if ( numOfRobots == 0 || (numOfWarriors * 100 / numOfRobots) < WARRIOR.quota() ) return WARRIOR;

        Long numOfMiners = robotRepository.countAllByTypeIs( MINER );
        if ( numOfRobots == 0 || (numOfMiners * 100 / numOfRobots) < MINER.quota() ) return MINER;

        return SCOUT;
    }


    public Optional<Robot> findRobotById( UUID robotId ) {
        return robotRepository.findById( robotId );
    }


    /**
     * @return all robots currently alive
     */
    public List<Robot> allLivingRobots() {
        List<Robot> robots = robotRepository.findAllByAliveEquals( true );
        robots.stream().forEach( robot -> { robot.setStrategy( getStrategyFor( robot ) ); } );
        return robots;
    }


    /**
     * @return Find the robots on a specific planet
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
        RobotType robotType = robot.getType();
        AbstractRobotStrategy strategy = SCOUT.equals( robotType ) ? scoutStrategy :
                MINER.equals( robotType ) ? minerStrategy : warriorStrategy;
        return strategy;
    }
}

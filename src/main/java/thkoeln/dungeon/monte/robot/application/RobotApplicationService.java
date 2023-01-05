package thkoeln.dungeon.monte.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.strategy.AbstractStrategy;
import thkoeln.dungeon.monte.core.util.PlayerInformation;
import thkoeln.dungeon.monte.eventlistener.concreteevents.robot.RobotSpawnedEvent;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.robot.domain.*;
import thkoeln.dungeon.monte.trading.application.TradingAccountApplicationService;
import thkoeln.dungeon.monte.trading.domain.TradingAccount;

import java.util.ArrayList;
import java.util.List;

import static thkoeln.dungeon.monte.robot.domain.RobotType.*;

@Service
public class RobotApplicationService {
    private Logger logger = LoggerFactory.getLogger( RobotApplicationService.class );
    private RobotRepository robotRepository;
    private PlayerInformation playerInformation;
    private TradingAccountApplicationService tradingAccountApplicationService;

    @Autowired
    public RobotApplicationService( RobotRepository robotRepository,
                                    PlayerInformation playerInformation,
                                    TradingAccountApplicationService tradingAccountApplicationService ) {
        this.robotRepository = robotRepository;
        this.playerInformation = playerInformation;
        this.tradingAccountApplicationService = tradingAccountApplicationService;
    }
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


    /**
     * Add new robot as result of an RobotSpawnedEvent. The robot type is decided according to current quotas.
     * @param robotSpawnedEvent
     * @return the new robot
     */
    public Robot addNewRobotFromEvent( RobotSpawnedEvent robotSpawnedEvent, Planet planet ) {
        if ( robotSpawnedEvent == null || !robotSpawnedEvent.isValid() || planet == null )
            throw new RobotException( "robotSpawnedEvent == null || !robotSpawnedEvent.isValid() || planet == null" );
        logger.info( "About to add new robot for RobotSpawnedEvent ...");
        RobotType robotType = nextRobotTypeAccordingToQuota();
        Robot robot = Robot.of( robotSpawnedEvent.getRobotDto().getId(), robotType,
                                playerInformation.currentGameId(), playerInformation.currentPlayerId() );
        AbstractRobotStrategy strategy = (robotType == SCOUT) ?
                scoutStrategy : ( (robotType == MINER) ? minerStrategy : warriorStrategy );
        robot.setStrategy( strategy );
        robot.setLocatedOn( planet );
        robotRepository.save( robot );
        logger.info( "Added robot " + robot );
        return robot;
    }

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

    /**
     * @return all robots currently alive
     */
    public List<Robot> allLivingRobots() {
        return robotRepository.findAllByAliveEquals( true );
    }


    /**
     * @return Find the robots on a specific planet
     */
    public List<Robot> livingRobotsOnPlanet( Planet planet ) {
        if ( planet == null ) return null; // black hole
        List<Robot> robotsOnPlanet = robotRepository.findAllByLocatedOnIsAndAliveIsTrue( planet );
        return robotsOnPlanet;
    }


    public void decideAllRobotCommands() {
        List<Robot> robots = allLivingRobots();
        TradingAccount tradingAccount = tradingAccountApplicationService.queryAndIfNeededCreateTradingAccount();
        AbstractStrategy.findNextCommandsForGroup( robots, tradingAccount );
        for ( Robot robot : robots ) robotRepository.save( robot );
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
}

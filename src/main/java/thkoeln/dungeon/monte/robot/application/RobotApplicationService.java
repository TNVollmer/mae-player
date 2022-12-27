package thkoeln.dungeon.monte.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.eventlistener.concreteevents.robot.RobotSpawnedEvent;
import thkoeln.dungeon.monte.player.application.PlayerEventListener;
import thkoeln.dungeon.monte.robot.domain.Robot;
import thkoeln.dungeon.monte.robot.domain.RobotRepository;
import thkoeln.dungeon.monte.robot.domain.RobotType;

import java.util.List;

import static thkoeln.dungeon.monte.robot.domain.RobotType.MINER;
import static thkoeln.dungeon.monte.robot.domain.RobotType.WARRIOR;
import static thkoeln.dungeon.monte.robot.domain.RobotType.SCOUT;

@Service
public class RobotApplicationService {
    private Logger logger = LoggerFactory.getLogger( RobotApplicationService.class );
    private RobotRepository robotRepository;
    private Environment environment;

    @Autowired
    public RobotApplicationService( RobotRepository robotRepository,
                                    Environment environment ) {
        this.robotRepository = robotRepository;
        this.environment = environment;
    }

    /**
     * Add new robot as result of an RobotSpawnedEvent. The robot type is decided according to current quotas.
     * @param robotSpawnedEvent
     * @return the new robot
     */
    public Robot addNewRobotFromEvent( RobotSpawnedEvent robotSpawnedEvent ) {
        logger.info( "About to add new robot for RobotSpawnedEvent ...");
        if ( !robotSpawnedEvent.isValid() ) {
            logger.error( "Invalid RobotSpawnedEvent - will not process." + robotSpawnedEvent );
            return null;
        }
        Robot robot = Robot.of( robotSpawnedEvent.getRobot().getId() );
        robot.setType( nextRobotTypeAccordingToQuota() );
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

    public String printStatus() {
        String printString = "\n" + "====== All my robots ... =======\n";
        List<Robot> robots = allLivingRobots();
        for ( Robot robot : robots ) {
            printString += robot.toString() + "\n";
        }
        printString += "================================";
        return printString;
    }
}

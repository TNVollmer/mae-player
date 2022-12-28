package thkoeln.dungeon.monte.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.eventlistener.concreteevents.robot.RobotSpawnedEvent;
import thkoeln.dungeon.monte.planet.application.PlanetConsolePrintDto;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.robot.domain.Robot;
import thkoeln.dungeon.monte.robot.domain.RobotException;
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
    public Robot addNewRobotFromEvent( RobotSpawnedEvent robotSpawnedEvent, Planet planet ) {
        if ( robotSpawnedEvent == null || !robotSpawnedEvent.isValid() || planet == null )
            throw new RobotException( "robotSpawnedEvent == null || !robotSpawnedEvent.isValid() || planet == null" );
        logger.info( "About to add new robot for RobotSpawnedEvent ...");
        Robot robot = Robot.of( robotSpawnedEvent.getRobotDto().getId() );
        robot.setType( nextRobotTypeAccordingToQuota() );
        robot.setPlanet( planet );
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
     * @return Print all currently alive robots, in a compact format suitable for the console.
     */
    public String consolePrintStatus() {
        String printString = "\n" + "====== All my robots ... =======\n";
        List<Robot> robots = allLivingRobots();
        for ( Robot robot : robots ) {
            printString += robot.toString() + "\n";
        }
        printString += "================================";
        return printString;
    }


    /**
     * @return Print the robots on a specific planet, suitable for a map display (4 chars)
     */
    public String consolePrintRobotsForPlanetOnMap( Planet planet ) {
        if ( planet == null ) return null; // black hole
        List<Robot> robotsOnPlanet = robotRepository.findAllByPlanetIs( planet );
        if ( robotsOnPlanet.size() == 0 ) return PlanetConsolePrintDto.empty();
        if ( robotsOnPlanet.size() == 1 )
            return PlanetConsolePrintDto.cell( robotsOnPlanet.get( 0 ).toString() );
        return PlanetConsolePrintDto.multiple( robotsOnPlanet.size() );
    }
}

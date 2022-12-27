package thkoeln.dungeon.monte.robot.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.monte.robot.domain.Robot;
import thkoeln.dungeon.monte.robot.domain.RobotRepository;
import thkoeln.dungeon.monte.robot.domain.RobotType;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.dungeon.monte.robot.domain.RobotType.*;

@SpringBootTest
public class RobotApplicationServiceTest {
    Robot w1, m1, m2, s1, s2, s3, s4;
    @Autowired
    private RobotRepository robotRepository;
    @Autowired
    private RobotApplicationService robotApplicationService;

    @BeforeEach
    public void setUp() {
        robotRepository.deleteAll();
        w1 = Robot.of( UUID.randomUUID(), WARRIOR );
        m1 = Robot.of( UUID.randomUUID(), MINER );
        m2 = Robot.of( UUID.randomUUID(), MINER );
        s1 = Robot.of( UUID.randomUUID(), SCOUT );
        s2 = Robot.of( UUID.randomUUID(), SCOUT );
        s3 = Robot.of( UUID.randomUUID(), SCOUT );
        s3.setAlive( false );
        s4 = Robot.of( UUID.randomUUID(), SCOUT );
        robotRepository.save( w1 );
        robotRepository.save( m1 );
        robotRepository.save( m2 );
        robotRepository.save( s1 );
        robotRepository.save( s2 );
        robotRepository.save( s3 );
        robotRepository.save( s4 );
    }

    @Test
    public void testAllLivingRobots() {
        // given
        // when
        long numOfRobots = robotApplicationService.allLivingRobots().size();

        // then
        assertEquals( 6, numOfRobots );
    }

    @Test
    public void testQuota() {
        // given
        // Total = 6; warrior-% is 16 (should be 20), miner-% is 33 (should be 30)

        // when
        // Should buy miner.
        RobotType warrior1 = robotApplicationService.nextRobotTypeAccordingToQuota();
        Robot newR = Robot.of( UUID.randomUUID(), warrior1 );
        robotRepository.save( newR );
        // Afterwards: Total = 7; warrior-%  28, miner-% 28

        // Should buy miner.
        RobotType miner2 = robotApplicationService.nextRobotTypeAccordingToQuota();
        newR = Robot.of( UUID.randomUUID(), miner2 );
        robotRepository.save( newR );
        // Afterwards: Total = 8; Warrior-% 25, miner-% 37

        // Should buy scout.
        RobotType scout3 = robotApplicationService.nextRobotTypeAccordingToQuota();
        newR = Robot.of( UUID.randomUUID(), scout3 );
        robotRepository.save( newR );
        // Afterwards: Total = 9; Warrior-% 22, miner-% 33

        // Should buy scout.
        RobotType scout4 = robotApplicationService.nextRobotTypeAccordingToQuota();
        newR = Robot.of( UUID.randomUUID(), scout4 );
        robotRepository.save( newR );
        // Afterwards: Total = 10; Warrior-% 20, miner-% 30

        // Should buy scout.
        RobotType scout5 = robotApplicationService.nextRobotTypeAccordingToQuota();
        newR = Robot.of( UUID.randomUUID(), scout5 );
        robotRepository.save( newR );
        // Afterwards: Total = 11; Warrior-% 18, miner-% 27

        // Should buy warrior.
        RobotType warrior6 = robotApplicationService.nextRobotTypeAccordingToQuota();
        newR = Robot.of( UUID.randomUUID(), warrior6 );
        robotRepository.save( newR );

        // then
        assertEquals( WARRIOR, warrior1 );
        assertEquals( MINER, miner2 );
        assertEquals( SCOUT, scout3 );
        assertEquals( SCOUT, scout4 );
        assertEquals( SCOUT, scout5 );
        assertEquals( WARRIOR, warrior6 );
    }
}

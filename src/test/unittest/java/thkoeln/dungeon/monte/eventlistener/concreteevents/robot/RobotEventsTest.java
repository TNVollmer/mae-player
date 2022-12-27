package thkoeln.dungeon.monte.eventlistener.concreteevents.robot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.monte.eventlistener.AbstractConcreteEventTest;
import thkoeln.dungeon.monte.eventlistener.EventHeader;
import thkoeln.dungeon.monte.eventlistener.EventType;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RobotEventsTest extends AbstractConcreteEventTest {

    protected RobotSpawnedEvent validRobotSpawnedEvent() {
        RobotSpawnedEvent validRobotSpawnedEvent = new RobotSpawnedEvent();
        validRobotSpawnedEvent.setEventHeader( mockEventHeaderFor( EventType.ROBOT_SPAWNED ) );
        RobotDto robotDto = new RobotDto();
        RobotInventoryDto robotInventoryDto = new RobotInventoryDto();
        robotInventoryDto.setMaxStorage( 20 );
        robotDto.setInventory( robotInventoryDto );

        robotDto.setId( UUID.randomUUID() );
        robotDto.setHealth( 10 );
        robotDto.setEnergy( 20 );
        robotDto.setMiningSpeed( 2 );
        robotDto.setMaxHealth( 10 );
        robotDto.setMaxEnergy( 20 );
        robotDto.setEnergyRegen( 4 );
        robotDto.setAttackDamage( 1 );

        validRobotSpawnedEvent.setRobot( robotDto );
        return validRobotSpawnedEvent;
    }

    @Test
    public void testIsValidRobotSpawnedEvent() {
        // given
        RobotSpawnedEvent validRobotSpawnedEvent = validRobotSpawnedEvent();
        // when
        // then
        assertTrue( validRobotSpawnedEvent.isValid() );
    }

    @Test
    public void testInvalidRobotSpawnedEvent() {
        // given
        RobotSpawnedEvent[] events = new RobotSpawnedEvent[20];
        for ( int i = 0; i < events.length; i++ ) {
            events[i] = validRobotSpawnedEvent();
        }

        // when
        events[0].setRobot( null );
        events[1].setEventHeader( null );
        events[2].getRobot().setInventory( null );
        events[3].getRobot().getInventory().setMaxStorage( null );
        events[4].getRobot().getInventory().setMaxStorage( 0 );
        events[5].getRobot().getInventory().setMaxStorage( -1 );
        events[6].getRobot().setHealth( null );
        events[7].getRobot().setHealth( 0 );
        events[8].getRobot().setEnergy( null );
        events[9].getRobot().setEnergy( 0 );
        events[10].getRobot().setMiningSpeed( null );
        events[11].getRobot().setMiningSpeed( 0 );
        events[12].getRobot().setMaxHealth( null );
        events[13].getRobot().setMaxHealth( 0 );
        events[14].getRobot().setMaxEnergy( null );
        events[15].getRobot().setMaxEnergy( 0 );
        events[16].getRobot().setEnergyRegen( null );
        events[17].getRobot().setEnergyRegen( 0 );
        events[18].getRobot().setAttackDamage( null );
        events[19].getRobot().setAttackDamage( 0 );

        // then
        for ( int i = 0; i < events.length; i++ ) {
            assertFalse( events[i].isValid(), "Test " + i + " failed" );
        }
    }

}

package thkoeln.dungeon.player.core.events.concreteevents.robot;

import thkoeln.dungeon.player.core.events.AbstractConcreteEventTest;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.player.core.events.EventType;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotInventoryDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotPlanetDto;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotSpawnedEvent;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RobotSpawnedEventTest extends AbstractConcreteEventTest {

    protected RobotSpawnedEvent validRobotSpawnedEvent() {
        RobotSpawnedEvent validRobotSpawnedEvent = new RobotSpawnedEvent();
        validRobotSpawnedEvent.setEventHeader( mockEventHeaderFor( EventType.ROBOT_SPAWNED ) );
        RobotDto robotDto = new RobotDto();
        RobotInventoryDto robotInventoryDto = new RobotInventoryDto();
        robotInventoryDto.setMaxStorage( 20 );
        robotDto.setInventory( robotInventoryDto );

        RobotPlanetDto robotPlanetDto = new RobotPlanetDto();
        robotPlanetDto.setPlanetId( UUID.randomUUID() );
        robotPlanetDto.setMovementDifficulty( 2 );
        robotDto.setPlanet( robotPlanetDto );

        robotDto.setId( UUID.randomUUID() );
        robotDto.setHealth( 10 );
        robotDto.setEnergy( 20 );
        robotDto.setMiningSpeed( 2 );
        robotDto.setMaxHealth( 10 );
        robotDto.setMaxEnergy( 20 );
        robotDto.setEnergyRegen( 4 );
        robotDto.setAttackDamage( 1 );

        validRobotSpawnedEvent.setRobotDto( robotDto );
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
        RobotSpawnedEvent[] events = new RobotSpawnedEvent[23];
        for ( int i = 0; i < events.length; i++ ) {
            events[i] = validRobotSpawnedEvent();
        }

        // when
        events[0].setRobotDto( null );
        events[1].setEventHeader( null );
        events[2].getRobotDto().setInventory( null );
        events[3].getRobotDto().getInventory().setMaxStorage( null );
        events[4].getRobotDto().getInventory().setMaxStorage( 0 );
        events[5].getRobotDto().getInventory().setMaxStorage( -1 );
        events[6].getRobotDto().setHealth( null );
        events[7].getRobotDto().setHealth( 0 );
        events[8].getRobotDto().setEnergy( null );
        events[9].getRobotDto().setEnergy( 0 );
        events[10].getRobotDto().setMiningSpeed( null );
        events[11].getRobotDto().setMiningSpeed( 0 );
        events[12].getRobotDto().setMaxHealth( null );
        events[13].getRobotDto().setMaxHealth( 0 );
        events[14].getRobotDto().setMaxEnergy( null );
        events[15].getRobotDto().setMaxEnergy( 0 );
        events[16].getRobotDto().setEnergyRegen( null );
        events[17].getRobotDto().setEnergyRegen( 0 );
        events[18].getRobotDto().setAttackDamage( null );
        events[19].getRobotDto().setAttackDamage( 0 );
        events[20].getRobotDto().getPlanet().setPlanetId( null );
        events[21].getRobotDto().getPlanet().setMovementDifficulty( -1 );
        events[22].getRobotDto().getPlanet().setMovementDifficulty( null );


        // then
        for ( int i = 0; i < events.length; i++ ) {
            assertFalse( events[i].isValid(), "Test " + i + " failed" );
        }
    }

}

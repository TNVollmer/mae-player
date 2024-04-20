package thkoeln.dungeon.player.unittest.core.events.concreteevents.planet;

import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetNeighboursDto;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetResourceDto;
import thkoeln.dungeon.player.unittest.core.events.AbstractConcreteEventTest;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.player.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.core.events.EventType;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlanetDiscoveredEventTest extends AbstractConcreteEventTest {

    protected PlanetDiscoveredEvent validPlanetDiscoveredEvent() {
        PlanetDiscoveredEvent validPlanetDiscoveredEvent = new PlanetDiscoveredEvent();
        validPlanetDiscoveredEvent.setEventHeader( mockEventHeaderFor( EventType.PLANET_DISCOVERED ) );
        validPlanetDiscoveredEvent.setPlanetId( UUID.randomUUID() );
        validPlanetDiscoveredEvent.setMovementDifficulty( 2 );

        PlanetNeighboursDto east = new PlanetNeighboursDto();
        east.setDirection( CompassDirection.EAST );
        east.setId( UUID.randomUUID() );
        PlanetNeighboursDto north = new PlanetNeighboursDto();
        north.setDirection( CompassDirection.NORTH );
        north.setId( UUID.randomUUID() );
        PlanetNeighboursDto[] neighboursDtos = new PlanetNeighboursDto[] { north, east };
        validPlanetDiscoveredEvent.setNeighbours(neighboursDtos );

        PlanetResourceDto planetResourceDto = new PlanetResourceDto();
        planetResourceDto.setResourceType( MineableResourceType.COAL );
        planetResourceDto.setMaxAmount( 2000 );
        planetResourceDto.setCurrentAmount( 2000 );
        validPlanetDiscoveredEvent.setResource( planetResourceDto );

        return validPlanetDiscoveredEvent;
    }

    @Test
    public void testIsValidRobotSpawnedEvent() {
        // given
        PlanetDiscoveredEvent validPlanetDiscoveredEvent = validPlanetDiscoveredEvent();
        // when
        // then
        assertTrue( validPlanetDiscoveredEvent.isValid() );
    }

    @Test
    public void testInvalidRobotSpawnedEvent() {
        // given
        PlanetDiscoveredEvent[] events = new PlanetDiscoveredEvent[13];
        for ( int i = 0; i < events.length; i++ ) {
            events[i] = validPlanetDiscoveredEvent();
        }

        // when
        events[0].setPlanetId( null );
        events[1].setEventHeader( null );
        events[2].setNeighbours( null );
        events[3].getNeighbours()[0].setId( null );
        events[4].getNeighbours()[1].setDirection( null );
        events[5].getResource().setResourceType( null );
        events[6].getResource().setCurrentAmount( null );
        events[7].getResource().setMaxAmount( null );
        events[8].getResource().setCurrentAmount( -1 );
        events[9].getResource().setMaxAmount( -1 );
        events[10].getResource().setCurrentAmount( events[10].getResource().getMaxAmount() + 1 );
        events[11].setMovementDifficulty( null );
        events[12].setMovementDifficulty( -1 );

        // then
        for ( int i = 0; i < events.length; i++ ) {
            assertFalse( events[i].isValid(), "Test " + i + " failed" );
        }
    }

}

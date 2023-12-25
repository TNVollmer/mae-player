package thkoeln.dungeon.player.core.events;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EventStatusTest {

    @Test
    public void testUnknownEvent() {
        // given
        // when
        EventType eventType = EventType.findByStringValue( "xxx-status" );

        // then
        assertEquals( EventType.UNKNOWN, eventType );
    }

    @Test
    public void testEventTypeClassification() {
        assertFalse( EventType.GAME_STATUS.isRobotRelated() );
        assertFalse( EventType.GAME_STATUS.isPlanetRelated() );

        assertFalse( EventType.BANK_INITIALIZED.isRobotRelated() );
        assertFalse( EventType.BANK_INITIALIZED.isPlanetRelated() );

        assertFalse( EventType.ROUND_STATUS.isRobotRelated() );
        assertFalse( EventType.ROUND_STATUS.isPlanetRelated() );

        assertFalse( EventType.TRADABLE_PRICES.isRobotRelated() );
        assertFalse( EventType.TRADABLE_PRICES.isPlanetRelated() );

        assertTrue( EventType.ROBOT_SPAWNED.isRobotRelated() );
        assertFalse( EventType.ROBOT_SPAWNED.isPlanetRelated() );

        assertFalse( EventType.PLANET_DISCOVERED.isRobotRelated() );
        assertTrue( EventType.PLANET_DISCOVERED.isPlanetRelated() );
    }
}

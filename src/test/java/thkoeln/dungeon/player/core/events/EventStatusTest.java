package thkoeln.dungeon.player.core.events;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.dungeon.player.core.events.EventType.*;

public class EventStatusTest {

    @Test
    public void testUnknownEvent() {
        // given
        // when
        EventType eventType = EventType.findByStringValue( "xxx-status" );

        // then
        assertEquals( UNKNOWN, eventType );
    }

    @Test
    public void testEventTypeClassification() {
        assertFalse( GAME_STATUS.isRobotRelated() );
        assertFalse( GAME_STATUS.isPlanetRelated() );

        assertFalse( BANK_INITIALIZED.isRobotRelated() );
        assertFalse( BANK_INITIALIZED.isPlanetRelated() );

        assertFalse( ROUND_STATUS.isRobotRelated() );
        assertFalse( ROUND_STATUS.isPlanetRelated() );

        assertFalse( TRADABLE_PRICES.isRobotRelated() );
        assertFalse( TRADABLE_PRICES.isPlanetRelated() );

        assertTrue( ROBOT_SPAWNED.isRobotRelated() );
        assertFalse( ROBOT_SPAWNED.isPlanetRelated() );

        assertFalse( PLANET_DISCOVERED.isRobotRelated() );
        assertTrue( PLANET_DISCOVERED.isPlanetRelated() );
    }
}

package thkoeln.dungeon.player.unittest.core.events;

import org.junit.jupiter.api.Test;
import thkoeln.dungeon.player.core.events.EventType;

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

}

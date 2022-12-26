package thkoeln.dungeon.monte.eventlistener;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static thkoeln.dungeon.monte.eventlistener.EventType.*;

public class EventStatusTest {

    @Test
    public void testGameStatus() {
        // given
        // when
        EventType eventType1 = EventType.findByStringValue( "game-status" );
        EventType eventType2 = EventType.findByStringValue( "xxx-status" );

        // then
        assertEquals( GAME_STATUS, eventType1 );
        assertEquals( UNKNOWN, eventType2 );
    }
}

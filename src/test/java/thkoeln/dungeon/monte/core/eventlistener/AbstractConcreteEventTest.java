package thkoeln.dungeon.monte.core.eventlistener;

import thkoeln.dungeon.monte.core.eventlistener.EventHeader;
import thkoeln.dungeon.monte.core.eventlistener.EventType;

import java.sql.Timestamp;
import java.util.UUID;

public abstract class AbstractConcreteEventTest {

    protected EventHeader mockEventHeaderFor(EventType eventType ) {
        EventHeader eventHeader = new EventHeader (
                eventType.toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), new Timestamp(System.currentTimeMillis()).toString(), "1" );
        return eventHeader;
    }
}

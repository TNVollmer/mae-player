package thkoeln.dungeon.monte.eventlistener;

import java.sql.Timestamp;
import java.util.UUID;

public abstract class AbstractConcreteEventTest {

    protected EventHeader mockEventHeaderFor( EventType eventType ) {
        EventHeader eventHeader = new EventHeader (
                eventType.toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), new Timestamp(System.currentTimeMillis()).toString(), "1" );
        return eventHeader;
    }
}

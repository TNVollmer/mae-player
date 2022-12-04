package thkoeln.dungeon.game.application.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.eventlistener.AbstractEvent;
import thkoeln.dungeon.eventlistener.AbstractEventCallback;


public class GameEventsCallback extends AbstractEventCallback {
    private Logger logger = LoggerFactory.getLogger( GameEventsCallback.class );

    @Override
    public void executeSpecificActionForEvent( AbstractEvent event ) {
        logger.info( "GameEventsCallback - got some event: " + event );
    }
}

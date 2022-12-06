package thkoeln.dungeon.game.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.eventlistener.AbstractEvent;
import thkoeln.dungeon.eventlistener.__OBSOLETE_AbstractEventCallback;


public class __OBSOLETE_GameEventsCallback extends __OBSOLETE_AbstractEventCallback {
    private Logger logger = LoggerFactory.getLogger( __OBSOLETE_GameEventsCallback.class );

    @Override
    public void executeSpecificActionForEvent( AbstractEvent event ) {
        logger.info( "__OBSOLETE_GameEventsCallback - got some event: " + event );
    }
}

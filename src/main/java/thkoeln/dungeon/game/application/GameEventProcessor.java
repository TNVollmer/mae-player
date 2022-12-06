package thkoeln.dungeon.game.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventlistener.EventHeader;

@Service
public class GameEventProcessor {
    private Logger logger = LoggerFactory.getLogger(GameEventProcessor.class);
    /**
     * Handle a game-related event, and dispatch to the appropriate application service method
     * @param header
     * @param payload
     */
    public void handleGameRelatedEvent( EventHeader header, String payload ) {
        // todo create dedicated event, store it, and call functionality
        logger.info("Handle game related event " + header);
    }


}

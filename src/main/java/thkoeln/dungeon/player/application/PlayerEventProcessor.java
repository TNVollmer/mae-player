package thkoeln.dungeon.player.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventlistener.AbstractEvent;
import thkoeln.dungeon.eventlistener.DungeonEventException;
import thkoeln.dungeon.eventlistener.EventFactory;
import thkoeln.dungeon.eventlistener.gameevents.GameStatusEvent;
import thkoeln.dungeon.eventlistener.EventHeader;
import thkoeln.dungeon.game.application.GameApplicationService;
import static thkoeln.dungeon.game.domain.GameStatus.*;

@Service
public class PlayerEventProcessor {
    private Logger logger = LoggerFactory.getLogger(PlayerEventProcessor.class);
    private EventFactory eventFactory;
    private GameApplicationService gameApplicationService;
    private PlayerApplicationService playerApplicationService;

    @Autowired
    public PlayerEventProcessor( EventFactory eventFactory,
                                 GameApplicationService gameApplicationService,
                                 PlayerApplicationService playerApplicationService ) {
        this.eventFactory = eventFactory;
        this.gameApplicationService = gameApplicationService;
        this.playerApplicationService = playerApplicationService;
    }

    /**
     * Handle a player-related event, and dispatch to the appropriate application service method
     * @param header
     * @param payload
     */
    public void handlePlayerRelatedEvent( EventHeader header, String payload ) {
        if ( header == null || payload == null ) throw new DungeonEventException( "header == null || payload == null" );
        logger.info( "Handle player related event " + header );
        AbstractEvent newEvent = eventFactory.fromHeaderAndPayload( header, payload );
        if ( !newEvent.isValid() ) {
            logger.error("Event invalid: " + newEvent);
            return;
        }
        // todo - rethink design, this will become a very long method ...
        switch ( newEvent.getEventHeader().getEventType() ) {
            case GAME_STATUS:
                GameStatusEvent gameStatusEvent = (GameStatusEvent) newEvent;
                if ( CREATED.equals( gameStatusEvent.getStatus() ) ) {
                    playerApplicationService.letPlayerJoinOpenGame();
                }
                else if ( RUNNING.equals( gameStatusEvent.getStatus() ) ) {
                    gameApplicationService.gameExternallyStarted( gameStatusEvent.getGameId() );
                }
                else if ( FINISHED.equals( gameStatusEvent.getStatus() ) ) {
                    gameApplicationService.gameExternallyFinished( gameStatusEvent.getGameId() );
                }
        }
    }

}

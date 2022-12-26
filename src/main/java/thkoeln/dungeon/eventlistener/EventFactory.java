package thkoeln.dungeon.eventlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventlistener.concreteevents.*;
import thkoeln.dungeon.eventlistener.concreteevents.robot.RobotSpawnedEvent;

@Service
public class EventFactory {
    private Logger logger = LoggerFactory.getLogger( EventFactory.class );

    public AbstractEvent fromHeaderAndPayload( EventHeader eventHeader, String payload ) {
        if ( eventHeader == null || payload == null )
            throw new DungeonEventException( "eventHeader == null || payload == null" );
        AbstractEvent newEvent = null;
        switch ( eventHeader.getEventType() ) {
            case GAME_STATUS:
                newEvent = new GameStatusEvent();
                break;
            case BANK_INITIALIZED:
                newEvent = new BankInitializedEvent();
                break;
            case ROUND_STATUS:
                newEvent = new RoundStatusEvent();
                break;
            case TRADABLE_PRICES:
                newEvent = new TradeablePricesEvent();
                break;
            case ROBOT_SPAWNED:
                newEvent = new RobotSpawnedEvent();
                break;
            // todo add other event types here
            default:
                newEvent = new UnknownEvent();
        }
        newEvent.setEventHeader( eventHeader );
        newEvent.fillWithPayload( payload );
        logger.info( "Created event: " + newEvent );
        return newEvent;
    }

}

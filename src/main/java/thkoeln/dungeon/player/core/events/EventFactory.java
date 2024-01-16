package thkoeln.dungeon.player.core.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.events.concreteevents.ErrorEvent;
import thkoeln.dungeon.player.core.events.concreteevents.UnknownEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.GameStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.player.core.events.concreteevents.planet.ResourceMinedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.change.RobotRegeneratedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.change.RobotRestoredAttributesEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.change.RobotUpgradedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.fight.RobotAttackedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.mine.RobotResourceMinedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.mine.RobotResourceRemovedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.move.RobotMovedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.reveal.RobotsRevealedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.trading.*;

@Service
public class EventFactory {
    private final Logger logger = LoggerFactory.getLogger( EventFactory.class );

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
            case BANK_ACCOUNT_TRANSACTION_BOOKED:
                newEvent = new BankAccountTransactionBookedEvent();
                break;
            case ROUND_STATUS:
                newEvent = new RoundStatusEvent();
                break;
            case TRADABLE_PRICES:
                newEvent = new TradablePricesEvent();
                break;
            case ROBOT_SPAWNED:
                newEvent = new RobotSpawnedEvent();
                break;
            case ROBOT_MOVED:
                newEvent = new RobotMovedEvent();
                break;
            case ROBOT_REGENERATED:
                newEvent = new RobotRegeneratedEvent();
                break;
            case ROBOT_REVEALED:
                newEvent = new RobotsRevealedEvent();
                break;
            case ROBOT_RESOURCE_MINED:
                newEvent = new RobotResourceMinedEvent();
                break;
            case PLANET_DISCOVERED:
                newEvent = new PlanetDiscoveredEvent();
                break;
            case TRADABLE_BOUGHT:
                newEvent = new TradableBoughtEvent();
                break;
            case TRADABLE_SOLD:
                newEvent = new TradableSoldEvent();
                break;
            case BANK_ACCOUNT_CLEARED:
                newEvent = new BankAccountClearedEvent();
                break;
            case RESOURCE_MINED:
                newEvent = new ResourceMinedEvent();
                break;
            case ROBOT_RESOURCE_REMOVED:
                newEvent = new RobotResourceRemovedEvent();
                break;
            case ROBOT_UPGRADED:
                newEvent = new RobotUpgradedEvent();
                break;
            case ROBOT_ATTACKED:
                newEvent = new RobotAttackedEvent();
                break;
            case ROBOT_RESTORED_ATTRIBUTES:
                newEvent = new RobotRestoredAttributesEvent();
                break;
            case ERROR:
                newEvent = new ErrorEvent();
                break;
            default:
                newEvent = new UnknownEvent();
        }
        newEvent.setEventHeader( eventHeader );
        newEvent.fillWithPayload( payload );
        logger.debug( "Created event: " + newEvent );
        return newEvent;
    }

}

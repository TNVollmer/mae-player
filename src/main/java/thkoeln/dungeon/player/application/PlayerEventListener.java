package thkoeln.dungeon.player.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventlistener.EventHeader;
import thkoeln.dungeon.game.application.GameEventProcessor;

import static thkoeln.dungeon.eventlistener.EventHeader.*;

@Service
public class PlayerEventListener {
    private Logger logger = LoggerFactory.getLogger(PlayerEventListener.class);
    private Environment environment;
    private GameEventProcessor gameEventProcessor;
    private PlayerEventProcessor playerEventProcessor;

    // todo replace by a dynamic mechanism
    static final String queueName = "player-fe529bc7-1bae-4017-8691-ccc6a744ff05";

    @Autowired
    public PlayerEventListener( Environment environment,
                                GameEventProcessor gameEventProcessor,
                                PlayerEventProcessor playerEventProcessor ) {
        this.gameEventProcessor = gameEventProcessor;
        this.playerEventProcessor = playerEventProcessor;
        this.environment = environment;
    }

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    /**
     * Listener to all events that the core services send to the player
     * TODO - this queue must be set dynamically, as a reaction to the player joining the game
     * It just fits in my personal dev env, just for the moment ... ;-(
     * @param eventIdStr
     * @param transactionIdStr
     * @param playerIdStr
     * @param type
     * @param version
     * @param timestampStr
     * @param payload
     */
    @RabbitListener( id = "player-queue" /* queues = "player-fe529bc7-1bae-4017-8691-ccc6a744ff05" */ )
    public void receiveEvent( @Header( EVENT_ID_KEY ) String eventIdStr,
                              @Header( TRANSACTION_ID_KEY ) String transactionIdStr,
                              @Header( PLAYER_ID_KEY ) String playerIdStr,
                              @Header( TYPE_KEY ) String type,
                              @Header( VERSION_KEY ) String version,
                              @Header( TIMESTAMP_KEY ) String timestampStr,
                              String payload ) {
        // todo make this a toString in the event
        logger.info( environment.getProperty( "ANSI_BLUE" ) + "====> received event ... \n\t" +
                " {type=" + type +
                ", eventId=" + eventIdStr +
                ", transactionId=" + transactionIdStr +
                ", playerId=" + playerIdStr +
                ", version=" + version +
                ", timestamp=" + timestampStr +
                "\n\t" + payload + environment.getProperty( "ANSI_RESET" ) );

        EventHeader eventHeader =
                new EventHeader( type, eventIdStr, playerIdStr, transactionIdStr, timestampStr, version );
        if ( eventHeader.getEventType().isGameRelated() ) {
            gameEventProcessor.handleGameRelatedEvent( eventHeader, payload );
        }
        else if ( eventHeader.getEventType().isPlayerRelated() ) {
            playerEventProcessor.handlePlayerRelatedEvent( eventHeader, payload );
        }
    }

}

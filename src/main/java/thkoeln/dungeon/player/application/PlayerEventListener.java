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
import static thkoeln.dungeon.eventlistener.AbstractEvent.*;

@Service
public class PlayerEventListener {
    private Logger logger = LoggerFactory.getLogger(PlayerEventListener.class);
    private Environment environment;

    // todo replace by a dynamic mechanism
    static final String queueName = "player-fe529bc7-1bae-4017-8691-ccc6a744ff05";

    @Autowired
    public PlayerEventListener( Environment environment ) {
        this.environment = environment;
    }

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @RabbitListener( queues = "player-fe529bc7-1bae-4017-8691-ccc6a744ff05" )
    public void receiveEvent( @Header( EVENT_ID_KEY ) String eventIdStr,
                              @Header( TRANSACTION_ID_KEY ) String transactionIdStr,
                              @Header( PLAYER_ID_KEY ) String playerIdStr,
                              @Header( TYPE_KEY ) String type,
                              @Header( VERSION_KEY ) String version,
                              @Header( TIMESTAMP_KEY ) String timestampStr,
                              String message ) {


        logger.info( environment.getProperty( "ANSI_GREEN" ) + "---------------- " +
                " {type=" + type +
                ", eventId=" + eventIdStr +
                ", transactionId=" + transactionIdStr +
                ", playerId=" + playerIdStr +
                ", version=" + version +
                ", timestamp='" + timestampStr +
                "\n\t" + message + environment.getProperty( "ANSI_RESET" ) );
    }

}

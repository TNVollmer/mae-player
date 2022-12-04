package thkoeln.dungeon.eventlistener;

import com.rabbitmq.client.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Listener for a rabbitmq queue
 */
@NoArgsConstructor
@Service
public class RabbitMQListener {
    private Logger logger = LoggerFactory.getLogger(RabbitMQListener.class);
    private EventCallback eventCallback;
    private String playerQueue;

    public RabbitMQListener( String playerQueue, EventCallback eventCallback ) {
        this.playerQueue = playerQueue;
        this.eventCallback = eventCallback;
    }


    /**
     * Configure and start up the player queue listening to various events, streaming in via the RabbitMQ
     * queue that has been set up specifically for this player.
     */
    public void startupListener( String playerQueue, EventCallback eventCallback ) {
        // todo: I need some kind of "master callback" where the dedicated handlers can plug into
        this.playerQueue = playerQueue;
        this.eventCallback = eventCallback;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername( "admin" );
        factory.setPassword( "admin" );
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            CancelCallback cancelCallback = consumerTag -> {};
            Consumer consumer = new DefaultConsumer( channel );
            channel.basicConsume( playerQueue, true, eventCallback, cancelCallback );
        }
        catch ( IOException ioException ) {
            logger.error( "Exception while starting up RabbitMQ channel " + playerQueue + ": " + ioException );
        }
        catch ( TimeoutException timeoutException ) {
            logger.error( "Exception while starting up RabbitMQ channel " + playerQueue + ": " + timeoutException );
        }
    }
}

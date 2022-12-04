package thkoeln.dungeon.eventlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import thkoeln.dungeon.player.application.PlayerApplicationService;

@SpringBootApplication
public class RabbitMQListener {
    private Logger logger = LoggerFactory.getLogger(RabbitMQListener.class);
    static final String queueName = "player-fe529bc7-1bae-4017-8691-ccc6a744ff05";

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername( "admin" );
        connectionFactory.setPassword( "admin" );
        return connectionFactory;
    }

    @Bean
    SimpleMessageListenerContainer container( ConnectionFactory connectionFactory,
                                              MessageListenerAdapter listenerAdapter ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory( connectionFactory );
        container.setQueueNames( queueName );
        container.setMessageListener( listenerAdapter );
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter( XXXTestReceiver receiver ) {
        return new MessageListenerAdapter( receiver, "receiveMessage" );
    }


}
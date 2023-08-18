package thkoeln.dungeon.monte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAutoConfiguration
@EntityScan("thkoeln.dungeon.*")
@ComponentScan("thkoeln.dungeon.*")
public class DungeonPlayerConfiguration {
    private Logger logger = LoggerFactory.getLogger(DungeonPlayerConfiguration.class);
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    @Autowired
    private Environment environment;


    /**
     * Needed for configuration of the RabbitMQ connection
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        String username = environment.getProperty( "dungeon.queue.username" );
        String password = environment.getProperty( "dungeon.queue.password" );
        String host = environment.getProperty( "dungeon.queue.host" );
        int port = Integer.valueOf( environment.getProperty( "dungeon.queue.port" ) );
        logger.debug( "Property dungeon.queue.host found: " + host + ":" + port );
        connectionFactory.setUsername( username );
        connectionFactory.setPassword( password );
        connectionFactory.setHost( host );
        connectionFactory.setPort( port );
        logger.debug( "Prepared RabbitMQ factory for host " + connectionFactory.getVirtualHost() );
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * todo needed?
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {
        return restTemplateBuilder.build();
    }





}

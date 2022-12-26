package thkoeln.dungeon.monte;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
        String username = environment.getProperty( "queue.username" );
        String password = environment.getProperty( "queue.password" );
        connectionFactory.setUsername( username );
        connectionFactory.setPassword( password );
        return connectionFactory;
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

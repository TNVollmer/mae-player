package thkoeln.dungeon.player;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DungeonPlayerConfiguration {
    private final RestTemplateBuilder restTemplateBuilder;

    /**
     * todo needed?
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {
        return restTemplateBuilder.build();
    }





}

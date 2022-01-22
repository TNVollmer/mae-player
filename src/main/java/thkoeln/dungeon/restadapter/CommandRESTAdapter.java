package thkoeln.dungeon.restadapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Adapter for sending commands to the GameService
 */
@Component
public class CommandRESTAdapter {
    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger( GameServiceRESTAdapter.class );
    @Value("${GAME_SERVICE:http://localhost:8080}")
    private String gameServiceUrlString;

    @Autowired
    public CommandRESTAdapter( RestTemplate restTemplate ) {
        this.restTemplate = restTemplate;
    }

}

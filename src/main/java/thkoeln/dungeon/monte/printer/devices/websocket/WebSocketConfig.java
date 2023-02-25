package thkoeln.dungeon.monte.printer.devices.websocket;

/**
 * Adapted from Baeldung, https://www.baeldung.com/websockets-spring and
 * https://www.baeldung.com/spring-boot-scheduled-websocket. The Baeldung code is at
 * https://github.com/eugenp/tutorials/tree/master/spring-websockets.
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        config.enableSimpleBroker( "/topic" );
    }

    @Override
    public void registerStompEndpoints( final StompEndpointRegistry registry ) {
        registry.addEndpoint( "/playerstatus" );
        registry.addEndpoint( "/playerstatus" ).withSockJS();
    }

}
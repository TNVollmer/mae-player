package thkoeln.dungeon.player.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationTestConfig {
    @Bean
    public MockSetupBeanPostProcessor mockSetupBeanPostProcessor() {
        return new MockSetupBeanPostProcessor();
    }
}

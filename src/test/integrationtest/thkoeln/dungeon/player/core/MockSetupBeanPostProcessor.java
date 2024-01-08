package thkoeln.dungeon.player.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import thkoeln.dungeon.player.DungeonPlayerInitializer;

public class MockSetupBeanPostProcessor implements BeanPostProcessor {
    @Autowired
    private IntegrationTestMocking integrationTestMocking;

    @Override
    public Object postProcessBeforeInitialization( Object bean, String beanName ) throws BeansException {
        if ( bean instanceof DungeonPlayerInitializer ) {
            // Setup your mocks here
            System.out.println( "MockSetupBeanPostProcessor: DungeonPlayerInitializer" );
            integrationTestMocking.setUpMocking();
        }
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization( Object bean, String beanName ) throws BeansException {
        return bean;
    }
}

package thkoeln.dungeon.monte.player.domain;

import org.springframework.stereotype.Component;
import thkoeln.dungeon.monte.core.strategy.AbstractStrategy;

@Component
public class PlayerStrategy extends AbstractStrategy {
    @Override
    public String[] commandCreatorMethodNames() {
        return new String[]{ "buyRobots" };
    }
}

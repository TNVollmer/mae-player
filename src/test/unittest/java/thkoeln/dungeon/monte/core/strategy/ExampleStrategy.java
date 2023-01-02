package thkoeln.dungeon.monte.core.strategy;

import thkoeln.dungeon.monte.core.strategy.AbstractStrategy;

public class ExampleStrategy extends AbstractStrategy {
    protected String[] commandCreatorMethodNames = new String[] {
            "getWellSoon",
            "buySomething",
            "move"
    };

    @Override
    public String[] commandCreatorMethodNames() {
        return commandCreatorMethodNames;
    }
}

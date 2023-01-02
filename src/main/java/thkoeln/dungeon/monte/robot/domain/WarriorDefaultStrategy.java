package thkoeln.dungeon.monte.robot.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarriorDefaultStrategy extends AbstractRobotStrategy {
    private Logger logger = LoggerFactory.getLogger( WarriorDefaultStrategy.class );

    protected String[] commandCreatorMethodNames = new String[] {
        "regenerateIfLowAndNotAttacked",
        "attack",
        "upgrade",
        "move",
        "regenerate"
    };

    @Override
    public String[] commandCreatorMethodNames() {
        return commandCreatorMethodNames;
    }
}

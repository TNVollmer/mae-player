package thkoeln.dungeon.monte.robot.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("warriorStrategy")
public class WarriorStrategy extends AbstractRobotStrategy {
    private Logger logger = LoggerFactory.getLogger( WarriorStrategy.class );

    protected String[] commandCreatorMethodNames = new String[] {
        "regenerateIfLowAndNotAttacked",
        "moveIfOpponentNearby",
        "attack",
        "upgrade",
        "moveRandomly",
        "regenerate"
    };

    @Override
    public String[] commandCreatorMethodNames() {
        return commandCreatorMethodNames;
    }
}

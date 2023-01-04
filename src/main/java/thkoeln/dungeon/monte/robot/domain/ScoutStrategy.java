package thkoeln.dungeon.monte.robot.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("scoutStrategy")
public class ScoutStrategy extends AbstractRobotStrategy {
    private Logger logger = LoggerFactory.getLogger( ScoutStrategy.class );

    protected String[] commandCreatorMethodNames = new String[] {
        "regenerateIfLowAndNotAttacked",
        "fleeIfAttacked",
        "mineIfNotMinedLastRound",
        "createMove",
        "regenerate"
    };

    @Override
    public String[] commandCreatorMethodNames() {
        return commandCreatorMethodNames;
    }
}

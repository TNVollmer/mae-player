package thkoeln.dungeon.monte.robot.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("minerStrategy")
public class MinerStrategy extends AbstractRobotStrategy {
    private Logger logger = LoggerFactory.getLogger( MinerStrategy.class );

    protected String[] commandCreatorMethodNames = new String[] {
        "regenerateIfLowAndNotAttacked",
        "fleeIfAttacked",
        "upgrade",
        "moveIfNotOnFittingResource",
        "moveIfOptimalResourceNearby",
        "mine",
        "regenerate"
    };

    @Override
    public String[] commandCreatorMethodNames() {
        return commandCreatorMethodNames;
    }
}

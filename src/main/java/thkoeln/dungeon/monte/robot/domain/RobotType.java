package thkoeln.dungeon.monte.robot.domain;

import thkoeln.dungeon.monte.domainprimitives.CapabilityType;

import java.util.Map;

import static thkoeln.dungeon.monte.domainprimitives.CapabilityType.*;

/**
 * This class is for modelling different robot types. I decided not to use inheritence from an abstract Robot
 * super class, since this would make queries complex. Instead, each robot carries this as a type attribute.
 */
public enum RobotType {
    SCOUT( "Scout" ),
    MINER( "Miner" ),
    WARRIOR( "Warrior" );

    private final String stringValue;

    private RobotType(String s ) {
        stringValue = s;
    }

    /**
     * The target levels for upgrading robots
     */
    protected static final Map<RobotType, Map<CapabilityType, Integer>> TARGET_LEVELS = Map.of(
        SCOUT, Map.of(),
        MINER, Map.of( MINING, 2, STORAGE, 2 ),
        WARRIOR, Map.of( DAMAGE, 2, HEALTH, 2 ) );

    /**
     * Tactical instructions for the robots, in priority order
     */
    protected static final Map<RobotType, String[]> INSTRUCTIONS = Map.of(
        SCOUT, new String[] {
            "regenerateIfLowAndNotAttacked",
            "fleeIfAttacked",
            "mineIfNotMinedLastRound",
            "move"
        },
        MINER, new String[] {
            "regenerateIfLowAndNotAttacked",
            "fleeIfAttacked",
            "mine",
            "upgrade",
            "move"
        },
        WARRIOR, new String[] {
            "regenerateIfLowAndNotAttacked",
            "attack",
            "upgrade",
            "move"
        });


    public Map<CapabilityType, Integer> targetLevels() {
        return TARGET_LEVELS.get( this );
    }

    public String[] instructions() {
        return INSTRUCTIONS.get( this );
    }
}

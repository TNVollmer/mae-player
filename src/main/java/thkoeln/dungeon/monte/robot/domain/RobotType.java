package thkoeln.dungeon.monte.robot.domain;

import thkoeln.dungeon.monte.core.domainprimitives.purchasing.CapabilityType;

import java.util.Map;

import static thkoeln.dungeon.monte.core.domainprimitives.purchasing.CapabilityType.*;

/**
 * This class is for modelling different robot types. I decided not to use inheritence from an abstract Robot
 * super class, since this would make queries complex. Instead, each robot carries this as a type attribute.
 */
public enum RobotType {
    SCOUT( "Scout" ),
    MINER( "Miner" ),
    WARRIOR( "Warrior" );

    private final String stringValue;

    private RobotType( String s ) {
        stringValue = s;
    }

    /**
     * The target levels for upgrading robots
     */
    protected static final Map<RobotType, Map<CapabilityType, Integer>> TARGET_LEVELS = Map.of(
        SCOUT, Map.of(),
        MINER, Map.of( MINING, 2, STORAGE, 2 ),
        WARRIOR, Map.of( DAMAGE, 2, HEALTH, 2 ) );

    public Map<CapabilityType, Integer> targetLevels() {
        return TARGET_LEVELS.get( this );
    }


    /**
     * The quotas (as % values) for the different robot types
     */
    protected static final Map<RobotType, Long> QUOTA = Map.of(
            SCOUT, 50L,
            MINER, 30L,
            WARRIOR, 20L );

    public Long quota() {
        return QUOTA.get( this );
    }


    @Override
    public String toString() {
        return this.stringValue;
    }
}

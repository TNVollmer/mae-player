package thkoeln.dungeon.player.core.domainprimitives.location;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum to represent different types of mineable resources
 */
public enum MineableResourceType {
    //TODO: Check level for resources
    COAL(0),
    IRON(1),
    GEM(2),
    GOLD(3),
    PLATIN(4);

    private final Integer miningLevel;

    MineableResourceType(Integer level) {
        this.miningLevel = level;
    }

    public Integer getNeededMiningLevel() {
        return miningLevel;
    }
}

package thkoeln.dungeon.player.core.domainprimitives.location;

import lombok.Getter;

/**
 * Enum to represent different types of mineable resources
 */
@Getter
public enum MineableResourceType {
    //TODO: Check level for resources
    COAL(0),
    IRON(1),
    GEM(2),
    GOLD(3),
    PLATIN(4);

    private final Integer requiredMiningLevel;

    MineableResourceType(Integer level) {
        this.requiredMiningLevel = level;
    }

    public boolean canMineBeMinedBy(Integer level) {
        return this.requiredMiningLevel <= level;
    }

    public boolean canMineBetterResources(Integer level) {
        return this.requiredMiningLevel < level;
    }

    public static MineableResourceType getBestType(Integer level) {
        return switch (level) {
            case 0 -> COAL;
            case 1 -> IRON;
            case 2 -> GEM;
            case 3 -> GOLD;
            default -> PLATIN;
        };
    }
}

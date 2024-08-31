package thkoeln.dungeon.player.core.domainprimitives.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum CompassDirection {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public CompassDirection getOppositeDirection() {
        return switch (this) {
            case NORTH -> SOUTH;
            case EAST -> WEST;
            case SOUTH -> NORTH;
            case WEST -> EAST;
        };
    }

    public int xOffset() {
        return switch (this) {
            case NORTH, SOUTH -> 0;
            case EAST -> 1;
            case WEST -> -1;
        };
    }

    public int yOffset() {
        return switch (this) {
            case NORTH -> 1;
            case EAST, WEST -> 0;
            case SOUTH -> -1;
        };
    }

    public List<CompassDirection> ninetyDegrees() {
        List<CompassDirection> retVals = new ArrayList<>();
        switch (this) {
            case NORTH:
                retVals.add(WEST);
                retVals.add(EAST);
                break;
            case EAST:
                retVals.add(NORTH);
                retVals.add(SOUTH);
                break;
            case SOUTH:
                retVals.add(WEST);
                retVals.add(EAST);
                break;
            case WEST:
                retVals.add(NORTH);
                retVals.add(SOUTH);
        }
        return retVals;
    }

    public static CompassDirection random() {
        Random random = new Random();
        return CompassDirection.values()[random.nextInt(4)];
    }

    public String toStringShort() {
        return toString().substring(0, 1);
    }
}
